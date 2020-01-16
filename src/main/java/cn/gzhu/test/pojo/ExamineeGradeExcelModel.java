package cn.gzhu.test.pojo;

import cn.gzhu.test.anno.ExcleColumn;
import cn.gzhu.test.anno.ExcleSheet;
import cn.gzhu.test.constant.ExcelColumType;
import lombok.Data;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;

/**
 * 描述：考生excel模型
 */
@Data
@ExcleSheet(exportFileName = "excelUtil", ignoreOnlyHaveNoRow = -1, startIndex = 0, sheetName = "myTest")
public class ExamineeGradeExcelModel {

    @ExcleColumn(index = 0, javaType = ExcelColumType.IDENTITY, name = "序号")
    private Integer no;

    @ExcleColumn(index = 1, javaType = ExcelColumType.STRING, name = "考试账号")
    private String account;

    @ExcleColumn(index = 2, javaType = ExcelColumType.DATE, dateFormat = "yyyy-MM-dd HH:mm", name = "入场时间")
    private Date submitTime;

}