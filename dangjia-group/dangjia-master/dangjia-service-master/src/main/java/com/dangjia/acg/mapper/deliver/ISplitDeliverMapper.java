package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.modle.deliver.SplitDeliver;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:25
 */
@Repository
public interface ISplitDeliverMapper extends Mapper<SplitDeliver> {

    /**授权大管家收货*/
    void supState(@Param("splitDeliverId") String splitDeliverId);
}
