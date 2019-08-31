package com.dangjia.acg.dto.sale.royalty;

import com.dangjia.acg.modle.sale.royalty.DjAreaMatchSetup;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Data
@Entity
@ApiModel(description = "楼栋提成详情list")
@FieldNameConstants(prefix = "")
public class DjAreaMatchSetupDTO {

    List<DjAreaMatchSetup> djAreaMatchSetups;


    List<Map<Object,Object>> buildingList;

    List<Map<Object,Object>> villageList;
}
