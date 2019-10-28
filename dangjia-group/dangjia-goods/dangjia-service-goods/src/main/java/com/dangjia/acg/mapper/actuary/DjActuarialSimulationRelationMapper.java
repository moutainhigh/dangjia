package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.ActuarialSimulateionReationDTO;
import com.dangjia.acg.modle.actuary.DjActuarialProductConfig;
import com.dangjia.acg.modle.actuary.DjActuarialSimulationRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: Date: 2019/10/15
 * Time: 16:28
 */
@Repository
public interface DjActuarialSimulationRelationMapper extends Mapper<DjActuarialSimulationRelation> {

    void batchInsertAssemblyInfo(@Param("list") List<DjActuarialSimulationRelation> list);

    List<ActuarialSimulateionReationDTO> querySimulateAssemblyRelateionList(@Param("address") String address,@Param("cityId") String cityId);

    /**
     * 根据code组合，查询对应的精算模板
     * @param codeList code编码列表
     * @return
     */
    DjActuarialSimulationRelation querySimulateAssemblyRelateionInfo(@Param("codeList") List  codeList);
}
