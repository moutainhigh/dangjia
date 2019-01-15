package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.ChangeOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/1/8 0008
 * Time: 11:44
 */
@Repository
public interface IChangeOrderMapper extends Mapper<ChangeOrder> {

    List<ChangeOrder> getList(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);

    /**未处理变更单*/
    List<ChangeOrder> unCheckOrder(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);

}
