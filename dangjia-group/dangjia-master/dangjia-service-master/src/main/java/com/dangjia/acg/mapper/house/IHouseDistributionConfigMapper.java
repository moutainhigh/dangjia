package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.HouseDistributionConfig;
import feign.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2019 08 23
 * Time: 20:26
 */
@Repository
public interface IHouseDistributionConfigMapper extends Mapper<HouseDistributionConfig> {

    List<HouseDistributionConfig> getHouseDistributionConfigs(@Param("villages") String villages);

}
