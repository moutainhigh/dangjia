package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.ModelingLayout;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/9 0009
 * Time: 17:43
 */
@Repository
public interface IModelingLayoutMapper extends Mapper<ModelingLayout> {
    //根据小区id查询户型实体
    List<ModelingLayout> queryModelingLayoutByVillageId(@Param("villageId") String villageId);

    //根据小区id 和 户型name查询户型实体
    List<ModelingLayout> queryModelingLayoutByName(@Param("villageId") String villageId,@Param("name")String name);

}
