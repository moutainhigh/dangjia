package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.RecommendItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description: 推荐参考主项数据操作
 * @author: luof
 * @date: 2020-3-7
 */
@Repository
public interface IRecommendItemMapper extends Mapper<RecommendItem> {

    /**
     * @Description: 查询推荐参考主项列表
     * @author: luof
     * @date: 2020-3-7
     */
    List<RecommendItem> queryList(@Param("itemName") String itemName);
}
