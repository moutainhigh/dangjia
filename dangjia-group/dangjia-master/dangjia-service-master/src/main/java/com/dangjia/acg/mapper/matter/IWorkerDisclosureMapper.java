package com.dangjia.acg.mapper.matter;

import com.dangjia.acg.modle.matter.WorkerDisclosure;
import com.dangjia.acg.modle.matter.WorkerEveryday;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**交底事项
 * zmj
 */
@Repository
public interface IWorkerDisclosureMapper extends Mapper<WorkerDisclosure> {

}

