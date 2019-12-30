package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.dto.supervisor.DjBasicsSiteMemoDTO;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemo;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemoReminder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface DjBasicsSiteMemoReminderMapper extends Mapper<DjBaicsSiteMemoReminder> {


}
