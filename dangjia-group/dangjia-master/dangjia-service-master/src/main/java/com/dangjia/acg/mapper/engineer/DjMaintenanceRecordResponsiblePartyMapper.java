package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.engineer.DimensionRecordDTO;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordResponsiblePartyDTO;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordResponsibleParty;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 11:58
 */
@Repository
public interface DjMaintenanceRecordResponsiblePartyMapper extends Mapper<DjMaintenanceRecordResponsibleParty> {

    DjMaintenanceRecordResponsiblePartyDTO queryDjMaintenanceRecordResponsibleParty(@Param("id") String id,
                                                                                    @Param("responsiblePartyType") Integer responsiblePartyType);



    List<DimensionRecordDTO> queryDimensionRecord(@Param("responsiblePartyId") String responsiblePartyId);
}
