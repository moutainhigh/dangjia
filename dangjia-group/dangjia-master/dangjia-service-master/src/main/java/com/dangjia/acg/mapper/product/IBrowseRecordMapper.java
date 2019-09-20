package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.BrowseRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IBrowseRecordMapper extends Mapper<BrowseRecord> {
    List<BrowseRecord> queryBrowseRecord(@Param("memberId") String memberId);
}
