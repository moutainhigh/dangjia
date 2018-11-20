package com.dangjia.acg.mapper.core;

import com.dangjia.acg.dto.core.WorkerTypeDTO;
import com.dangjia.acg.modle.core.WorkerType;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工种类表dao层
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IWorkerTypeMapper extends Mapper<WorkerType> {

    //注册用 不查精算,防水
    List<WorkerTypeDTO> getWorkerTypeRegister();
    //查询未禁用，并且不查设计，精算，防水工种信息
    List<WorkerTypeDTO> getWorkerTypeList();
    //查询未禁用
    List<WorkerTypeDTO> getWorkerTypeListAll();

}

