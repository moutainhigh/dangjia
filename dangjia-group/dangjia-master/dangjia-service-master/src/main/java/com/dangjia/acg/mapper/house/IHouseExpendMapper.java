package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.HouseExpend;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/12/13 0013
 * Time: 17:12
 */
@Repository
public interface IHouseExpendMapper extends Mapper<HouseExpend> {

    HouseExpend getByHouseId(@Param("houseId")String houseId);
}
