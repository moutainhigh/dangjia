package com.dangjia.acg.mapper.refund;

import com.dangjia.acg.modle.brand.Brand;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**对数据库中Brand表操作的接口*/
@Repository
public interface IBillBrandMapper extends Mapper<Brand> {

}
