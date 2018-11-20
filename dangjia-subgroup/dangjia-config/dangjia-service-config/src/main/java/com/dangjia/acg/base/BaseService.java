package com.dangjia.acg.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.common.Mapper;

/**
 * Created by QiYuXiang on 2017/7/4.
 */
@Service
public abstract class BaseService<T> {

    @Autowired
    protected Mapper<T> mapper;

    public int save(T entity){
        return mapper.insert(entity);
    }

    public int delete(T entity){
        return mapper.deleteByPrimaryKey(entity);
    }

    public int modify(T entity){
        return mapper.updateByPrimaryKey(entity);
    }
}
