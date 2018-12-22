package com.dangjia.acg.mapper.pay;

import com.dangjia.acg.modle.pay.BusinessOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 15:59
 */
@Repository
public interface IBusinessOrderMapper extends Mapper<BusinessOrder> {

    BusinessOrder byNumber(@Param("number")String number);
}
