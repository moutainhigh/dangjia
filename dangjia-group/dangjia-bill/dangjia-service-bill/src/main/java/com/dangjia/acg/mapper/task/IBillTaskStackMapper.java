package com.dangjia.acg.mapper.task;

import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.modle.house.TaskStack;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * 任务处理
 * fzh 2019/12/12
 */
@Repository
public interface IBillTaskStackMapper extends Mapper<TaskStack> {

}
