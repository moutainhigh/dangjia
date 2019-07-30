package com.dangjia.acg.mapper.store;

import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.modle.store.StoreUser;
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
public interface IStoreUserMapper extends Mapper<StoreUser> {

    List<StoreUserDTO> getStoreUsers(@Param("storeId") String storeId, @Param("searchKey") String searchKey, @Param("limit") Integer limit);

}
