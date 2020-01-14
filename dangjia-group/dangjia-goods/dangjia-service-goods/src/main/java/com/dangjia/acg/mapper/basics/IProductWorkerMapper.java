package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.dto.product.ProductAppDTO;
import com.dangjia.acg.dto.product.ProductWorkerDTO;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
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
public interface IProductWorkerMapper extends Mapper<DjBasicsProductTemplate> {

    Double getWorkertoCheck(@Param("houseId") String houseId, @Param("houseFlowId") String houseFlowId);

    Double getPayedWorker(@Param("houseId") String houseId, @Param("houseFlowId") String houseFlowId);
    List<HomeProductDTO> getHomeProductList();
    Double getAgencyPurchaseMoney(@Param("houseId") String houseId, @Param("houseFlowId") String houseFlowId);

    /**
     * 根据工种查询对应的产品信息
     * @param workerTypeId 工种ID
     * @param houseId 工地ID
     * @return
     */
    List<ProductWorkerDTO> getProductWorker(@Param("workerTypeId") String workerTypeId,@Param("houseId") String houseId,@Param("orderSource") String orderSource);

    List<ProductAppDTO> getWorkerProductList(@Param("workerId") String workerId,@Param("houseId") String houseId, @Param("searchKey") String searchKey, @Param("cityId") String cityId);

}