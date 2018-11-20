package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.modle.actuary.SearchBox;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
 * 
   * @类 名： ISerchBoxMapper
   * @功能描述：  
   * @作者信息： zmj
   * @创建时间： 2018-9-15下午3:15:10
 */
@Repository
public interface ISearchBoxMapper extends Mapper<SearchBox> {
	List<SearchBox> getHeatSearchBox();
}
