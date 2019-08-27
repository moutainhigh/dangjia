package com.dangjia.acg.dto.sale.royalty;

import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.royalty.DjAreaMatch;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import java.util.List;

@Data
@Entity
@ApiModel(description = "楼栋DTO")
@FieldNameConstants(prefix = "")
public class ResidentialBuildingDTO {

    List<ResidentialBuilding> rb;

    List<DjAreaMatch> list;
}
