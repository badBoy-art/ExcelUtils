package cn.gzhu.test.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhaoxuedui <zhaoxuedui@kuaishou.com>
 * Created on 2020-01-16
 * @Description
 */
@Getter
@AllArgsConstructor
@ToString
public enum ExcelType {
    EXCEL_2003(".xls"), EXCEL_2007(".xlsx");

    private String suffix;

}
