package com.dangjia.acg.dto.other;
import lombok.Data;
import java.util.Date;
@Data
public class ClueDTO {

    protected String id;
    protected Date createDate;// 创建日期
    protected Date modifyDate;// 修改日期
    protected int dataStatus;
    private String owername;
    private String phone;
    private String wechat;
    private String address;
    private int stage;
    private String cusService;
    private String labelId;
    private String labelName;
    private String[][] labelIds;
}
