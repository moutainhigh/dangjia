package com.dangjia.acg.mapper.member;

import com.dangjia.acg.dto.member.LoanDTO;
import com.dangjia.acg.modle.member.Loan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface LoanMapper extends Mapper<Loan> {

    List<LoanDTO> getLoanList(@Param("cityId") String cityId,@Param("state") Integer state, @Param("searchKey") String searchKey);

}