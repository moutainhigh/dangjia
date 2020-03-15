package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.LatticeContentType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Description: 方格内容类型操作
 * @author: luof
 * @date: 2020-3-9
 */
@Repository
public interface ILatticeContentTypeMapper extends Mapper<LatticeContentType> {

    /** 查询类型值 根据主键 */
    Integer queryTypeValue(@Param("id")String id);
}
