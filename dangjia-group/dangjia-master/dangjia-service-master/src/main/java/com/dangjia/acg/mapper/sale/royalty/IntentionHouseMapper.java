package com.dangjia.acg.mapper.sale.royalty;

import com.dangjia.acg.dto.member.IntentionHouseDTO;
import com.dangjia.acg.modle.home.IntentionHouse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 意向房子
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@Repository
public interface IntentionHouseMapper extends Mapper<IntentionHouse> {

    /**
     * 删除意向房子
     * @param id
     */
    void deleteIntentionHouse(@Param("id")String id);

    /**
     * 查询意向房子
     * @param clueId
     * @return
     */
    List<IntentionHouseDTO> queryIntentionHouse(@Param("clueId")String clueId);

}
