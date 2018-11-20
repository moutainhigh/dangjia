package com.dangjia.acg.mapper.matter;

import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.matter.RenovationManualMember;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**装修指南
 * zmj
 */
@Repository
public interface IRenovationManualMemberMapper extends Mapper<RenovationManualMember> {
}

