package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.WorkerType;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 工种类表dao层
 *
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IWorkerTypeMapper extends Mapper<WorkerType> {
}

