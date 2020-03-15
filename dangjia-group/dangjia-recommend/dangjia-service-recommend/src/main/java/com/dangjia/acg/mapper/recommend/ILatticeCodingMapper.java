package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.LatticeCoding;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description: 方格编号操作
 * @author: luof
 * @date: 2020-3-13
 */
@Repository
public interface ILatticeCodingMapper extends Mapper<LatticeCoding> {

    /**
     * @Description: 查询编号名称列表 - 根据编码值列表
     * @author: luof
     * @date: 2020-3-13
     */
    List<String> queryCodingNameList(@Param("valueList") List<Integer> valueList);
}
