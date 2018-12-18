package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.modle.worker.Evaluate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/11/27 0027
 * Time: 9:38
 */
@Repository
public interface IEvaluateMapper extends Mapper<Evaluate> {

    /**查工匠被管家的评价*/
    Evaluate getForCountMoneySup(@Param("houseFlowId") String houseFlowId,
                                 @Param("applyType") int applyType ,@Param("workerId") String workerId);

    /**查工匠被业主的评价*/
    Evaluate getForCountMoney(@Param("houseFlowId") String houseFlowId,
                              @Param("applyType") int applyType ,@Param("workerId") String workerId);

}
