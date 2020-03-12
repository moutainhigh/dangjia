package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.support.recommend.dto.BasicsStorefrontProductViewDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品
 * luof
 */
@Repository
public interface IStorefrontProductMapper {

    /** 通过货品或者商品名称查询 */
    List<BasicsStorefrontProductViewDTO> queryProductGroundByKeyWord(@Param("keyWord") String keyWord);
}
