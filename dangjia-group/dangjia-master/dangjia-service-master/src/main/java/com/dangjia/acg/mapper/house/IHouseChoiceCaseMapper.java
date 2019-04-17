package com.dangjia.acg.mapper.house;

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
public interface IHouseChoiceCaseMapper extends Mapper<HouseChoiceCase> {

    List<String> getTimeOutAd();

    List<String> getTimingAd();

    List<HouseChoiceCase> getHouseChoiceCase(@Param("cityId") String cityId);
    List<HouseChoiceCase> getAllHouseChoiceCase();

}
