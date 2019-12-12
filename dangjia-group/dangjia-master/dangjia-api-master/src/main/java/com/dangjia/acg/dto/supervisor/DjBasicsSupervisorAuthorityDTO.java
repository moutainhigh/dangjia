package com.dangjia.acg.dto.supervisor;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class DjBasicsSupervisorAuthorityDTO {


    private String houseId ;


    private String address ;


    private String memberId ;


    private String name ;


    private String mobile ;

    private String visitState ;


    private String constructionDate ;
}
