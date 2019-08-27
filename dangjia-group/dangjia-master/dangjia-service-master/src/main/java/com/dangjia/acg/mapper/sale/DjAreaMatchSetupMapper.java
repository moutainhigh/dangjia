package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.modle.sale.royalty.DjAreaMatchSetup;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

/**
 * 提成楼栋配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@Repository
public interface DjAreaMatchSetupMapper extends Mapper<DjAreaMatchSetup> {

    /**
     * 修改 楼栋id
     * @param map
     */
    void upDateBuildingId(Map<String,Object> map);

}
