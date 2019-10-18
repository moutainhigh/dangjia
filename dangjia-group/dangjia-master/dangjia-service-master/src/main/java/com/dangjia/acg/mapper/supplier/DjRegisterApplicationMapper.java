package com.dangjia.acg.mapper.supplier;

import com.dangjia.acg.dto.supplier.RegisterApplicationDTO;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:46
 */
@Repository
public interface DjRegisterApplicationMapper extends Mapper<DjRegisterApplication> {

    /**
     * 查询服务列表
     * @return
     */
    List<RegisterApplicationDTO> getAllRegistList(@Param("applicationStatus") String applicationStatus,
                                                  @Param("searchKey") String searchKey);
}
