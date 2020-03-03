package com.dangjia.acg.mapper.shell;

import com.dangjia.acg.dto.shell.HomeShellOrderDTO;
import com.dangjia.acg.modle.shell.HomeShellOrder;
import org.apache.ibatis.annotations.Param;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface IHomeShellOrderMapper extends Mapper<HomeShellOrder> {

    List<HomeShellOrderDTO> selectShellOrderList(@Param("exchangeClient") Integer  exchangeClient,
                                                 @Param("status") Integer  status,
                                                 @Param("startTime") String startTime,
                                                 @Param("endTime") String  endTime,
                                                 @Param("searchKey") String  searchKey);

    Integer selectExchangeCount(@Param("memberId") String memberId,@Param("productId") String productId);

    List<HomeShellOrderDTO> searchShellProductInfo(@Param("memberId") String memberId);

    List<HomeShellOrderDTO> queryHomeShellOrderList(@Param("paramKey") String paramkey,@Param("type") Integer type);
}
