package cn.gzhu.test.pojo;

import cn.gzhu.test.anno.ExcleColumn;
import cn.gzhu.test.anno.ExcleSheet;
import cn.gzhu.test.constant.ExcelColumType;
import cn.gzhu.test.constant.ExcelType;
import lombok.Data;

/**
 * @author zhaoxuedui <zhaoxuedui@kuaishou.com>
 * Created on 2021-03-18
 * @Description
 */
@Data
@ExcleSheet(titleIndex = 0, startIndex = 1, importBlankRow = false, ignoreOnlyHaveNoRow = 0, excellType = ExcelType.EXCEL_2007)
public class PoiExcelModel {
    @ExcleColumn(index = 0, javaType = ExcelColumType.STRING)
    private String poiId;
    @ExcleColumn(index = 1, javaType = ExcelColumType.STRING)
    private String poiName;
    @ExcleColumn(index = 2, javaType = ExcelColumType.STRING)
    private String pv;
}
