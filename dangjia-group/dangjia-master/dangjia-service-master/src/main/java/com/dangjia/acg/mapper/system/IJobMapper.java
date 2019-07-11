package com.dangjia.acg.mapper.system;

import com.dangjia.acg.dto.system.JobRolesVO;
import com.dangjia.acg.modle.system.Job;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


/**
 * author: qyx
 * Date: 2019/7/1
 * Time: 16:20
 */
@Repository
public interface IJobMapper extends Mapper<Job>{

    /**
     * 查询用户及对应的角色
     * @param id
     * @return
     */
    JobRolesVO getJobAndRoles(@Param("id") String id);

}
