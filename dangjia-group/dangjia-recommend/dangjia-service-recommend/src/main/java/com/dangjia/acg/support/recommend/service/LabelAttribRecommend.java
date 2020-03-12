package com.dangjia.acg.support.recommend.service;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.IRecommendItemSubMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
//import com.dangjia.acg.service.core.HouseFlowService;
import com.dangjia.acg.service.core.WorkerTypeService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.recommend.RecommendTargetService;
import com.dangjia.acg.support.recommend.util.RecommendMainItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:根据属性标签-进行推荐
 * @author: luof
 * @date: 2020-3-11
 */
@Service
public class LabelAttribRecommend {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(LabelAttribRecommend.class);

    @Autowired
    private HouseService houseService;

//    @Autowired
//    private HouseFlowService houseFlowService;

    @Autowired
    private WorkerTypeService workerTypeService;

    @Autowired
    private IRecommendItemSubMapper recommendItemSubMapper;

    @Autowired
    private RecommendTargetService recommendTargetService;

    /** 加载推荐 */
    public ServerResponse loadRecommendTarget(String memberId, PageDTO pageDTO){

        int number = pageDTO.getPageSize();
        logger.debug("需加载[属性标签]推荐条数["+number+"]");

        List<String> itemSubIdAllList = new ArrayList<String>();

        // 获取个人属性/标签 TODO 暂无

        // 查询推荐子项id - 根据房子属性
        List<String> itemSubIdHouseList = queryItemSubIdByHouseAttrib(memberId);
        if( itemSubIdHouseList != null ){
            itemSubIdAllList.addAll(itemSubIdHouseList);
        }

        if( itemSubIdAllList.size() < 1 ){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }

        // 查对应的推荐目标
        return recommendTargetService.queryPage(pageDTO, itemSubIdAllList, null);
    }

    // 查询推荐子项id - 根据房子属性
    private List<String> queryItemSubIdByHouseAttrib(String memberId){

        // 获取房子属性/标签
        ServerResponse houseRes = houseService.queryMyHouseByMemberId(memberId);
        List<House> houseList = (List<House>)houseRes.getResultObj();
//        logger.debug("用户["+memberId+"]房子信息:"+ JSON.toJSONString(houseList));
        if( houseList == null || houseList.size() < 1 ){
            return null;
        }

        // 获取房子当前[所有][正在施工][工序+排期]
        List<WorkerType> workerTypeList = workerTypeService.queryWorkerTypeListByMemberId(memberId);
//        logger.debug("用户["+memberId+"]房子所有正在施工的工序与排期信息:"+ JSON.toJSONString(workerTypeList));
        if( workerTypeList == null || workerTypeList.size() < 1 ){
            return null;
        }
        // 每个排期+1
        List<Integer> sortNextList = new ArrayList<Integer>();
        for( WorkerType workerType : workerTypeList ){
            sortNextList.add(workerType.getSort()+1);
        }

        // 再查工序
        List<Integer> typeList = workerTypeService.queryTypeBySort(sortNextList);
//        logger.debug("用户["+memberId+"]房子所有正在施工的下一步工序信息:"+ JSON.toJSONString(typeList));
        if( typeList == null || typeList.size() < 1 ){
            return null;
        }
        List<String> typeListStr = new ArrayList<String>();
        for( Integer type : typeList ){
            typeListStr.add(String.valueOf(type));
        }

        // 再查对应的子项id
        List<String> itemSubIdList = recommendItemSubMapper.queryItemSubIdByItemIdAndUnique(RecommendMainItem.worker_type.getItemId(), typeListStr);
        logger.debug("用户["+memberId+"]房子所有正在施工的下一步工序的推荐子项信息:"+ JSON.toJSONString(itemSubIdList));
        if( itemSubIdList == null || itemSubIdList.size() < 1 ){
            return null;
        }
        return itemSubIdList;
    }
}
