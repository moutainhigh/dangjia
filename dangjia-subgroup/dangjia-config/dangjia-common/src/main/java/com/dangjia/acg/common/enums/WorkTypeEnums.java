package com.dangjia.acg.common.enums;

/**
 * 工序类型枚举
 *
 * @author: ysl
 * @date: 2018-12-25 11:44
 */
public enum WorkTypeEnums implements IBaseEnum {

    // 1:设计，2：精算， 3: 大管家 ，4：拆除 ，5：  ，6：水电 ，7：泥工 ，8：木工 ，9：油漆
    SHEJI(1, "设计"),
    JINGSUAN(2, "精算"),
    DAGUANJIA(3, "大管家"),
    CHAICHU(4, "拆除"),
    FANGSHUI(5, "防水"),
    SHUIDIAN(6, "水电"),
    NIGONG(7, "泥工"),
    MUGONG(8, "木工"),
    YOUQI(9, "油漆");

    public static WorkTypeEnums getInstance(int code) {
        for (WorkTypeEnums entity : WorkTypeEnums.values()) {
            if (entity.getCode() == code) {
                return entity;
            }
        }
        return null;
    }

    WorkTypeEnums(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    private int code;

    private String desc;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
