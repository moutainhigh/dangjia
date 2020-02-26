package com.dangjia.acg.mapper.shell;

import com.dangjia.acg.dto.shell.HomeShellProductSpecDTO;
import com.dangjia.acg.modle.shell.HomeShellProductSpec;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IHomeShellProductSpecMapper extends Mapper<HomeShellProductSpec> {

    List<HomeShellProductSpecDTO> selectProductSpecByProductId(@Param("productId") String productId);

    HomeShellProductSpecDTO selectProductSpecInfo(@Param("productId") String productId);

}
