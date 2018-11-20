package com.dangjia.acg.mapper.house;

import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.modle.house.House;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:09
 */
@Repository
public interface IHouseMapper extends Mapper<House> {

    List<House> getStatisticsByDate(@Param("start")Date start, @Param("end")Date end);

    List<Map<String,Object>> getList(@Param("memberId")String memberId);

    HouseDTO startWorkPage(@Param("houseId")String houseId);

    List<DesignDTO> getDesignList(@Param("designerOk")int designerOk,@Param("userName")String mobile,
                                  @Param("residential")String residential,@Param("number")String number);

    List<House> getSameLayout(@Param("cityId")String cityId,@Param("villageId")String villageId,
                                  @Param("minSquare")Double minSquare,@Param("maxSquare")Double maxSquare);
}
