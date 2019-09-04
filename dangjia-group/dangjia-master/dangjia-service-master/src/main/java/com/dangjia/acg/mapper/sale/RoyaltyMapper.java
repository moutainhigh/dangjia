package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.sale.royalty.DjRoyaltyDetailsSurface;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 提成配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@Repository
public interface RoyaltyMapper extends Mapper<DjRoyaltyDetailsSurface> {

    /**
     * 查询订单数量
     * @return
     */
    List<BaseEntity> queryRoyaltySurface();

    /**
     * 查询提出配置最大单数
     * @return
     */
    DjRoyaltyDetailsSurface selectOverSingle();

}
