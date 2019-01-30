package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.modle.house.SurplusWareHouseItem;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 剩余材料的临时仓库 的所有仓库材料详情
 * ysl at 2019/1/29
 */
@Repository
public interface ISurplusWareHouseItemMapper extends Mapper<SurplusWareHouseItem> {
    //获取某个临时仓库 的所有剩余材料
    List<SurplusWareHouseItem> getAllSurplusWareHouseItemById(String surplusWareHouseId);

    //获取某个临时仓库 的所有剩余材料 的某个 材料
    SurplusWareHouseItem getAllSurplusWareHouseItemByProductId(String surplusWareHouseId, String productId);
}

