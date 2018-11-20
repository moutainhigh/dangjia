package com.dangjia.acg.mapper.design;

import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 17:53
 */
@Repository
public interface IDesignImageTypeMapper extends Mapper<DesignImageType> {

    List<String> getDesignImageIdList(@Param("houseId")String houseId);

    List<DesignImageType> getDesignImageTypeList(@Param("typeList")String[] typeList);

    /**
     * 根据houseId designImageTypeId查询
     */
    HouseDesignImage getHouseDesignImage(@Param("houseId")String houseId,@Param("designImageTypeId")String designImageTypeId);
}
