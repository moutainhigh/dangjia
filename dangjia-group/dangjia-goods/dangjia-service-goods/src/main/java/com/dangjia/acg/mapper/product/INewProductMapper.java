package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.basics.Product;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
/**
 * @类 名： ProductDao
 * @功能描述： 商品dao
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface INewProductMapper extends Mapper<Product> {
}
