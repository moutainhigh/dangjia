package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.engineer.DjMaintenanceRecordProductDTO;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 11:55
 */
@Repository
public interface DjMaintenanceRecordProductMapper extends Mapper<DjMaintenanceRecordProduct> {

    List<DjMaintenanceRecordProductDTO> queryDjMaintenanceRecordProductList(@Param("maintenanceRecordId") String maintenanceRecordId);
}
