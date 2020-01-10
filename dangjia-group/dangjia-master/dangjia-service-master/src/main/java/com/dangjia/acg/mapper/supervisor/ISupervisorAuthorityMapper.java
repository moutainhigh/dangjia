package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.dto.supervisor.AuthorityDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordIndexDTO;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ISupervisorAuthorityMapper extends Mapper<DjBasicsSupervisorAuthority> {

    List<AuthorityDTO> getStayAuthorityList(@Param("cityId") String cityId, @Param("visitState") Integer visitState, @Param("searchKey") String searchKey);

    List<AuthorityDTO> getAuthorityList(@Param("memberId") String memberId, @Param("visitState") Integer visitState, @Param("searchKey") String searchKey);

    List<PatrolRecordIndexDTO> getSupHomePage(@Param("memberId") String memberId, @Param("cityId") String cityId);


    List<AuthorityDTO> getSupHouseList(@Param("memberId") String memberId,
                                       @Param("cityId") String cityId,
                                       @Param("sortNum") Integer sortNum,
                                       @Param("type") Integer type,
                                       @Param("latitude") String latitude,
                                       @Param("longitude") String longitude,
                                       @Param("searchKey") String searchKey);

    Integer getFatalism(@Param("houseId") String houseId, @Param("workerType") Integer workerType);

    Integer getAcceptanceCheck(@Param("houseId") String houseId);


    int getAllNumber(@Param("houseId") String houseId,
                     @Param("workerTypeId") String workerType);//总节点

    int getCompleteNumber(@Param("houseId") String houseId,
                          @Param("workerTypeId") String workerType);//已完成节点
}
