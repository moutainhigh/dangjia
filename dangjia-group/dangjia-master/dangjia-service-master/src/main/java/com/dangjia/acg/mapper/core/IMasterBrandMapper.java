package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.brand.Brand;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**对数据库中Brand表操作的接口*/
@Repository
public interface IMasterBrandMapper extends Mapper<Brand> {

}
