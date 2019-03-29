package com.dangjia.acg.mapper.complain;

import com.dangjia.acg.dto.complain.ComplainDTO;
import com.dangjia.acg.modle.complain.Complain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface IComplainMapper extends Mapper<Complain> {

    List<ComplainDTO> getComplainList(@Param("complainType")  Integer complainType, @Param("state")  Integer state, @Param("searchKey")  String searchKey);

    ComplainDTO getComplain( @Param("complainId")  String complainId);

}
