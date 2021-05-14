package cn.gzhu.test.pojo;

import cn.gzhu.test.anno.ExcleColumn;
import cn.gzhu.test.anno.ExcleSheet;
import cn.gzhu.test.constant.ExcelColumType;
import cn.gzhu.test.constant.ExcelType;
import lombok.Data;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;

/**
 * 描述：考生excel模型
 */
@Data
@ExcleSheet(exportFileName = "poi.pv.photo", ignoreOnlyHaveNoRow = -1, startIndex = 0, sheetName = "myTest", excellType = ExcelType.EXCEL_2007)
public class ExamineeGradeExcelModel {
    @ExcleColumn(index = 0, javaType = ExcelColumType.STRING, name = "poiId")
    private String poiId;
    @ExcleColumn(index = 1, javaType = ExcelColumType.STRING, name = "poiName")
    private String poiName;
    @ExcleColumn(index = 2, javaType = ExcelColumType.STRING, name = "pv")
    private String pv;
    @ExcleColumn(index = 3, javaType = ExcelColumType.STRING, name = "视频数")
    private String photoNmus;

}