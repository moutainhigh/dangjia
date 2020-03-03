package com.dangjia.acg.mapper.engineer;

import com.dangjia.acg.dto.engineer.DjMaintenanceRecordDTO;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordContent;
import com.dangjia.acg.modle.house.TaskStack;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

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

    //判断是否有正在处理中的质保
    List<DjMaintenanceRecord> selectMaintenanceRecoredByHouseId(@Param("houseId") String houseId,@Param("workerTypeSafeOrderId") String workerTypeSafeOrderId);

    /**
     * 查询符合条件的，需要处理的任务
     * @return
     */
    List<TaskStack> queryDjMaintenanceRecordListByStateTime();

    List<Map<String,Object>> queryRecordContentList(@Param("houseId") String houseId);
}
