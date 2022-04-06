package cn.gzhu.test.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/5 10:54
 */
public class XlsBuilder implements IExcelBuilder {

    private static final String EXCEL_HEADER = "<?xml version=\"1.0\"?>\n"
            +
            "<?mso-application progid=\"Excel.Sheet\"?>\n"
            +
            "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n"
            +
            " xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n"
            +
            " xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n"
            +
            " xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n"
            +
            " xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n";

    private static final String EXCEL_PROPERTIES =
            "<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">\n"
                    +
                    "  <Author>badBoy</Author>\n"
                    +
                    "  <Company>www.beike.com</Company>\n"
                    +
                    "  <Version>11.5606</Version>\n"
                    +
                    " </DocumentProperties>";

    private static final String EXCEL_STYLE = "<Styles>\n"
            +
            "  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n"
            +
            "   <Alignment ss:Vertical=\"Center\"/>\n"
            +
            "   <Borders/>\n"
            +
            "   <Font ss:FontName=\"宋体\" x:CharSet=\"134\" ss:Size=\"12\"/>\n"
            +
            "   <Interior/>\n"
            +
            "   <NumberFormat/>\n"
            +
            "   <Protection/>\n"
            +
            "  </Style>\n"
            +
            " </Styles>";

    private static final String EXCEL_BOTTOM = "</Workbook>";

    private XmlWorkbook workbook;

    private XlsBuilder(OutputStream os) {
        workbook = new XmlWorkbook(os);
    }

    public static IExcelBuilder newExcel(OutputStream os) throws IOException {
        XlsBuilder excelBuilder = new XlsBuilder(os);
        excelBuilder.createHeader();
        return excelBuilder;
    }

    @Override
    public IExcelSheetBuilder newSheet(String sheetTitle) throws IOException {
        return new XlsSheetBuilder(workbook, sheetTitle);
    }

    @Override
    public void flushAndClose() throws IOException {
        appendBottom();
        workbook.flush();
        workbook.close();
    }

    private void createHeader() throws IOException {
        flushContent(EXCEL_HEADER);
        flushContent(EXCEL_PROPERTIES);
        flushContent(EXCEL_STYLE);
    }

    private void appendBottom() throws IOException {
        flushContent(EXCEL_BOTTOM);
    }

    private void flushContent(String content) throws IOException {
        workbook.write(content);
    }

    private void batchFlushContent(List<String> contents) throws IOException {
        workbook.batchWrite(contents);
    }
}
