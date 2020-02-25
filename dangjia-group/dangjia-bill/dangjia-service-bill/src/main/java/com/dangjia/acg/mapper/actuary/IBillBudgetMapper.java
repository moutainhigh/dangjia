package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.dto.order.DecorationCostDTO;
import com.dangjia.acg.dto.order.DecorationCostItemDTO;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
 * 
   * @类 名： IBillBudgetMapper.java
   * @功能描述：
 */
@Repository
public interface IBillBudgetMapper extends Mapper<BudgetMaterial> {

    //查询已支付精算的总费用
    Double selectTotalPriceByHouseId(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId,@Param("categoryTopId") String categoryTopId,@Param("steta") Integer steta);

    /**
     * 查询按工序汇总后的类别
     * @return
     */
    List<DecorationCostDTO> selectBudgetWorkerInfoList(@Param("houseId") String houseId);
    /**
     * 查询按类别汇总后的类别
     * @return
     */
    List<DecorationCostDTO> selectBudgetCategoryInfoList(@Param("houseId") String houseId);

    /**
     * 精算--按分类标签查询汇总信息
     * @param houseId
     * @param workerTypeId
     * @param categoryTopId
     * @return
     */
    List<DecorationCostDTO> searchBudgetCategoryLabelList(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId,@Param("categoryTopId") String categoryTopId);
    /**
     * 精算--分类汇总（按末级分类）
     * @param houseId
     * @param searchTypeId
     * @return
     */
    List<DecorationCostDTO> searchBudgetLastCategoryList(@Param("houseId") String houseId,@Param("searchTypeId") String searchTypeId,@Param("labelValId") String  labelValId);

    /**
     * 查询汇总的价钱
     * @return
     */
    Double searchBudgetLastCategoryCount(@Param("houseId") String houseId,@Param("searchTypeId") String searchTypeId);
    /**
     * 精算--查询商品信息
     * @param houseId
     * @param searchTypeId
     * @param labelValId
     * @return
     */
    List<DecorationCostItemDTO> searchBudgetProductList(@Param("houseId") String houseId,@Param("searchTypeId") String searchTypeId,
                                                        @Param("labelValId") String  labelValId,@Param("categoryId") String categoryId);
}
