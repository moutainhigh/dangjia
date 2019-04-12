package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.member.Member;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

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

    /**
     * 查询houseFlowId
     */
    String selectHouseFlowId(@Param("houseId") String houseId, @Param("workerTypeId") String workerTypeId);

    /**
     * 查询某个房子的某个 顺序的工序的 HouseFlow 是否生成
     *
     * @param houseId
     * @param sort
     * @return
     */
    HouseFlow getHouseFlowBySort(@Param("houseId") String houseId, @Param("sort") Integer sort);

    List<HouseFlow> getAllFlowByHouseId(@Param("houseId") String houseId);

    List<HouseFlow> checkAllFinish(@Param("houseId") String houseId, @Param("houseFlowId") String houseFlowId);

    List<HouseFlow> getHouseIsStart(@Param("houseId") String houseId);

    HouseFlow getHouseFlowByHidAndWty(@Param("houseId") String houseId, @Param("workerType") Integer workerType);

    /**
     * 通过houseId和workerType拿到Member
     */
    Member getMemberByHouseIdAndWorkerType(@Param("houseId") String houseId, @Param("workerType") Integer workerType);
    /**
     * 找出启用工种给大管家算审核钱
     */
    List<HouseFlow> getForCheckMoney(@Param("houseId") String houseId);

    /**
     * 查下个未开工的工种
     */
    List<HouseFlow> getNextHouseFlow(@Param("sort") int sort, @Param("houseId") String houseId);
   HouseFlow getNextHouseFlow1( @Param("houseId") String houseId);

    List<HouseFlow> getWorkerFlow(@Param("houseId") String houseId);
}
