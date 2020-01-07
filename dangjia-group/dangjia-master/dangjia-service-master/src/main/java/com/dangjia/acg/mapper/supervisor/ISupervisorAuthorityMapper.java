package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.dto.supervisor.AuthorityDTO;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ISupervisorAuthorityMapper extends Mapper<DjBasicsSupervisorAuthority> {

    List<AuthorityDTO> getStayAuthorityList(@Param("cityId") String cityId, @Param("visitState") Integer visitState, @Param("searchKey") String searchKey);

    List<AuthorityDTO> getAuthorityList(@Param("memberId") String memberId, @Param("visitState") Integer visitState, @Param("searchKey") String searchKey);

}
