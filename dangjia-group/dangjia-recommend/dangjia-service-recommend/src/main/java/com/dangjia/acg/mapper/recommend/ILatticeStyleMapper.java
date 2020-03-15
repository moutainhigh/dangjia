package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.LatticeStyle;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Description: 方格样式操作
 * @author: luof
 * @date: 2020-3-9
 */
@Repository
public interface ILatticeStyleMapper extends Mapper<LatticeStyle> {

    /** 查询类型值 根据主键 */
    Integer queryTypeValues(@Param("id")String id);

}
