package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.SurplusWareDivert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 剩余材料的临时仓库 挪货记录
 * ysl at 2019/1/29
 */
@Repository
public interface ISurplusWareDivertMapper extends Mapper<SurplusWareDivert> {
    //所有剩余材料的临时仓库 挪货记录
    List<SurplusWareDivert> getAllSurplusWareDivert();


    //获取某个临时仓库 的所有剩余材料 的某个 材料
    List<SurplusWareDivert> getAllSurplusWareDivertListBySIdAndPid(@Param("surplusWareHouseId")String surplusWareHouseId,@Param("productId")String productId);

    //获取某个商品的 最近的挪货记录
    SurplusWareDivert getDivertBySIdAndPidSortDate(@Param("productId")String productId);

    //获取某个临时仓库的 最近的挪货记录
    SurplusWareDivert getDivertBySIdAndWareHouseIdSortDate(@Param("surplusWareHouseId")String surplusWareHouseId);
}

