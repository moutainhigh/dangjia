package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseFlow;
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
    List<HouseFlow> getAllFlowByHouseId(@Param("houseId")String houseId);
    List<HouseFlow> checkAllFinish (@Param("houseId")String houseId,@Param("houseFlowId")String houseFlowId);
    List<HouseFlow> getHouseIsStart(@Param("houseId")String houseId);
    HouseFlow getHouseFlowByHidAndWty(@Param("houseId")String houseId,@Param("workerType")Integer workerType);
    List<HouseFlow> getForCheckMoney(@Param("houseId")String houseId);
    List<HouseFlow> getNextTopHouseFlow(@Param("houseId")String houseId);
    List<HouseFlow> getHouseNot0(@Param("houseId")String houseId);
    List<HouseFlow> getFlowByhouseIdNot12(@Param("houseId")String houseId);
}
