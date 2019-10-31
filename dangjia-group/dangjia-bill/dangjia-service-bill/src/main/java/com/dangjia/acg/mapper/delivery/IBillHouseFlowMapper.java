package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.core.HouseFlowDTO;
import com.dangjia.acg.modle.core.HouseFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/10/31 0031
 * Time: 17:01
 */
@Repository
public interface IBillHouseFlowMapper extends Mapper<HouseFlow> {

}
