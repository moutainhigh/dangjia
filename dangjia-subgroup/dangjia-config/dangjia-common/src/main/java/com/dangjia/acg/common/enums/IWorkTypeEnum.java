package com.dangjia.acg.common.enums;

public enum IWorkTypeEnum implements IBaseEnum {
    /**
     * 工种类型：【1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装】
     */
    DESIGNER(1, "设计师"),
    ACTUARY(2, "精算师"),
    BIG_HOUSEKEEPER(3, "大管家"),
    TEAR_DOWN(4, "拆除"),
    PUNCH(5, "打孔"),
    PLUMBER(6, "水电工"),
    WATERPROOF(7, "防水"),
    MASON(8, "泥工"),
    WOODWORKING(9, "木工"),
    PAINTER(10, "油漆工"),
    INSTALLATION(11, "安装"),
    ;


    private int code;
    private String desc;

    IWorkTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getValue(Integer code) {
        for (IWorkTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele.getDesc();
        }
        return null;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
