package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.UserAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.UserInfoResultDTO;
import com.dangjia.acg.dto.delivery.MaterialNumberDTO;
import com.dangjia.acg.dto.delivery.NodeNumberDTO;
import com.dangjia.acg.dto.delivery.WorkInFoDTO;
import com.dangjia.acg.dto.delivery.WorkNodeListDTO;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderItemMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderMapper;
import com.dangjia.acg.mapper.delivery.IBillHouseFlowMapper;
import com.dangjia.acg.mapper.delivery.IBillTechnologyRecordMapper;
import com.dangjia.acg.mapper.sale.IBillDjAlreadyRobSingleMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.sale.IBillUserMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.group.GroupUserConfig;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.modle.delivery.DjDeliverOrder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DjDeliverOrderService {
    @Autowired
    private IBillDjDeliverOrderMapper IBillDjDeliverOrderMapper;
    @Autowired
    private IBillDjAlreadyRobSingleMapper iBillDjAlreadyRobSingleMapper;
    @Autowired
    private IBillUserMapper iBillUserMapper;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private IBillMemberMapper iBillMemberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBillHouseFlowMapper iBillHouseFlowMapper;
    @Autowired
    private IBillTechnologyRecordMapper iBillTechnologyRecordMapper;
    @Autowired
    private IBillDjDeliverOrderItemMapper iBillDjDeliverOrderItemMapper;

    private static Logger logger = LoggerFactory.getLogger(DjDeliverOrderService.class);

    /**
     * 查询我要装修首页
     * @param houseId
     * @return
     */
    public ServerResponse queryOrderNumber(String houseId){

        //订单状态 1待付款，2已付款，3待收货
        WorkInFoDTO workInFoDTO = new WorkInFoDTO();
        Example example = new Example(Order.class);
        //待付款
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,1);
        Map<String,Object> map = new HashMap<>();
        map.put("one", IBillDjDeliverOrderMapper.selectCountByExample(example));

        //已付款
        example = new Example(Order.class);
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,2);
        map.put("two", IBillDjDeliverOrderMapper.selectCountByExample(example));

        //待收货
        example = new Example(Order.class);
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0)
                .andEqualTo(Order.ORDER_STATUS,3);
        map.put("three", IBillDjDeliverOrderMapper.selectCountByExample(example));
        map.put("four", 0);
        map.put("five", 0);
        List<Map<String,Object>> mapList = new ArrayList<>();
        mapList.add(map);
        workInFoDTO.setMapList(mapList);
        //查询今日播报信息
        List<String> strList = IBillDjDeliverOrderMapper.queryApplyDec();

        //查询房子工种类型
        example.createCriteria().andEqualTo(Order.HOUSE_ID,houseId)
                .andEqualTo(Order.DATA_STATUS, 0);
        List<HouseFlow> houseFlows = iBillHouseFlowMapper.selectByExample(example);

        //获取工序信息
        WorkNodeListDTO workNodeListDTO = summationMethod(houseFlows,houseId);


        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        //获取客服明细
        Example example1 = new Example(DjAlreadyRobSingle.class);
        example1.createCriteria()
                .andEqualTo(DjAlreadyRobSingle.HOUSE_ID, houseId)
                .andEqualTo(DjAlreadyRobSingle.DATA_STATUS, 0);
        List<DjAlreadyRobSingle> lists = iBillDjAlreadyRobSingleMapper.selectByExample(example1);
        if (!lists.isEmpty()) {
            String userid = lists.get(0).getUserId();
            example = new Example(MainUser.class);
            example.createCriteria().andEqualTo(MainUser.ID, userid);
            example.orderBy(GroupUserConfig.CREATE_DATE).desc();
            List<MainUser> list = iBillUserMapper.selectByExample(example);
            if (list != null && list.size() > 0) {
                MainUser user = list.get(0);
                map = new HashMap<>();
                map.put("id", user.getId());
                map.put("targetId", user.getId());
                UserInfoResultDTO userInfoResult = userAPI.getUserInfo(AppType.SALE.getDesc(), userid);
                if (userInfoResult != null && !CommonUtil.isEmpty(userInfoResult.getNickname())) {
                    map.put("nickName", "装修顾问 " + userInfoResult.getNickname());
                } else {
                    map.put("nickName", "装修顾问 小" + user.getUsername().substring(0, 1));
                }
                map.put("name", user.getUsername());
                map.put("mobile", user.getMobile());
                Member member1 = iBillMemberMapper.selectByPrimaryKey(user.getMemberId());
                if (member1 != null) {
                    member1.initPath(address);
                    map.put("head", member1.getHead());
                } else {
                    map.put("head", address + Utils.getHead());
                }
                workInFoDTO.setMap(map);
            }
        }
        return ServerResponse.createBySuccess("查询成功", map);
    }

    /**
     * 获取工序信息
     * @param houseId
     * @return
     */
    public WorkNodeListDTO summationMethod(List<HouseFlow> houseFlows,String houseId){
        WorkNodeListDTO workNodeListDTO = new WorkNodeListDTO();
        //查询工序节点
        List<NodeNumberDTO> nodeNumberDTOS = iBillDjDeliverOrderItemMapper.queryNodeNumber(houseId);
        //查询材料数量
        List<MaterialNumberDTO> materialNumberDTOS = iBillDjDeliverOrderItemMapper.queryMaterialNumber(houseId);

        List<Map<String,Object>> workList = new ArrayList<>();
        Map<String,Object> maps = new HashMap<>();
        //1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工
        for (HouseFlow houseFlow : houseFlows) {
            if(houseFlow.getWorkerType() == 1){
                maps = new HashMap<>();
                maps.put("workName", "设计师");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 1).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 1).count();
                maps.put("workStart",iStart);//获取工序已验收节点
                maps.put("workEnd",iEnd);//获取工序全部节点
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 1
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 1
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 1
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setOneList(workList);
            }else if(houseFlow.getWorkerType() == 2){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "精算师");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 2).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 2).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 2
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 2
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 2
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setTwoList(workList);
            }else if(houseFlow.getWorkerType() == 3){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "大管家");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 3).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 3).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 3
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 3
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 3
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setThreeList(workList);
            }else if(houseFlow.getWorkerType() == 4){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "拆除");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 4).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 4).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 4
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 4
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 4
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setFourList(workList);
            }else if(houseFlow.getWorkerType() == 6){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "水电工");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 6).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 6).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 6
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 6
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 6
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setSixList(workList);
            }else if(houseFlow.getWorkerType() == 7){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "防水");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 7).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 7).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 7
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 7
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 7
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setSevenList(workList);
            }else if(houseFlow.getWorkerType() == 8){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "泥工");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 8).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 8).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 8
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 8
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 8
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setEightList(workList);
            }else if(houseFlow.getWorkerType() == 9){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "木工");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 9).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 9).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 9
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 9
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 9
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setNineList(workList);
            }else if(houseFlow.getWorkerType() == 10){
                workList = new ArrayList<>();
                maps = new HashMap<>();
                maps.put("workName", "油漆工");
                maps.put("plan",  DateUtil.getDiffDays(new Date(),houseFlow.getStartDate()));
                maps.put("actual",  DateUtil.getDiffDays(houseFlow.getEndDate(),houseFlow.getStartDate()));
                //获取工序已验收节点
                Long iStart = nodeNumberDTOS.stream().filter(x -> x.getState() == 1 && x.getType() == 10).count();
                //获取工序全部节点
                Long iEnd = nodeNumberDTOS.stream().filter(x -> x.getType() == 10).count();
                maps.put("workStart",iStart);
                maps.put("workEnd",iEnd);
                maps.put("hundred", (int)(iStart/iEnd*100));
                int ss = materialNumberDTOS.stream().filter(x -> x.getType() == 10
                        && x.getShopCount() != null).mapToInt(MaterialNumberDTO :: getShopCount).sum();
                int aa = materialNumberDTOS.stream().filter(x -> x.getType() == 10
                        && x.getAskCount() != null).mapToInt(MaterialNumberDTO :: getAskCount).sum();
                int rr = materialNumberDTOS.stream().filter(x -> x.getType() == 10
                        && x.getReturnCount() != null).mapToInt(MaterialNumberDTO :: getReturnCount).sum();
                maps.put("askCount",aa + rr);//全部工序节点
                maps.put("shopCount",ss);//全部工序节点
                workList.add(maps);
                workNodeListDTO.setTenList(workList);
            }
        }
        return workNodeListDTO;
    }


    /**
     * 查询所有订单
     *
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryAllDeliverOrder(PageDTO pageDTO, String userId, String cityId, String orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (StringUtils.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("用户ID不能为空!");
            }
            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空!");
            }
//            if (StringUtils.isEmpty(orderStatus)) {
////                return ServerResponse.createByErrorMessage("订单状态不能为空!");
////            }
            Example example = new Example(DjDeliverOrder.class);
            example.createCriteria()
                    .andEqualTo(DjDeliverOrder.CITY_ID,cityId)
                    .andEqualTo(DjDeliverOrder.ORDER_STATUS,orderStatus)
                    .andEqualTo(DjDeliverOrder.MEMBER_ID,userId)
                    .andEqualTo(DjDeliverOrder.STOREFONT_ID,userId);
            List<Order> list = IBillDjDeliverOrderMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("查询所有订单异常", e);
            return ServerResponse.createByErrorMessage("查询所有订单异常" + e);
        }
    }
}
