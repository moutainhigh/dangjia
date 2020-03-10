package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.RecommendConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @Description: 推荐配置操作
 * @author: luof
 * @date: 2020-3-9
 */
@Repository
public interface IRecommendConfigMapper extends Mapper<RecommendConfig> {

    /**
     * @Description: 查询推荐配置列表
     * @author: luof
     * @date: 2020-3-9
     */
    List<RecommendConfig> queryList();

    /**
     * @Description: 修改单个推荐配置
     * @author: luof
     * @date: 2020-3-9
     */
    int updateSingle(@Param("id") String id, @Param("modifyDate") Date modifyDate, @Param("configCode") String configCode, @Param("configValue") Integer configValue);
}
