package com.dangjia.acg.dto.delivery;

import com.dangjia.acg.common.util.ImageUtil;
import lombok.Data;
import sun.plugin.com.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * 我要装修首页DTO
 * Date: 2019.10-30
 * Time: 下午 4:34
 */
@Data
public class WorkInFoDTO {

    //当前房子装修状态
    private Integer type;

    //订单状态
    private List<Map<String,Object>> mapList;

    //客服明细
    private Map<String,Object> map;

    //工序明细
//    private WorkNodeListDTO workList;

    private List<Object> workList;

    //今日播报
    private HouseFlowInfoDTO houseFlowInfoDTO;

    //订单状态
    private Map<String,Object> orderMap;

    //房子名称
    private String houseName;

    //房子id
    private String houseId;

    //1-下单后（销售阶段） 2-下单后（销售接单） 3-下单后（设计阶段）4-下单后（精算阶段）5-下单后(施工阶段)
    private Integer houseType;

    private List<BigListBean> bigList;//菜单

    @Data
    public static class BigListBean {
        private String name;
        private List<ListMapBean> listMap;
        public static  String[] shieji=new String[]{"ZX0001"};//设计阶段
        public static  String[] jingsuan=new String[]{"ZX0001","ZX0002"};//精算阶段
        public static  String[] shigong=new String[]{"ZX0001","ZX0002","ZX0003"};//施工阶段
        private HashMap<String, ListMapBean> beanBut = new HashMap<String, ListMapBean>(){
            {

                put("ZX0001",  new ListMapBean("设计图","iconWork/menus/zxy_icon_cangku@2x.png","","ZX0001"));
                put("ZX0002",  new ListMapBean("精算","iconWork/menus/zxy_icon_record@2x.png","","ZX0002"));
                put("ZX0003",  new ListMapBean("施工","iconWork/menus/zxy_icon_audit@2x.png","","ZX0003"));
            }
        };
        public  List<ListMapBean> getMenus(String imageAddress,String[] menusCodes){
            this.listMap=new LinkedList<>();
            for (String menusCode : menusCodes) {
                ListMapBean listMapBean=beanBut.get(menusCode);
                listMapBean.setImage(ImageUtil.getImageAddress(imageAddress,listMapBean.getImage()));
                this.listMap.add(listMapBean);
            }
            return this.listMap;
        }
        @Data
        public static class ListMapBean {
            public ListMapBean() {
            }
            public ListMapBean(String name,String image,String url,String type) {
                this.name =name;
                this.image = image;
                this.url= url;
                this.type= type;
            }
            private String image;//按钮图标
            private String name;//按钮名称
            private String url;
            /**
             *  施工首页-菜单按钮类型说明
             *      其他：1000：URL跳转
             *  菜单：
             *      ZX0001:设计图
             *      ZX0002:精算
             *      ZX0003:施工
             */
            private String type;//0:跳转URL，1:获取定位后跳转URL，2:量房，3：传平面图，4：传施工图
            private int state;//0无 1有点
            private int number;//点数量
        }
    }


}
