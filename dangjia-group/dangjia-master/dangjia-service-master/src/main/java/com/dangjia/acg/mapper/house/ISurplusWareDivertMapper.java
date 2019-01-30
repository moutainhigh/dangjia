package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.modle.house.SurplusWareHouseItem;
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
    List<SurplusWareDivert> getAllSurplusWareDivertListBySId(String surplusWareHouseId);
}

