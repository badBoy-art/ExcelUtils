package cn.gzhu.test.stream.base;

import cn.gzhu.test.constant.CommonConstants;
import cn.gzhu.test.stream.Column;
import cn.gzhu.test.stream.ExcelContent;
import cn.gzhu.test.stream.ExcelImporterInfo;
import cn.gzhu.test.stream.ExcelMetaCache;
import cn.gzhu.test.stream.ExcelStreamExportUtils;
import cn.gzhu.test.stream.ExcelVersion;
import cn.gzhu.test.stream.Message;
import cn.gzhu.test.stream.ServiceException;
import cn.gzhu.test.utils.Safes;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.gzhu.test.constant.CommonConstants.ONE_INT;
import static java.util.stream.Collectors.toList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 */
@Slf4j
public abstract class BaseExcelImporter<T> implements ExcelConsumer<T> {
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Column[] COLUMNS = new Column[]{Column.create("错误信息", 300)};

    private static final int MAX_SIZE = 200;

    private static final int CORE_SIZE = 200;

    private static final int KEEP_ALIVE_TIME = 10;

    private static final List<String> ERRORS = Lists.newArrayList();

    private static final ConcurrentLinkedQueue<String> SYSTEM_ERRORS = new ConcurrentLinkedQueue<>();

    private static final AtomicBoolean IS_COMPLETED = new AtomicBoolean(false);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private ExcelMetaCache metaCache;

    public abstract ExcelContent<T> getContent();

