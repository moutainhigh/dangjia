package com.dangjia.acg.mapper.member;

import com.dangjia.acg.dto.sale.client.CustomerIndexDTO;
import com.dangjia.acg.dto.sale.store.GrabSheetDTO;
import com.dangjia.acg.modle.member.Customer;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 客服表dao层
 *
 * @Description: TODO
 * @author: qyx
 * @date: 2018-11-9 10:22:23
 */
@Repository
public interface ICustomerMapper extends Mapper<Customer> {


    Customer getCustomerByMemberId(@Param("memberId") String memberId);

    /**
     * 根据业主id查询
     * @param memberId
     * @param stage -1 表示 忽略该字段
     * @return
     */
    Customer getCustomerByMemberIdAndStage(@Param("memberId") String memberId,@Param("stage") Integer stage);


    List<CustomerIndexDTO> waitDistribution(@Param("userId") String userId,@Param("searchKey") String searchKey,@Param("time") String time);

    List<GrabSheetDTO> grabSheet(@Param("storeId") String storeId, @Param("robStats") Integer robStats);


    Integer queryType(@Param("memberId") String memberId);

    String queryLabelIdArr(@Param("mcId") String mcId);

    String queryLabelId(@Param("clueId") String clueId);

    void upDateLabelIdArr(Map<String,Object> map);

    void upDateLabelId(Map<String,Object> map);

    Integer queryTypeId(@Param("userId") String userId);

    List<Customer> getCustomerGroupBy(@Param("mobile") String mobile);

    Integer getwaitDistributionTips();

    List<Customer> getCustomerMemberIdList(@Param("memberId") String memberId);
}

