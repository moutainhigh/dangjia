package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.house.HouseChoiceCase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:09
 */
@Repository
public interface HouseChoiceCaseMapper extends Mapper<HouseChoiceCase> {

    /** 查询案例列表 */
    List<HouseChoiceCase> queryHouseChoiceCaseList(@Param("title") String title);
}
