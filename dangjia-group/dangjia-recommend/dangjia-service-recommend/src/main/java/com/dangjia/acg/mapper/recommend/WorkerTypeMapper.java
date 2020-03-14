package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.core.WorkerType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工种类表dao层
 * @author: luof
 */
@Repository
public interface WorkerTypeMapper extends Mapper<WorkerType> {

    /**
     * @Description:根据用户id查询[所有房子][正在施工][所有工序与排期]
     * @author: luof
     * @date: 2020-3-12
     */
    List<WorkerType> queryWorkerTypeListByMemberId(@Param("memberId") String memberId);

    /**
     * @Description:查询工序 根据排期
     * @author: luof
     * @date: 2020-3-12
     */
    List<Integer> queryTypeBySort(@Param("sortList") List<Integer> sortList);
}

