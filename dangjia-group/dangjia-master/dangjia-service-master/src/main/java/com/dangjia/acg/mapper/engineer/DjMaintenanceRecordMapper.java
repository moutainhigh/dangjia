package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.engineer.DjMaintenanceRecordDTO;
import com.dangjia.acg.dto.supervisor.*;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 9:59
 */
@Repository
public interface DjMaintenanceRecordMapper extends Mapper<DjMaintenanceRecord> {

    List<DjMaintenanceRecordDTO> queryDjMaintenanceRecordList(@Param("searchKey") String searchKey,
                                                              @Param("state") Integer state);


    DjMaintenanceRecordDTO queryDjMaintenanceRecordDetail(@Param("id") String id);

    List<MaintenanceRecordDTO > queryApplicationInfo(@Param("houseId") String houseId);

    List<AcceptanceTrendDTO> queryAcceptanceTrend(@Param("houseId") String houseId);

    List<AcceptanceTrendDetailDTO> queryAcceptanceTrendDetail(@Param("id") String id);

    List<DjResponsiblePartyDTO> queryDvResponsibility(@Param("houseId") String houseId);

    List<StoreMaintenanceDTO> queryStoreMaintenance(@Param("responsiblePartyType")String responsiblePartyType ,@Param("responsiblePartyId") String responsiblePartyId);

    List<MemberMaintenanceDTO> queryMemberMaintenance(@Param("responsiblePartyType")String responsiblePartyType ,@Param("responsiblePartyId") String responsiblePartyId);

    WorkerSiteDetailsDTO  querySupervisorHostDetailList(@Param("houseId") String houseId);

    List<RepairHouseListDTO>  queryMaintenanceHostList(@Param("memberId")String memberId,@Param("keyWord")String keyWord);

    MtHostListDetailDTO queryMtHostListDetail(@Param("houseId") String houseId);

    List<DjMaintenanceRecord> queryMaintenanceRecord(@Param("memberId") String memberId,@Param("houseId") String houseId);

    //判断是否有正在处理中的质保
    List<DjMaintenanceRecord> selectMaintenanceRecoredByHouseId(@Param("houseId") String houseId);

}
