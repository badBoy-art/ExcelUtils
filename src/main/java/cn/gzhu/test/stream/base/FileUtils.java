package cn.gzhu.test.stream.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.TempFile;

import java.io.File;
import java.util.UUID;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 15:12
 */
@Slf4j
public class FileUtils {
    public static final String POI_FILES = "poifiles";

    public static final String EX_CACHE = "excache";

    private static String tempFilePrefix =
            System.getProperty(TempFile.JAVA_IO_TMPDIR) + File.separator + UUID.randomUUID().toString()
                    + File.separator;

    private static String poiFilesPath = tempFilePrefix + POI_FILES + File.separator;

    private static String cachePath = tempFilePrefix + EX_CACHE + File.separator;

    private FileUtils() {
    }

    static {
        // Create a temporary directory in advance
        File tempFile = new File(tempFilePrefix);
        createDirectory(tempFile);
        tempFile.deleteOnExit();
        // Initialize the cache directory
        File cacheFile = new File(cachePath);
        createDirectory(cacheFile);
        cacheFile.deleteOnExit();
    }

    public static File createCacheTmpFile() {
        log.info("--> file path:{}", cachePath + UUID.randomUUID().toString());
        return createDirectory(new File(cachePath + UUID.randomUUID().toString()));
    }

    private static File createDirectory(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalArgumentException("Cannot create directory:" + directory.getAbsolutePath());
        }
        return directory;
    }

    public static String getTempFilePrefix() {
        return tempFilePrefix;
    }

    public static void setTempFilePrefix(String tempFilePrefix) {
        FileUtils.tempFilePrefix = tempFilePrefix;
    }

    public static String getPoiFilesPath() {
        return poiFilesPath;
    }

    public static void setPoiFilesPath(String poiFilesPath) {
        FileUtils.poiFilesPath = poiFilesPath;
    }

    public static String getCachePath() {
        return cachePath;
    }

    public static void setCachePath(String cachePath) {
        FileUtils.cachePath = cachePath;
    }
}
