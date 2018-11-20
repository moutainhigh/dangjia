package com.dangjia.acg.mapper;

import com.dangjia.acg.dto.ResourceFileDTO;
import com.dangjia.acg.model.ResourceFile;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author: QiYuXiang
 * @date: 2018/4/21
 */
public interface IResourceFileMapper extends Mapper<ResourceFile> {

  List<ResourceFileDTO> getResourceList(@Param("fileName") String fileName,@Param("path") String path);

}
