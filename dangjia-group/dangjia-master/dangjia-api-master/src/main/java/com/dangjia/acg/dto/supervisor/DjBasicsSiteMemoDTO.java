package com.dangjia.acg.dto.supervisor;

import lombok.Data;

@Data
public class DjBasicsSiteMemoDTO {
    private String id; //备忘录表id
    private String remark; //备注
    private String createDate; //创建时间
    private String workerTypeId; //工种类型：【1设计师，2精算师，3大管家,4拆除，5督导，6水电工，7防水，8泥工,9木工，10油漆工，11安装,】
    private String workerTypeName; //工种名称
    private String name;// 姓名
    private String isSelfCreate;//是否是自己创建(1:是 0：他人提醒查看)
    private String head;//头像

}
