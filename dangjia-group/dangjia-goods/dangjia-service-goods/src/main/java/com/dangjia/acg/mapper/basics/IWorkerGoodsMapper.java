package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.WorkerGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 工价商品Dao
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/12 上午11:09
 */
@Repository
public interface IWorkerGoodsMapper extends Mapper<WorkerGoods> {


    List<WorkerGoods> selectLists();
    
    List<WorkerGoods> selectList(@Param("workerTypeId") String workerTypeId, @Param("searchKey") String searchKey, @Param("showGoods") String showGoods);
    
    List<WorkerGoods> queryByName(@Param("name") String name,@Param("workerTypeId") String workerTypeId);//模糊查询
    List<WorkerGoods> selectByName(@Param("name") String name,@Param("workerTypeId") String workerTypeId);//非模糊查询name
    List<WorkerGoods> selectByWorkerGoodsSn(@Param("workerGoodsSn") String name,@Param("workerTypeId") String workerTypeId);

    WorkerGoods queryById(@Param("id") String id);
    Double getWorkertoCheck(@Param("houseId") String houseId,@Param("houseFlowId") String houseFlowId);
    Double getPayedWorker(@Param("houseId") String houseId,@Param("houseFlowId") String houseFlowId);
    List<WorkerGoods> queryRepairBudgetWorker(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId,@Param("name") String name);

    /*更新单位*/
    void updateWorkerGoodsByUnitId(@Param("unitId") String unitId,@Param("unitName") String unitName);
}