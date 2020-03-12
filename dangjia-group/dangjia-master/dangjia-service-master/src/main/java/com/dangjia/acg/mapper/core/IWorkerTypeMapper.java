package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.WorkerType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工种类表dao层
 *
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IWorkerTypeMapper extends Mapper<WorkerType> {

    List<WorkerType> unfinishedFlow(@Param("houseId") String houseId);

    String getName(@Param("type") int type);

    /**
     * @Description:根据用户id查询[所有房子][正在施工][所有工序与排期]
     * @author: luof
     * @date: 2020-3-12
     */
    List<WorkerType> queryWorkerTypeListByMemberId(String memberId);
}

