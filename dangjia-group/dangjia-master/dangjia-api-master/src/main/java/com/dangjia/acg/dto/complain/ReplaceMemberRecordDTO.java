package com.dangjia.acg.dto.complain;
import lombok.Data;
import java.util.Date;

/**
 * 退换工匠历史记录返回体
 */
@Data
public class ReplaceMemberRecordDTO {
    protected String name;
    private Date createDate;
    private String mobile;


}
