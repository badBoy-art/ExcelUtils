package cn.gzhu.test.pojo;

import cn.gzhu.test.anno.ExcleColumn;
import cn.gzhu.test.anno.ExcleSheet;
import cn.gzhu.test.constant.ExcelColumType;
import cn.gzhu.test.constant.ExcelType;
import lombok.Data;

/**
 * @author badBoy <badBoy>
 * Created on 2021-05-14
 * @Description
 */
@Data
@ExcleSheet(titleIndex = 0, startIndex = 1, importBlankRow = false, ignoreOnlyHaveNoRow = 0, excellType = ExcelType.EXCEL_2007)
public class PoiPhotoModel {
    @ExcleColumn(index = 0, javaType = ExcelColumType.STRING)
    private String poiId;
    @ExcleColumn(index = 1, javaType = ExcelColumType.STRING)
    private String photoNums;
}
