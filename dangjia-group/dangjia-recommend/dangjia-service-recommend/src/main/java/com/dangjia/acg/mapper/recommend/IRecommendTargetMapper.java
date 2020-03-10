package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @Description: 推荐目标数据操作
 * @author: luof
 * @date: 2020-3-9
 */
@Repository
public interface IRecommendTargetMapper extends Mapper<RecommendTargetInfo> {

    /**
     * @Description: 查询推荐目标列表
     * @author: luof
     * @date: 2020-3-9
     */
    List<RecommendTargetInfo> queryList(@Param("itemSubId") String itemSubId,
                                        @Param("targetType") Integer targetType,
                                        @Param("targetName") String targetName);

    /**
     * @Description: 删除单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    int deleteSingle(@Param("id") String id);

    /**
     * @Description: 查询单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    RecommendTargetInfo querySingle(@Param("id") String id);

    /**
     * @Description: 修改单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    int updateSingle(@Param("id") String id,
                     @Param("modifyDate") Date modifyDate,
                     @Param("sort") Integer sort,
                     @Param("clickNumber") Integer clickNumber);

    /**
     * @Description: 查询推荐目标个数
     * @author: luof
     * @date: 2020-3-9
     */
    int queryCount(@Param("itemSubId") String itemSubId,
                   @Param("targetType") Integer targetType,
                   @Param("targetId") String targetId);

    /**
     * @Description: 新增推荐目标单个
     * @author: luof
     * @date: 2020-3-10
     */
    int addSingle(RecommendTargetInfo target);
}
