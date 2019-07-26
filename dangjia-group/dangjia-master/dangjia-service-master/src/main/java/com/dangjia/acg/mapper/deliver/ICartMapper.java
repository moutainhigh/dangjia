package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.modle.deliver.Cart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: wk
 * Date: 2019/05/15 0009
 * Time: 15:24
 */
@Repository
public interface ICartMapper extends Mapper<Cart> {


    /*更新商品名称和各项信息*/
    List<Cart> cartList( @Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId, @Param("memberId") String memberId);

}
