package com.dangjia.acg.mapper.store;

import com.dangjia.acg.modle.store.Store;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:20
 */
@Repository
public interface IStoreMapper extends Mapper<Store>{

    /**
     * 查詢門店
     * @param cityId
     * @param storeName
     * @return
     */
    List<Store> queryStore(@Param("cityId") String cityId,@Param("storeName") String storeName);
}
