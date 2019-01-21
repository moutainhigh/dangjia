package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.MendOrderCheck;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2019/1/16 0016
 * Time: 15:10
 */
@Repository
public interface IMendOrderCheckMapper extends Mapper<MendOrderCheck> {

    MendOrderCheck getByMendOrderId(@Param("mendOrderId")String mendOrderId,@Param("roleType")String roleType);
}
