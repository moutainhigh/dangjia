package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.order.AcceptanceEvaluationListDTO;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 1/11/2019
 * Time: 上午 11:06
 */
@Repository
public interface BillDjDeliverSplitDeliverMapper extends Mapper<SplitDeliver> {

    List<AcceptanceEvaluationListDTO> queryAcceptanceEvaluationList(@Param("splitDeliverId") String splitDeliverId);
}
