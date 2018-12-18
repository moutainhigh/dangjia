package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.Label;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ILabelMapper extends Mapper<Label> {

    /**c查找所有的单位*/
    List<Label> getLabel();
    /**根据拿到的name拿到单位对象*/
    Label getLabelByName(String name);
}