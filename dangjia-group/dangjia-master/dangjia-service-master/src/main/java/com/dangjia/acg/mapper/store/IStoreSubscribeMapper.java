package com.dangjia.acg.mapper.store;

import com.dangjia.acg.modle.store.StoreSubscribe;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 18:03
 */
@Repository
public interface IStoreSubscribeMapper extends Mapper<StoreSubscribe> {

    /**
     * 门店预约查询
     * @return
     */
    List<StoreSubscribe> queryStoreSubscribe(@Param("searchKey") String searchKey);
}
