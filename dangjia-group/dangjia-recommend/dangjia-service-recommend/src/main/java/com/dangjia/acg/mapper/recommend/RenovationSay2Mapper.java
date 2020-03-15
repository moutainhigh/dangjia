package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.say.RenovationSay;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 装修说
 */
@Repository
public interface RenovationSay2Mapper {

    /** 查询列表 */
    List<RenovationSay> queryList(@Param("content")String content);
}
