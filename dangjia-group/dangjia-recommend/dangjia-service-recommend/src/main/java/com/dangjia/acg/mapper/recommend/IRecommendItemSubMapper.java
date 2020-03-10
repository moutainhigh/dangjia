package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.RecommendItemSub;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description: 推荐参考子项数据操作
 * @author: luof
 * @date: 2020-3-9
 */
@Repository
public interface IRecommendItemSubMapper extends Mapper<RecommendItemSub> {

    /**
     * @Description: 查询推荐参考子项列表
     * @author: luof
     * @date: 2020-3-9
     */
    List<RecommendItemSub> queryList(@Param("itemId") String itemId, @Param("itemSubName") String itemSubName);
}
