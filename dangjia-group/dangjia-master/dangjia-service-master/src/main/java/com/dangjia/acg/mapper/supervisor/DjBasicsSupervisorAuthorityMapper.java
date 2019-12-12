package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface DjBasicsSupervisorAuthorityMapper  extends Mapper<DjBasicsSupervisorAuthority> {

   List<DjBasicsSupervisorAuthority> searchAuthority(@Param("visitState") String visitState, @Param("keyWord") String keyWord); ;

}
