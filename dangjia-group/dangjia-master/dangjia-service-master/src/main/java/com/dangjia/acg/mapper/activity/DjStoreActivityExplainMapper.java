package com.dangjia.acg.mapper.activity;

import com.dangjia.acg.dto.activity.DjStoreActivityDTO;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.activity.DjStoreActivityExplain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2020/3/13
 * Time: 14:18
 */
@Repository
public interface DjStoreActivityExplainMapper extends Mapper<DjStoreActivityExplain> {
}
