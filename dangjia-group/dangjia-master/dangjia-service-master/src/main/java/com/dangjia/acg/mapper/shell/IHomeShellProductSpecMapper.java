package com.dangjia.acg.mapper.shell;

import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import com.dangjia.acg.modle.shell.HomeShellProductSpec;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IHomeShellProductSpecMapper extends Mapper<HomeShellProductSpec> {

}
