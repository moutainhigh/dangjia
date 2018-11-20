package com.dangjia.acg.api;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.List;

/**
 * @author: QiYuXiang
 * @date: 2018/4/17
 */
@FeignClient("dangjia-service-upload")
@Api(value = "文件上传",description = "文件上传")
public interface FileCommonAPI {

  @RequestMapping(value = "saveFileList", method = RequestMethod.POST)
  @ApiOperation(value = "保存文件至暂时目录", notes = "保存文件至暂时目录")
  ServerResponse saveFileList(@RequestParam("request") StandardMultipartHttpServletRequest request, @RequestParam("file") MultipartFile[] multipartFile, @RequestParam("address") String address);

  @RequestMapping(value = "saveToImgByInputStream", method = RequestMethod.POST)
  @ApiOperation(value = "保存文件", notes = "保存文件")
  ServerResponse saveToImgByInputStream(@RequestParam("bytes") byte[] bytes, @RequestParam("imgPath") String imgPath, @RequestParam("imgName") String imgName);

  @RequestMapping(value = "copyFileList", method = RequestMethod.POST)
  @ApiOperation(value = "复制文件至新目录", notes = "复制文件至新目录")
  ServerResponse copyFileList(@RequestBody List<String> fileNameList, @RequestParam("copyAddress")String copyAddress);

  @RequestMapping(value = "getFileList", method = RequestMethod.POST)
  @ApiOperation(value = "获取文件路径", notes = "获取文件路径")
  ServerResponse getFileList(@RequestParam("addressPath")String addressPath);


  @RequestMapping(value = "renovateFileClient", method = RequestMethod.POST)
  @ApiOperation(value = "刷新文件缓存", notes = "刷新文件缓存")
  ServerResponse renovateFileClient();


}
