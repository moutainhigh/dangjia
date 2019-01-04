package com.dangjia.acg.mapper.design;

import com.dangjia.acg.modle.design.HouseDesignImage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 17:54
 */
@Repository
public interface IHouseDesignImageMapper extends Mapper<HouseDesignImage> {

    /**查询平面图*/
    HouseDesignImage planeGraph(@Param("houseId")String houseId);

    List<HouseDesignImage> byNumber(@Param("houseId")String houseId, @Param("businessOrderNumber")String businessOrderNumber);
}
