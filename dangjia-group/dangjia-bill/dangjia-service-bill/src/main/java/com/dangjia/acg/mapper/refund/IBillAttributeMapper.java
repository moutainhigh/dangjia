package com.dangjia.acg.mapper.refund;

import com.dangjia.acg.modle.attribute.Attribute;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
   * @类 名： GoodsAttributeDao
   * @功能描述：属性类别dao
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IBillAttributeMapper extends Mapper<Attribute> {

}
