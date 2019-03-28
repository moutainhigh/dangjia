package com.dangjia.acg.mapper.clue;


import com.dangjia.acg.modle.clue.Clue;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface ClueMapper extends Mapper<Clue> {
    List<Clue> getByStage(int stage);
    Clue getByPhone(String phone);
    List<Clue> getAll();
    List<Clue> getAllByCondition(String values);
}
