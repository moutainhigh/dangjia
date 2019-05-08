package com.dangjia.acg.mapper.clue;

import com.dangjia.acg.modle.clue.ClueTalk;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface ClueTalkMapper extends Mapper<ClueTalk> {

    /**
     * 通过线索ID获取所有谈话记录
     * @param clueId
     * @return
     */
    List<ClueTalk> getTalkByClueId(String clueId);
    Date getMaxDate(String clueID);
}
