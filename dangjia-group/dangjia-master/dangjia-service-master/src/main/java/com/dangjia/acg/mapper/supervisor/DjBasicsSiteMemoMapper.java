package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.dto.supervisor.DjBasicsSiteMemoDTO;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface DjBasicsSiteMemoMapper extends Mapper<DjBaicsSiteMemo> {

    List<DjBasicsSiteMemoDTO> querySiteMemo( @Param("memberId") String memberId);

    DjBasicsSiteMemoDTO querySiteMemoDetail(@Param("id") String id);

    List<DjBasicsSiteMemoDTO> queryRemindSiteMemo( @Param("memberId") String memberId);

    List<DjBasicsSiteMemoDTO> queryRemindSiteMemoDetail( @Param("memberId") String memberId,@Param("id")  String id);

    List<DjBaicsSiteMemo> querySiteMemoByMemberId(@Param("memberId") String memberId);

}
