package com.dangjia.acg.controller;

import com.dangjia.acg.api.FileCommonAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.FileCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: QiYuXiang
 * @date: 2018/4/17
 */
@RestController
public class FileCommonController implements FileCommonAPI {

  @Autowired
  private FileCommonService fileCommonService;

  @Override
  @ApiMethod
  public ServerResponse saveFileList(StandardMultipartHttpServletRequest request, MultipartFile[] multipartFiles,String address) {
    if(multipartFiles.length==0){
      List<MultipartFile> allimg=new ArrayList<>();
      List<MultipartFile> images=request.getFiles("image");
      List<MultipartFile> files=request.getFiles("file");
      List<MultipartFile> imgFile=request.getFiles("imgFile");
      allimg.addAll(images);
      allimg.addAll(files);
      allimg.addAll(imgFile);
      multipartFiles=new MultipartFile[allimg.size()];
      multipartFiles=allimg.toArray(multipartFiles);
    }
    String name=request.getParameter("name");
   return  fileCommonService.saveFileList(multipartFiles,address,name);
  }

  @Override
  public Map saveEditorFile(MultipartFile[] imgFile, String dir) {
    return  fileCommonService.saveEditorFile(imgFile,dir);
  }
  @Override
  public ServerResponse saveToImgByInputStream(byte[] bytes, String imgPath, String imgName) {
   return fileCommonService.saveToImgByInputStream(bytes, imgPath, imgName);
  }

  @Override
  @ApiMethod
  public ServerResponse copyFileList(@RequestBody List<String> fileNameList, String copyAddress) {
    return fileCommonService.copyFileList(fileNameList,copyAddress);
  }


  @Override
  @ApiMethod
  public ServerResponse getFileList(String addressPath) {

  return fileCommonService.getFileList(addressPath);

  }


  @ApiMethod
  public ServerResponse renovateFileClient(){
    return fileCommonService.renovateFileClient();
  }



}
