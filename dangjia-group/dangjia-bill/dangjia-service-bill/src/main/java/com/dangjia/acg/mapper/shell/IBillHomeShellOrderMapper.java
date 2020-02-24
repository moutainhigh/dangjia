package com.dangjia.acg.mapper.shell;

import com.dangjia.acg.modle.shell.HomeShellOrder;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IBillHomeShellOrderMapper extends Mapper<HomeShellOrder> {
}
