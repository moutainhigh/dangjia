package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.dto.house.HouseListDTO;
import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import com.dangjia.acg.dto.repair.RepairMendDTO;
import com.dangjia.acg.modle.house.House;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:09
 */
@Repository
public interface IBillHouseMapper extends Mapper<House> {

}
