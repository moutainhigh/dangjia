package com.dangjia.acg.service.matter;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zmj
 * Date: 2018/11/20 0001
 * Time: 17:56
 */
@Service
public class RenovationManualService {
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    /**
     * 根据工序id查询所有装修指南
     * @param workerTypeId
     * @return
     */
    public ServerResponse queryRenovationManual(Integer pageNum, Integer pageSize,String workerTypeId){
        try{
            if(pageNum==null){
                pageNum=1;
            }
            if(pageSize==null){
                pageSize=10;
            }
            PageHelper.startPage(pageNum, pageSize);
            Example example=new Example(RenovationManual.class);
            example.createCriteria().andEqualTo("workerTypeId",workerTypeId);
            List<RenovationManual> rmList = renovationManualMapper.selectByExample(example);
            List<Map<String,Object>> listMap=new ArrayList<>();
            for(RenovationManual renovationManual:rmList){
                Map<String,Object> map = new HashMap<>();
                map.put("id",renovationManual.getId());
                map.put("name",renovationManual.getName());
                map.put("workerTypeId",renovationManual.getWorkerTypeId());
                map.put("workerTypeName",workerTypeMapper.selectByPrimaryKey(renovationManual.getWorkerTypeId()).getName());
                map.put("url",renovationManual.getUrl());
                map.put("urlName",renovationManual.getUrlName());
                map.put("types",renovationManual.getTypes());
                map.put("state",renovationManual.getState());
                map.put("orderNumber",renovationManual.getOrderNumber());
                listMap.add(map);
            }
            PageInfo pageResult = new PageInfo(rmList);
            pageResult.setList(listMap);
            return ServerResponse.createBySuccess("获取所有装修指南成功",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("获取所有装修指南失败");
        }
    }

    /**
     * 新增装修指南
     * @return
     */
    public ServerResponse addRenovationManual(String name,String workerTypeId,Integer orderNumber,String types,
                                              String url,String urlName){
        try{
            RenovationManual renovationManual=new RenovationManual();
            renovationManual.setName(name);
            renovationManual.setWorkerTypeId(workerTypeId);
            renovationManual.setOrderNumber(orderNumber);
            renovationManual.setTypes(types);
            renovationManual.setState(0);
            renovationManual.setUrl(url);
            renovationManual.setUrlName(urlName);
            renovationManualMapper.insertSelective(renovationManual);
            return ServerResponse.createBySuccessMessage("新增装修指南成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("新增装修指南失败");
        }
    }

    /**
     * 修改装修指南
     * @return
     */
    public ServerResponse updateRenovationManual(String id,String name,Integer orderNumber,String types,Integer state,
                                              String url,String urlName){
        try{
            RenovationManual renovationManual=new RenovationManual();
            renovationManual.setId(id);
            renovationManual.setName(name);
            renovationManual.setOrderNumber(orderNumber);
            renovationManual.setTypes(types);
            renovationManual.setState(state);
            renovationManual.setUrl(url);
            renovationManual.setUrlName(urlName);
            renovationManualMapper.updateByPrimaryKeySelective(renovationManual);
            return ServerResponse.createBySuccessMessage("修改装修指南成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("修改装修指南失败");
        }
    }

    /**
     * 删除装修指南
     * @return
     */
    public ServerResponse deleteRenovationManual(String id){
        try{
            RenovationManual renovationManual=new RenovationManual();
            renovationManual.setId(id);
            renovationManual.setState(1);
            renovationManualMapper.deleteByPrimaryKey(renovationManual);
            return ServerResponse.createBySuccessMessage("删除装修指南成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("删除装修指南失败");
        }
    }

    /**
     * 根据id查询装修指南对象
     * @return
     */
    public ServerResponse getRenovationManualById(String id){
        try{
            return ServerResponse.createBySuccess("根据id查询装修指南对象成功",renovationManualMapper.selectByPrimaryKey(id));
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createBySuccessMessage("根据id查询装修指南对象失败");
        }
    }

}
