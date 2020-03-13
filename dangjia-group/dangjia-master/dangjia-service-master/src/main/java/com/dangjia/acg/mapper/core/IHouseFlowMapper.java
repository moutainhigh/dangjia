package com.dangjia.acg.mapper.core;

import com.dangjia.acg.dto.core.HouseFlowDTO;
import com.dangjia.acg.modle.core.HouseFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:01
 */
@Repository
public interface IHouseFlowMapper extends Mapper<HouseFlow> {
    HouseFlow getByWorkerTypeId(@Param("houseId") String houseId, @Param("workerTypeId") String workerTypeId);

    /**
     * 根据houseId查询已进场未完工工序
     */
    List<HouseFlow> unfinishedFlow(@Param("houseId") String houseId);


    List<HouseFlow> getAllFlowByHouseId(@Param("houseId") String houseId);

    List<HouseFlow> checkAllFinish(@Param("houseId") String houseId, @Param("houseFlowId") String houseFlowId);

    List<HouseFlow> getHouseIsStart(@Param("houseId") String houseId);

    HouseFlow getHouseFlowByHidAndWty(@Param("houseId") String houseId, @Param("workerType") Integer workerType);

    /**
     * 找出启用工种给大管家算审核钱
     */
    List<HouseFlow> getForCheckMoney(@Param("houseId") String houseId);


    List<HouseFlowDTO> getHouseScheduleFlow(@Param("houseId") String houseId);


    /**
     * 查下个未开工的工种
     */
    HouseFlow getNextHouseFlow(@Param("houseId") String houseId);

    List<HouseFlow> getWorkerFlow(@Param("houseId") String houseId);

    /**
     * @Description:根据用户id查询[所有房子][正在施工][所有工序]
     * @author: luof
     * @date: 2020-3-11
     */
    List<Integer> queryWorkerTypeListByMemberId(@Param("memberId")String memberId);


    Map<String,Object> optimizationHander(HouseFlow houseFlow);
}
