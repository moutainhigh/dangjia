package com.dangjia.acg.dto.label;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/17
 * Time: 11:00
 */
@Data
public class OptionalLabelDTO{

    private String id;
    private String labelName;
    private String status;//是否选配标签 0：选配 ，1:不选配
}