    Gson gson = new Gson();

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR =
            new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(MAX_SIZE), new CallerRunsPolicy());

    public Message<ExcelImporterInfo> execute() {
        try {
            try {
                // 默认使用内存缓存excel字典表信息
                metaCache = new LocalExcelMetaCache();

                ExcelContent<T> content = getContent();

                OPCPackage pkg;
                if (content.getMultipartFile() != null) {
                    baseCheckParam(content.getMultipartFile());
                    buildExcelImporterContent(content);
                    // 文件如果太大,就切换成磁盘存储,免得撑爆内存
                    if (content.getMultipartFile().getSize() > MAX_FILE_SIZE) {
                        metaCache = new DiskExcelMetaCache();
                    }
                    log.info("ExcelStreamImporter cache use {}", metaCache.getClass().getSimpleName());
                    pkg = OPCPackage.open(content.getMultipartFile().getInputStream());
                } else if (StringUtils.isNotBlank(content.getLocalFilePath())) {
                    pkg = OPCPackage.open(content.getLocalFilePath(), PackageAccess.READ);
                } else {
                    throw new IllegalArgumentException("ExcelContent必须构建,要指定用哪个对象实例去接值,以及指定excel文件来源!");
                }

                // 流式解析excel字典表信息
                parseSharedXMLToCache(pkg);

                // 流式解析excel,仅解析第一个sheet
                parseFirstSheetXML(content.getFieldClass(), pkg);
            } catch (IllegalArgumentException e) {
                log.error("ExcelStreamImporter exceute failed, error.", e);
                throw ServiceException.ofMessage(ExcelErrorCodeEnum.EXCEL_ERROR.getCode(), e.getMessage());
            } catch (ExcelCheckException e) {
                ExcelCheckException.buildErrorInfoToContent(e);
            } catch (InvalidFormatException | IOException e) {
                log.error("ExcelStreamImporter execute failed error.", e);
                throw ServiceException
                        .ofMessage(ExcelErrorCodeEnum.EXCEL_ERROR.getCode(), Throwables.getStackTraceAsString(e));
            } finally {
                metaCache.clear();
            }
            List<String> errors = Safes.of(ExcelStreamImporterContext.getErrors()).stream()
                    .filter(Objects::nonNull).collect(toList());
            if (CollectionUtils.isEmpty(errors)) {
                return Message.ok(ExcelStreamImporterContext.get());
            }
            return Message.error(ExcelStreamImporterContext.get(), ExcelErrorCodeEnum.EXCEL_ERROR.getCode(),
                    ExcelErrorCodeEnum.EXCEL_ERROR.getMsg());
        } catch (ServiceException e) {
            ExcelStreamImporterContext.get().getErrors().add(e.getOverrideMessage());
            SYSTEM_ERRORS.add(e.getOverrideMessage());
            log.error("ExcelStreamImporter execute failed error. msg:{}", e.getOverrideMessage());
            return Message.error(ExcelStreamImporterContext.get(), ExcelErrorCodeEnum.EXCEL_ERROR.getCode(),
                    ExcelErrorCodeEnum.EXCEL_ERROR.getMsg());

        } catch (Exception e) {
            ExcelStreamImporterContext.get().getErrors().add(Throwables.getStackTraceAsString(e));
            SYSTEM_ERRORS.add(Throwables.getStackTraceAsString(e));
            log.error("ExcelStreamImporter execute failed error.", e);
            return Message.error(ExcelStreamImporterContext.get(), ExcelErrorCodeEnum.EXCEL_ERROR.getCode(),
                    ExcelErrorCodeEnum.EXCEL_ERROR.getMsg());
        } finally {
            ERRORS.addAll(ExcelStreamImporterContext.getErrors());
            IS_COMPLETED.set(true);
            ExcelStreamImporterContext.remove();
        }
    }

    public void executeWithDownload() {
        THREAD_POOL_EXECUTOR.submit(this::execute);
        try {
            if (!IS_COMPLETED.get()) {

                String originalFilename = Optional.ofNullable(getContent()).map(ExcelContent::getMultipartFile)
                        .map(MultipartFile::getOriginalFilename).orElse(StringUtils.EMPTY);
                String exportFileName = "UPLOAD-ERROR-" + DATE_FORMAT.format(new Date());
                ExcelStreamExportUtils exporter = new ExcelStreamExportUtils(ExcelVersion.E2003, "sheet1", COLUMNS,
                        getContent().getResponse(), exportFileName);
                try {
                    while (!IS_COMPLETED.get() && CollectionUtils.isEmpty(SYSTEM_ERRORS)) {
                        exporter.append(Lists.newArrayList("======= pending ======="));
                        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
                    }
                    exporter.append(Lists.newArrayList("↓↓↓↓↓↓ 以下为全部错误信息 ↓↓↓↓↓↓"));
                    List<String> errorInfos =
                            Safes.of(ERRORS).stream().filter(StringUtils::isNotBlank).collect(toList());
                    if (CollectionUtils.isNotEmpty(errorInfos)) {
                        for (String error : errorInfos) {
                            log.info("error:{}", error);
                            exporter.append(Lists.newArrayList(error));
                        }
                    } else {
                        exporter.append(Lists.newArrayList("======= 全部导入成功! ======="));
                    }
                } finally {
                    exporter.append(Lists.newArrayList("↑↑↑↑↑↑ 以上为全部错误信息 ↑↑↑↑↑↑"));
                    log.info("executeWithDownload execution completed, uploadFileName:{},exportErrorFileName:{}",
                            originalFilename, exportFileName);
                    exporter.close();
                }
            }
        } catch (Exception e) {
            log.error("excel error export failed, error.", e);
        } finally {
            SYSTEM_ERRORS.clear();
            ERRORS.clear();
            IS_COMPLETED.set(false);
        }
    }

    private void buildExcelImporterContent(ExcelContent<T> content) {
        ExcelStreamImporterContext.get()
                .setContentType(content.getMultipartFile().getContentType());
        ExcelStreamImporterContext.get()
                .setOriginalFilename(content.getMultipartFile().getOriginalFilename());
        ExcelStreamImporterContext.get().setSize(content.getMultipartFile().getSize());
    }

    private void baseCheckParam(MultipartFile multipartFile) {
        checkExcelCell(multipartFile.getSize() != 0, StringUtils.EMPTY, "当前Excel没有内容!请补充内容后上传!");
        List<String> list = CommonConstants.SPLITTER_POINT.splitToList(multipartFile.getOriginalFilename());
        String fileSuffix = list.get(list.size() - ONE_INT);
        checkExcelCell(StringUtils.isNotBlank(multipartFile.getOriginalFilename()), StringUtils.EMPTY,
                "请选择正确的Excel上传!");
        checkExcelCell(CommonConstants.EXCEL_XLSX.equals(fileSuffix), StringUtils.EMPTY, "请将Excel另存为.xlsx格式上传!");
    }

    private void parseFirstSheetXML(T fieldClass, OPCPackage pkg) {
        try {
            XSSFReader r = new XSSFReader(pkg);
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            ExcelSheetHandler<T> excelSheetHandler = new ExcelSheetHandler<>(fieldClass, this.metaCache, this);
            parser.setContentHandler(excelSheetHandler);
            InputStream sheet2InputStream = r.getSheetsData().next();
            InputSource sheetSource = new InputSource(sheet2InputStream);
            parser.parse(sheetSource);
            sheet2InputStream.close();
        } catch (ExcelCheckException e) {
            ExcelCheckException.buildErrorInfoToContent(e);
        } catch (IOException | SAXException | OpenXML4JException e) {
            log.error("ExcelStreamImporter parseFirstSheetXML failed error.", e);
            throw ServiceException.ofMessage(ExcelErrorCodeEnum.EXCEL_ERROR.getCode(), Throwables.getStackTraceAsString(e));
        }
    }

    private void parseSharedXMLToCache(OPCPackage pkg) throws IOException {

        ArrayList<PackagePart> packageParts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        PackagePart first = Safes.first(packageParts);
        InputStream sharedStringTableInputStream = first.getInputStream();
        SharedStringsTableHandler sharedStringsTableHandler = new SharedStringsTableHandler(metaCache);

        InputSource inputSource = new InputSource(sharedStringTableInputStream);
        try {
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            saxFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            saxFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            XMLReader xmlReader = saxFactory.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(sharedStringsTableHandler);
            xmlReader.parse(inputSource);
            metaCache.putFinished();
            sharedStringTableInputStream.close();
        } catch (ExcelCheckException e) {
            ExcelCheckException.buildErrorInfoToContent(e);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            log.error("ExcelStreamImporter parseFirstSheetXML failed error.", e);
            throw ServiceException.ofMessage(ExcelErrorCodeEnum.EXCEL_ERROR.getCode(), Throwables.getStackTraceAsString(e));
        } finally {
            if (sharedStringTableInputStream != null) {
                try {
                    sharedStringTableInputStream.close();
                } catch (IOException e) {
                    log.error("ExcelStreamImporter parseSharedXMLToCache close failed error.", e);
                }
            }
        }
    }

    public void checkExcelCell(boolean expression, String errorRowName, Object errorMessage) {
        if (!expression) {
            throw new ExcelCheckException(errorRowName, String.valueOf(errorMessage));
        }
    }

    public void checkExcelCell(boolean expression, String errorRowName, String errorRowNum, Object errorMessage) {
        if (!expression) {
            throw new ExcelCheckException(errorRowName, errorRowNum, String.valueOf(errorMessage));
        }
    }

    public void checkExcelCell(boolean expression, String errorRowName, long errorRowNum, Object errorMessage) {
        String errorNum;
        if (errorRowNum == NumberUtils.LONG_ZERO) {
            errorNum = StringUtils.EMPTY;
        } else {
            errorNum = String.valueOf(errorRowNum);
        }
        if (!expression) {
            throw new ExcelCheckException(errorRowName, errorNum, String.valueOf(errorMessage));
        }
    }

    public void checkExcelFile(boolean expression, Object errorMessage) {
        if (!expression) {
            log.warn("checkExcelFile failed! msg:{}", gson.toJson(errorMessage));
            throw ServiceException.ofMessage(ExcelErrorCodeEnum.EXCEL_ERROR.getCode(), String.valueOf(errorMessage));
        }
    }

    @SuppressWarnings("unchecked")
    public T getInstance(T rowObj) {
        if (rowObj == null) {
            return null;
        }
        try {
            T instance = (T) rowObj.getClass().newInstance();
            if (instance.hashCode() == rowObj.hashCode() && instance.equals(rowObj)) {
                return getInstance(rowObj);
            } else {
                return instance;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("batchAppendRow failed!", e);
            throw ServiceException.ofMessage(ExcelErrorCodeEnum.SERVER_ERROR.getCode(), "batchAppendRow failed");
        }
    }
}
