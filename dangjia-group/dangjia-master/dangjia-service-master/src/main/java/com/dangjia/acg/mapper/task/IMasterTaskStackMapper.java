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
public interface IMasterTaskStackMapper extends Mapper<TaskStack> {
  /**
   * 查询需处理的任务信息
   * @param houseId
   * @param memberId
   * @return
   */
  List<Task> selectTaskStackInfo(@Param("houseId") String houseId, @Param("memberId") String memberId);

  /**
   * 查询当前类型是否有待处理的任务
   * @param houseId
   * @param type
   * @return
   */
  List<Task> selectTaskStackInfoByType(@Param("houseId") String houseId,@Param("type") String type);
}
