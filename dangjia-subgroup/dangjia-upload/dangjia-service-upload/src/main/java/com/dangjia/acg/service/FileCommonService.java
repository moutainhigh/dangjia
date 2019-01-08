package com.dangjia.acg.service;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.ByteToInputStream;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.config.ConstantProperties;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.IResourceFileMapper;
import com.dangjia.acg.model.ResourceFile;
import com.obs.services.ObsClient;
import com.obs.services.model.AccessControlList;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.System.currentTimeMillis;

/**
 * @author: QiYuXiang
 * @date: 2018/4/17
 */
@Service
@Component
public class FileCommonService {

  private Logger logger = LoggerFactory.getLogger(FileCommonService.class);
  @Autowired
  private IResourceFileMapper resourceFileMapper;
  @Autowired
  private ConfigUtil configUtil;

  private boolean bool = false;

  private String updateTime = "";

  @Value("${spring.profiles.active}")
  private String active;


  /**
   * 保存文件到暂时目录
   *
   * @param files
   * @return
   */
  public ServerResponse saveFileList(MultipartFile[] files,String filePath) {
    List<Map<String, Object>> list = new ArrayList<>();
    try {
      if(CommonUtil.isEmpty(filePath)){
        filePath=configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
      }else{
        filePath=filePath+"/";
      }
      for (MultipartFile file : files) {
        Map<String, Object> paramMap = new HashedMap();
        String webAddress = filePath+ DateUtil.convert(new Date(), DateUtil.FORMAT1) + "/";
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_PATH, String.class) + webAddress;
        String fileName = System.currentTimeMillis() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        fileName = new String(fileName.getBytes(), "UTF-8");
        File path = new File(address);
        if (!path.exists()) {
          path.mkdirs();
        }
        File dest = new File(address + fileName);
        file.transferTo(dest);
        try {
          if (file.getSize() >= 1*1024*1024)
          {
            Thumbnails.of(address + fileName)
                    .scale(1f)
                    .outputQuality(0.5d)
                    .toFile(address + fileName);
          }
          //压缩图片
//          ImageUtil.generateThumbnail3Directory(address, address + fileName);
          //上传至华为云OBS
          if(active!=null&&(active.equals("pre")||active.equals("test"))){
            // 创建ObsClient实例
            ObsClient obsClient = new ObsClient(ConstantProperties.HUAWEI_ACCESS_KEY_ID, ConstantProperties.HUAWEI_ACCESS_KEY_SECRET, ConstantProperties.HUAWEI_END_POINT);
            obsClient.putObject(ConstantProperties.HUAWEI_BUCKET_NAME, webAddress+"/"+fileName,  new File(address + fileName));
            //设置权限 这里是公开读
            obsClient.setBucketAcl(ConstantProperties.HUAWEI_BUCKET_NAME, AccessControlList.REST_CANNED_PUBLIC_READ);
            //删除服务器临时文件
            dest.delete();
          }
        }catch (Exception e){
          logger.error(e.getMessage(),e);
        }
        paramMap.put("address", webAddress+"/"+fileName);
        paramMap.put("url", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class)+webAddress+"/"+fileName);
        paramMap.put("fileName", fileName);

        //新增上传记录，记录上传文件
        ResourceFile resourceFile=new ResourceFile();
        resourceFile.setAddress(String.valueOf(paramMap.get("url")));
        resourceFile.setPath(String.valueOf(paramMap.get("address")));
        resourceFile.setFileName(fileName);
        resourceFileMapper.insert(resourceFile);

        list.add(paramMap);
      }

    } catch (IOException e) {
      throw new BaseException(ServerCode.SERVER_UNKNOWN_ERROR, "上传文件失败");
    }
    return ServerResponse.createBySuccess("上传文件成功",list);
  }

  /**
   * 将二进制转换成文件保存
   *
   * @param bytes   二进制字节
   * @param imgPath 图片的保存路径
   * @param imgName 图片的名称
   * @return 1：保存正常
   * 0：保存失败
   */
  public ServerResponse saveToImgByInputStream(byte[] bytes, String imgPath, String imgName) {
    File imgPathFile = new File(imgPath);
    if (!imgPathFile.exists()) {
      imgPathFile.mkdirs();
    }
    int stateInt = 1;
    InputStream instreams = ByteToInputStream.byte2Input(bytes);
    if (instreams != null) {
      try {
        File file = new File(imgPath, imgName);//可以是任何图片格式.jpg,.png等
        FileOutputStream fos = new FileOutputStream(file);
        byte[] b = new byte[1024];
        int nRead = 0;
        while ((nRead = instreams.read(b)) != -1) {
          fos.write(b, 0, nRead);
        }
        fos.flush();
        fos.close();
      } catch (Exception e) {
        stateInt = 0;
        return ServerResponse.createByErrorMessage("保存失败,原因："+e.getMessage());
      } finally {
      }
    }
    return ServerResponse.createBySuccess("保存成功",stateInt);
  }

  /**
   * 复制文件到新目录
   *
   * @param fileNameList
   * @param copyAddress
   */

  public ServerResponse copyFileList(List<String> fileNameList, String copyAddress) {
    for (String s : fileNameList) {
      //String oldAddress = "f:/temporary/";
      String oldAddress =
              configUtil.getValue(SysConfig.PUBLIC_DANGJIA_PATH, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class) + DateUtil.convert(new Date(), DateUtil.FORMAT1)
                      + "/";
      try {
        File desFile = new File(copyAddress);
        if (!desFile.exists()) {
          desFile.mkdirs();
        }
        FileUtils.copyFile(new File(oldAddress + s), new File(desFile, s));
      } catch (IOException e) {
        throw new BaseException(ServerCode.UPLODAD_FILE_ERROR);
      }
    }
    return ServerResponse.createBySuccessMessage("复制文件到新目录成功");
  }


  /**
   * 获取文件夹下面所有文件（包括子文件夹）
   *
   * @param addressPath
   * @return
   */
  public ServerResponse getFileList(String addressPath) {
    String path = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_PATH, String.class);
    if (addressPath == null || "".equals(addressPath)) {
      path = path + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
    } else {
      path = path + addressPath;
    }
    Map<String, String> paramMap = new HashMap<>();
    if (bool) {
      updateTime = "?" + currentTimeMillis();
      bool = false;
    }
    getFile(path, 0, paramMap);
    return ServerResponse.createBySuccess("获取成功",paramMap);
  }

  /**
   * 刷新缓存
   */
  public ServerResponse renovateFileClient() {
    bool = true;
    return ServerResponse.createBySuccessMessage("刷新缓存成功");
  }

  /**
   * 获取所有文件
   *
   * @param path
   * @param deep
   * @param paramMap
   */
  private void getFile(String path, int deep, Map paramMap) {
    String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
    String pathAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_PATH, String.class);
    // 获得指定文件对象
    File file = new File(path);
    // 获得该文件夹内的所有文件
    File[] array = file.listFiles();
    for (int i = 0; i < array.length; i++) {
      if (array[i].isFile())//如果是文件
      {

        String fileName = array[i].getPath().split(pathAddress)[1];
        fileName = fileName + updateTime;
        paramMap.put(fileName.split("\\.")[0].replaceAll("/", "_"), address + fileName);

      } else if (array[i].isDirectory())//如果是文件夹
      {

        getFile(array[i].getPath(), deep + 1, paramMap);
      }
    }
  }


}

