package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.CustomerImage;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 客服图片表dao层
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface ICustomerImageMapper extends Mapper<CustomerImage> {

}

