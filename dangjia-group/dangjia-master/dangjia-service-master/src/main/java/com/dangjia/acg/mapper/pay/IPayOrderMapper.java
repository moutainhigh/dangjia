package com.dangjia.acg.mapper.pay;

import com.dangjia.acg.modle.pay.PayOrder;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 16:05
 */
@Repository
public interface IPayOrderMapper extends Mapper<PayOrder> {
}
