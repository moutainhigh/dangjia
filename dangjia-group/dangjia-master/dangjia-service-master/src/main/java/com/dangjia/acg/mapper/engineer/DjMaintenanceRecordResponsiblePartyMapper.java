package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.engineer.DjMaintenanceRecordResponsiblePartyDTO;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordResponsibleParty;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

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

    int setAmountDeducted(@Param("id") String id);

}
