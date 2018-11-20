package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.modle.actuary.ActuarialTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工艺说明
 * @ClassName: technologyDao
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IActuarialTemplateMapper  extends Mapper<ActuarialTemplate> {
    List<ActuarialTemplate> query(@Param("workerTypeId") String userId, @Param("stateType") String stateType,@Param("name")String name);
    int useById(String id);
}

