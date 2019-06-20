package com.dangjia.acg.modle.menu;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Ruking.Cheng
 * @descrilbe 菜单配置表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/23 5:31 PM
 */
@Data
@Entity
@Table(name = "dj_menu_configuration")
@ApiModel(description = "菜单配置表")
@FieldNameConstants(prefix = "")
public class MenuConfiguration extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "菜单名称")
    @ApiModelProperty("菜单名称")
    private String name;

    @Column(name = "parent_id")
    @Desc(value = "菜单父级ID")
    @ApiModelProperty("菜单父级ID")
    private String parentId;

    @Column(name = "image")
    @Desc(value = "菜单图片")
    @ApiModelProperty("菜单图片")
    private String image;

    @Column(name = "url")
    @Desc(value = "跳转URL")
    @ApiModelProperty("跳转URL")
    private String url;

    @Column(name = "apiUrl")
    @Desc(value = "异步加载接口获取图标状态")
    @ApiModelProperty("异步加载接口获取图标状态")
    private String apiUrl;//统一返回格式：{"image":"图标地址","state":"0无 1有点","num":"点数量"}

    @Column(name = "type")
    @Desc(value = "0:跳转URL，1:获取定位后跳转URL，2:量房，3：传平面图，4：传施工图")
    @ApiModelProperty("0:跳转URL，1:获取定位后跳转URL,2:量房，3：传平面图，4：传施工图")
    private Integer type;

    @Column(name = "menu_type")
    @Desc(value = "菜单类型：0:施工页面，1:工匠我的页面，2:装修设计阶段，3:装修精算阶段，4:装修施工阶段")
    @ApiModelProperty("菜单类型：0:施工页面，1:工匠我的页面")
    private Integer menuType;

    @Column(name = "show_designer")
    @Desc(value = "设计师是否显示：0:不显示，1:显示")
    @ApiModelProperty("设计师是否显示：0:不显示，1:显示")
    private Integer showDesigner;

    @Column(name = "show_actuaries")
    @Desc(value = "精算师是否显示：0:不显示，1:显示")
    @ApiModelProperty("精算师是否显示：0:不显示，1:显示")
    private Integer showActuaries;

    @Column(name = "show_housekeeper")
    @Desc(value = "大管家是否显示：0:不显示，1:显示")
    @ApiModelProperty("大管家是否显示：0:不显示，1:显示")
    private Integer showHousekeeper;

    @Column(name = "show_craftsman")
    @Desc(value = "工匠是否显示：0:不显示，1:显示")
    @ApiModelProperty("工匠是否显示：0:不显示，1:显示")
    private Integer showCraftsman;

    @Column(name = "show_type")
    @Desc(value = "完工是否显示：0:不显示，1:显示")
    @ApiModelProperty("完工是否显示：0:不显示，1:显示")
    private Integer showType;

    @Column(name = "show_proprietor")
    @Desc(value = "业主端是否显示：0:不显示，1:显示")
    @ApiModelProperty("业主端是否显示：0:不显示，1:显示")
    private Integer showProprietor;

    @Column(name = "show_payment")
    @Desc(value = "未支付是否显示：0:不显示，1:显示")
    @ApiModelProperty("未支付是否显示：0:不显示，1:显示")
    private Integer showPayment;

    @Column(name = "sort")
    @Desc(value = "优先顺序")
    @ApiModelProperty("优先顺序")
    private Integer sort;

    public MenuConfiguration() {
        this.sort = 99;
    }

    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String imageAddress, String webAddress) {
        initPath(imageAddress, webAddress, null, null, null);
    }

    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String imageAddress, String webAddress, String houseId, String houseFlowId, Integer profession) {
        StringBuilder data = new StringBuilder();
        if (!CommonUtil.isEmpty(houseId)) {
            data.append("&houseId=").append(houseId);
        }
        if (!CommonUtil.isEmpty(houseFlowId)) {
            data.append("&houseFlowId=").append(houseFlowId);
        }
        if (!CommonUtil.isEmpty(profession)) {
            data.append("&profession=").append(profession);
        }
        this.image = CommonUtil.isEmpty(this.image) ? null : imageAddress + this.image;
        this.url = CommonUtil.isEmpty(this.url) ? null : webAddress + this.url + "?title=" + name + data.toString();
    }
}