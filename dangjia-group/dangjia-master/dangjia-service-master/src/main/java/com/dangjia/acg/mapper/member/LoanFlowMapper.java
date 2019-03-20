package com.dangjia.acg.mapper.member;

import com.dangjia.acg.dto.member.LoanDTO;
import com.dangjia.acg.modle.member.LoanFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface LoanFlowMapper extends Mapper<LoanFlow> {

    List<LoanDTO> getLoanFlow(@Param("loanId") String loanId);

}