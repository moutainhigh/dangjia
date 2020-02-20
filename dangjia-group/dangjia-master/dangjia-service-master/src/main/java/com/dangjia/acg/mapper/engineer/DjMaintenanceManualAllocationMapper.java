package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.engineer.DjMaintenanceManualAllocationDTO;
import com.dangjia.acg.modle.engineer.DjMaintenanceManualAllocation;
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
public interface DjMaintenanceManualAllocationMapper extends Mapper<DjMaintenanceManualAllocation> {

    List<DjMaintenanceManualAllocationDTO> searchManualAllocation(@Param("status") Integer status, @Param("searchKey") String searchKey,@Param("manuaId") String manuaId);

}
