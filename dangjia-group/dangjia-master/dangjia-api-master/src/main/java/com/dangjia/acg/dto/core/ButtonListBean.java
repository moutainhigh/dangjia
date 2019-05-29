package com.dangjia.acg.dto.core;

import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe 按钮
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/28 6:14 PM
 */

@Data
public class ButtonListBean {
    private String url;
    private int buttonType;//0:跳转URL，主按钮提示1：巡查工地2：申请业主验收；3:确认开工--主按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
    private String buttonTypeName;//主按钮提示 巡查工地;申请业主验收;确认开工--主按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
}
