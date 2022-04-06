package cn.gzhu.test.stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 * <p>
 * 构造一个xml格式的sheet, 形如:
 * <Worksheet ss:Name="Sheet1">
 * <Table ss:ExpandedColumnCount="5" ss:ExpandedRowCount="6" x:FullColumns="1" x:FullRows="1" ss:DefaultColumnWidth="54"
 * ss:DefaultRowHeight="14.25">
 * <Row ss:AutoFitHeight="0">
 * <Cell><Data ss:Type="Number">11</Data></Cell>
 * <Cell><Data ss:Type="Number">232</Data></Cell>
 * <Cell><Data ss:Type="String">三地风俗</Data></Cell>
 * <Cell ss:Index="5"><Data ss:Type="String">三地风俗</Data></Cell>
 * </Row>
 * </Table>
 * </Worksheet>
 * <p/>
 * 调用方法 ：
 * <p/>
 * XlsSheetBuilder builder = new XlsSheetBuilder() builder.appendHeader().appendContent().appendContent()...end()
 */
public class XlsSheetBuilder implements IExcelSheetBuilder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String TABLE_START = "<Table ss:DefaultColumnWidth=\"54\" ss:DefaultRowHeight=\"14.25\">";

    private static final String TABLE_END = "</Table>";

    // 2003版excel每一页最大展示行数65535
    private static final int MAX_ROW_NUMBER = 60000;
    private int currentSheetNum = 1;
    private int lastRowIndex = 0;
    // 记录客户端写入的表头, 用于翻页时使用
    private String headers;
    private String sheetTitle;
    private XmlWorkbook workbook;

    public XlsSheetBuilder(XmlWorkbook workbook, String sheetTitle) throws IOException {
        this.workbook = workbook;
        this.sheetTitle = sheetTitle;
        createHeader();
    }

    @Override
    public IExcelSheetBuilder appendHeader(Column... headersParam) throws IOException {
        StringBuilder sb = new StringBuilder();

        // 记录表头每一列的属性
        for (int i = 0; i < headersParam.length; i++) {
            if (headersParam[i].getWidth() > 0) {
                sb.append("<Column ss:Index=\"").append(i + 1)
                        .append("\" ss:StyleID=\"Default\" ss:AutoFitWidth=\"0\" ss:Width=\"")
                        .append(headersParam[i].getWidth()).append("\"/>");
            }
        }

        sb.append("<Row ss:AutoFitHeight=\"0\">");
        for (Column column : headersParam) {
            sb.append(createCell(column.getName()));
        }
        sb.append("</Row>");

        // 记录客户端写入的表头
        headers = sb.toString();
        flushContent(headers);
        return this;
    }

    @Override
    public IExcelSheetBuilder appendContent(List<Object> excelRow) throws IOException {
        rotateSheet();
        StringBuilder sb = new StringBuilder();
        sb.append("<Row ss:AutoFitHeight=\"0\">");
        for (Object obj : excelRow) {
            sb.append(createCell(obj == null ? "" : Objects.toString(obj)));
        }
        sb.append("</Row>");
        flushContent(sb.toString());
        lastRowIndex++;
        return this;
    }

    @Override
    public IExcelSheetBuilder batchAppendContent(List<List<Object>> excelRows) throws IOException {

        Preconditions.checkArgument(CollectionUtils.isNotEmpty(excelRows), "excelRows is null");
        Preconditions.checkArgument(excelRows.size() <= IExcelSheetBuilder.BATCH_MAX_SIZE,
                "excelRows.size must <= " + IExcelSheetBuilder.BATCH_MAX_SIZE);

        List<String> sbs = Lists.newArrayList();

        for (List<Object> excelRow : excelRows) {
            rotateSheet();
            StringBuilder sb = new StringBuilder();
            sb.append("<Row ss:AutoFitHeight=\"0\">");
            for (Object obj : excelRow) {
                sb.append(createCell(obj == null ? "" : Objects.toString(obj)));
            }
            sb.append("</Row>");
            sbs.add(sb.toString());
            lastRowIndex++;
        }
        batchFlushContent(sbs);
        return this;
    }

    @Override
    public void end() throws IOException {
        appendBottom();
    }

    private String createCell(String value) {
        return "<Cell><Data ss:Type=\"String\">" + StringEscapeUtils.escapeXml(value) + "</Data></Cell>";
    }

    private void createHeader() throws IOException {
        String extend = "";
        if (currentSheetNum > 1) {
            extend = "-" + currentSheetNum;
        }

        flushContent("<Worksheet ss:Name=\"" + this.sheetTitle + extend + "\">");
        flushContent(TABLE_START);
        currentSheetNum++;
    }

    private void appendBottom() throws IOException {
        flushContent(TABLE_END);
        flushContent("</Worksheet>");
    }

    private void flushContent(String content) throws IOException {
        workbook.write(content);
    }

    private void batchFlushContent(List<String> contents) throws IOException {
        workbook.batchWrite(contents);
    }

    private void rotateSheet() throws IOException {
        if (lastRowIndex >= MAX_ROW_NUMBER) {
            logger.info("sheet: {} 中数据条数超过60000, 新建sheet: {}", sheetTitle, sheetTitle + "-" + currentSheetNum);
            appendBottom();
            createHeader();
            flushContent(headers);
            lastRowIndex = 0;
        }
    }
}
