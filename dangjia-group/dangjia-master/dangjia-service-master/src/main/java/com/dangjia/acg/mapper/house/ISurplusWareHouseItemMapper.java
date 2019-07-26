package com.dangjia.acg.mapper.house;

import com.dangjia.acg.dto.house.SurplusWareHouseProductDTO;
import com.dangjia.acg.modle.house.SurplusWareHouseItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 剩余材料的临时仓库 的所有仓库材料详情
 * ysl at 2019/1/29
 */
@Repository
public interface ISurplusWareHouseItemMapper extends Mapper<SurplusWareHouseItem> {

    //查询所有商品的库存 ，按照address 或 商品名字模糊查询
    List<SurplusWareHouseProductDTO> getAllProductsLikeAddressOrPName(@Param("address") String address, @Param("productName") String productName);

    //查询指定productId的所有仓库
    List<Map<String,Object>> getAllSurplusWareHouseListByPId(@Param("productId") String productId);

    //获取某个临时仓库 的所有剩余材料
    List<SurplusWareHouseItem> getAllSurplusWareHouseItemById(@Param("surplusWareHouseId") String surplusWareHouseId);

    //获取某个临时仓库 的所有剩余材料 的某个 材料
    SurplusWareHouseItem getAllSurplusWareHouseItemByProductId(@Param("surplusWareHouseId") String surplusWareHouseId, @Param("productId") String productId);

}

