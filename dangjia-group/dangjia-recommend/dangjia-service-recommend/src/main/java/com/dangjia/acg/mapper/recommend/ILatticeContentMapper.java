package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.recommend.LatticeContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description: 方格内容操作
 * @author: luof
 * @date: 2020-3-13
 */
@Repository
public interface ILatticeContentMapper extends Mapper<LatticeContent> {

    /** 查询 总数 */
    int queryTotal();

    /** 删除 全部 */
    int deleteAll();

    /** 新增 批量 */
    int addBatch(@Param("contentList")List<LatticeContent> contentList);
}
