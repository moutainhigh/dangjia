package com.dangjia.acg.mapper.pay;

import com.dangjia.acg.dto.deliver.WebOrderDTO;
import com.dangjia.acg.modle.pay.BusinessOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 15:59
 */
@Repository
public interface IBusinessOrderMapper extends Mapper<BusinessOrder> {

    List<BusinessOrder> byMemberId(@Param("memberId") String memberId);

    BusinessOrder byTaskId(@Param("taskId") String taskId, @Param("type") int type);

    List<WebOrderDTO> getWebOrderList(@Param("state") Integer state, @Param("searchKey") String searchKey);
}
