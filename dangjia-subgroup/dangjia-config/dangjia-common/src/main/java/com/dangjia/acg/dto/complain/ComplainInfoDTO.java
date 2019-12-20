package com.dangjia.acg.dto.complain;

import lombok.Data;

import java.util.List;


/**
 * author: ljl
 */
@Data
public class ComplainInfoDTO {

    private List<String> changeList;//更换原因
    private List<String> imageList;//图片
    private String rejectReason;//驳回原因


}
