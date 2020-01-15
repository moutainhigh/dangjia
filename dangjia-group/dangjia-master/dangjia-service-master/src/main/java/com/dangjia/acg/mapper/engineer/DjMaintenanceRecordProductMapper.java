package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordProductDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordProduct;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 11:55
 */
@Repository
public interface DjMaintenanceRecordProductMapper extends Mapper<DjMaintenanceRecordProduct> {

    List<DjMaintenanceRecordProductDTO> queryDjMaintenanceRecordProductList(@Param("maintenanceRecordId") String maintenanceRecordId);

    /**
     * 判断当前业主维保时所选的商品
     * @param memberId
     * @param houseId
     * @param maintenanceRecordType
     * @return
     */
    List<DjMaintenanceRecordProduct> selectMaintenanceProductByMemberId(@Param("memberId") String memberId,@Param("houseId") String houseId,@Param("maintenanceRecordType") String maintenanceRecordType,@Param("workerTypeId") String workerTypeId);

    /**
     * 根据申请单ID和申请类型，查询对应的需支付定单
     * @param maintenanceRecordId
     * @param maintenanceRecordType
     * @return
     */
    List<Map<String,Object>> selectWorkerTypeListById(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("maintenanceRecordType") Integer maintenanceRecordType);

    /**
     * 查询已支付的订单总额
     * @param maintenanceRecordId
     * @param maintenanceRecordType
     * @return
     */
    Double queryMaintenanceRecordMoney(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("maintenanceRecordType") Integer maintenanceRecordType);

    List<BasicsGoodsCategory> queryGroupByGoodsCategory(Map<String,Object> map);

    List<DjMaintenanceRecordProductDTO> queryMaintenanceShoppingBasket(Map<String,Object> map);

    List<DjMaintenanceRecordProduct> queryPayMaintenanceRecordProduct(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("maintenanceRecordType") Integer maintenanceRecordType,@Param("payState") Integer payState,@Param("workerTypeId") String  workerTypeId,@Param("storefrontId") String storefrontId);

    List<DjMaintenanceRecordProduct> selectStorefrontIdByTypeId(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("maintenanceRecordType") Integer maintenanceRecordType,@Param("payState") Integer payState);

    void updateRecordProductInfo(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("maintenanceRecordType") Integer maintenanceRecordType,@Param("businessOrderNumber") String businessOrderNumber,@Param("payState") Integer payState);

    void updateRecordProductInfoByBusinessNumber(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("businessOrderNumber") String businessOrderNumber);

    void updateRecordProductInfoByRecordId(@Param("maintenanceRecordId") String maintenanceRecordId);

    List<Map<String,Object>> selectCategoryByRecordId(@Param("maintenanceRecordId") String maintenanceRecordId,@Param("maintenanceRecordType") Integer maintenanceRecordType,@Param("storefrontId") String storefrontId);

    List<ActuarialProductAppDTO> selectMaintenaceProductByCategoryId(@Param("maintenanceRecordId") String maintenanceRecordId, @Param("maintenanceRecordType") Integer maintenanceRecordType, @Param("storefrontId") String storefrontId, @Param("categoryId") String categoryId);

    int setWorkerMaintenanceGoods(Map<String,Object> map);
}
