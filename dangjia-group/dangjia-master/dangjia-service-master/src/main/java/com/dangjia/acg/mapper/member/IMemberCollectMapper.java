package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.MemberCollect;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 用户表dao层
 * @author: qyx
 * @date: 2019-7-30下午17:00:23
 */
@Repository
public interface IMemberCollectMapper extends Mapper<MemberCollect> {

    List<House> queryCollectHouse(@Param("memberId") String memberId);

    List<DjBasicsProduct>  queryCollectGood(@Param("memberId") String memberId);
}

