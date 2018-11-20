package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.Course;
import com.dangjia.acg.dto.core.HouseResult;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.dto.house.ShareDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingLayoutMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMemberMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingLayout;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.matter.RenovationManualMember;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.City;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 17:56
 */
@Service
public class HouseService {

    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IModelingLayoutMapper modelingLayoutMapper;
    @Autowired
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
    @Autowired
    private IRenovationManualMemberMapper renovationManualMemberMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    /*
     * 切换房产
     */
    public ServerResponse setSelectHouse(String userToken, String cityId, String houseId){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", accessToken.getMember().getId());
        List<House> houseList = iHouseMapper.selectByExample(example);
        for (House house : houseList){
            if (house.getId().equals(houseId)){
                house.setIsSelect(1);//改为选择
            }else {
                house.setIsSelect(0);
            }
            iHouseMapper.updateByPrimaryKeySelective(house);
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /*
     * 房产列表
     */
    public ServerResponse getHouseList(String userToken, String cityId){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", accessToken.getMember().getId()).andEqualTo("visitState", 1);
        List<House> houseList = iHouseMapper.selectByExample(example);
        List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
        for (House house : houseList){
            Map<String,String> map = new HashMap<String, String>();
            map.put("houseId" , house.getId());
            map.put("houseName" , house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber());
            map.put("task" , getTask(house.getId())+"");
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }
    //待处理任务
    private int getTask(String houseId){
        int task = 0;
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("workType", 3).andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        task = houseFlowList.size();
        //还有补货 补人工 待审核待查询
        return task;
    }

    /*
     * APP我的房产
     */
    public ServerResponse getMyHouse(String userToken, String cityId){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Member member = accessToken.getMember();
        //该城市该用户所有开工房产
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", member.getId())
                .andEqualTo("visitState", 1);
        List<House> houseList = iHouseMapper.selectByExample(example);
        String houseId = null;
        if (houseList.size() > 1){
            for (House house : houseList){
                if(house.getIsSelect() == 1){//当前选中
                    houseId = house.getId();
                    break;
                }
            }
            if(houseId == null){//有很多房子但是没有isSelect为1的
                houseId = houseList.get(0).getId();
            }
        }else if(houseList.size() == 1){
            houseId = houseList.get(0).getId();
        }else{
           return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),"暂无房产");
        }
        House house = iHouseMapper.selectByPrimaryKey(houseId);

        //统计几套房
        int again = houseList.size();
        HouseResult houseResult = new HouseResult();
        houseResult.setHouseName(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber());
        houseResult.setAgain(again);
        houseResult.setHouseId(houseId);
        /*
         * 其它房产待处理任务 列表状态 待完成
         */
        houseResult.setTask(2);
        houseResult.setState("11011");
        example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Course> courseList = new ArrayList<Course>();
        for (HouseFlow houseFlow : houseFlowList){
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            if(workerType != null){
                Course course = new Course();
                course.setNameA(workerType.getName());
                course.setNameB("节点名");
                course.setNameC("工序详情");
                course.setUrl("url");
                course.setTotal(4);
                course.setRank(2);
                course.setColor(workerType.getColor());
                course.setState(1);
                courseList.add(course);
            }
        }
        houseResult.setCourseList(courseList);
        return ServerResponse.createBySuccess("查询成功", houseResult);
    }

    /**
     * 开工页面
     */
    public ServerResponse startWorkPage(HttpServletRequest request,String houseId){
        HouseDTO houseDTO = iHouseMapper.startWorkPage(houseId);
        if(StringUtil.isNotEmpty(houseDTO.getReferHouseId())){
            House house = iHouseMapper.selectByPrimaryKey(houseDTO.getReferHouseId());
            houseDTO.setReferHouseName(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber());
        }
        return ServerResponse.createBySuccess("查询成功", houseDTO);
    }

    /**
     * WEB确认开工
     */
    public ServerResponse startWork(HttpServletRequest request, HouseDTO houseDTO){
        if(houseDTO.getDecorationType() >= 3 || houseDTO.getDecorationType() == 0){
            return ServerResponse.createByErrorMessage("装修类型参数错误");
        }
        if(StringUtils.isEmpty(houseDTO.getHouseId()) || StringUtils.isEmpty(houseDTO.getCityId())
        || StringUtils.isEmpty(houseDTO.getStyle()) || StringUtils.isEmpty(houseDTO.getVillageId())){
            return ServerResponse.createByErrorMessage("参数为空");
        }
        if(houseDTO.getSquare() <= 0){
            return ServerResponse.createByErrorMessage("面积错误");
        }
        House house = iHouseMapper.selectByPrimaryKey(houseDTO.getHouseId());
        house.setCityId(houseDTO.getCityId());
        house.setCityName(houseDTO.getCityName());
        house.setVillageId(house.getVillageId());
        house.setResidential(houseDTO.getResidential());
        house.setModelingLayoutId(houseDTO.getModelingLayoutId());
        house.setBuilding(houseDTO.getBuilding());
        house.setUnit(houseDTO.getUnit());
        house.setNumber(houseDTO.getNumber());
        house.setSquare(new BigDecimal(houseDTO.getSquare()));
        house.setReferHouseId(houseDTO.getReferHouseId());
        house.setStyle(houseDTO.getStyle());
        house.setHouseType(houseDTO.getHouseType());
        house.setDrawings(houseDTO.getDrawings());
        house.setDecorationType(house.getDecorationType());
        HouseFlow houseFlow = null;
        try {
            if(houseDTO.getDecorationType() == 1){//远程设计
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("1");
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo("houseId", houseDTO.getHouseId()).andEqualTo("workerTypeId", workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                if(houseFlowList.size() > 1){
                    return ServerResponse.createByErrorMessage("设计异常,请联系平台部");
                }else if(houseFlowList.size() == 1){
                    houseFlow = houseFlowList.get(0);
                    houseFlow.setReleaseTime(new Date());//发布时间
                    houseFlow.setMemberId(house.getMemberId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setSafe(workerType.getSafeState());
                    houseFlow.setWorkType(2);//开始设计等待被抢
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                }else {
                    houseFlow = new HouseFlow();
                    houseFlow.setReleaseTime(new Date());//发布时间
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setMemberId(house.getMemberId());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setSafe(workerType.getSafeState());
                    houseFlow.setWorkType(2);//开始设计等待被抢
                    houseFlowMapper.insert(houseFlow);
                }
            }else if(house.getDecorationType() == 2){//自带设计,通知精算师
                house.setDesignerOk(1);
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("2");
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo("houseId", house.getId()).andEqualTo("workerTypeId", workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                if(houseFlowList.size() > 1) {
                    return ServerResponse.createByErrorMessage("生成精算houseFlow异常,请联系平台部");
                }else if(houseFlowList.size() == 0){
                    houseFlow = new HouseFlow();
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setMemberId(house.getMemberId());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setSafe(workerType.getSafeState());
                    houseFlow.setWorkType(3);//自动抢单待支付精算费
                    houseFlowMapper.insert(houseFlow);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }

        house.setVisitState(1);//开工成单
        iHouseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /*
     * APP开始装修
     */
    public ServerResponse setStartHouse(String userToken, String cityId, int houseType, int drawings){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        String memberId = accessToken.getMemberId();
        if(StringUtils.isEmpty(memberId)){
            return ServerResponse.createByErrorMessage("用户id不存在");
        }
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<House> houseList = iHouseMapper.selectByExample(example);
        int again = 1;
        if(houseList.size() > 0){
            again += houseList.size();
            for (House house : houseList){
                if(house.getVisitState() != 1 ){ //visitState 1开工，2有意向，3无装修需求，4恶意操作，默认为0
                    return ServerResponse.createByErrorMessage("有房子未确认开工,不能再装");
                }
            }
        }
        City city = iCityMapper.selectByPrimaryKey(cityId);
        House house = new House();//新增房产信息
        if (houseList.size() > 0){
            house.setIsSelect(0);
        }
        house.setMemberId(memberId);//用户id
        house.setCityName(city.getName());//城市名
        house.setCityId(cityId);
        house.setAgain(again);//第几套房产
        house.setHouseType(houseType);//装修的房子类型0：新房；1：老房
        house.setDrawings(drawings);//有无图纸0：无图纸；1：有图纸
        iHouseMapper.insert(house);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 房子装修列表
     */
    public ServerResponse getList(HttpServletRequest request, String memberId){
        List<Map<String,Object>> mapList = iHouseMapper.getList(memberId);
        return ServerResponse.createBySuccess("查询用户列表成功", mapList);
    }

    /**
     * 修改房子精算状态
     */
    public ServerResponse setHouseBudgetOk(String houseId, Integer budgetOk){
        try {
            House house = iHouseMapper.selectByPrimaryKey(houseId);
            if(house==null){
                return ServerResponse.createByErrorMessage("修改房子精算状态失败");
            }
            house.setBudgetOk(budgetOk);//精算状态:-1已精算没有发给业主,默认0未开始,1已开始精算,2已发给业主,3审核通过,4审核不通过
            iHouseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("修改房子精算状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改房子精算状态失败");
        }
    }

    //根据城市，小区，最小最大面积查询房子
    public ServerResponse queryHouseByCity(String userToken,String cityId,String villageId,Double minSquare,Double maxSquare,Integer pageNum, Integer pageSize) {
        try{
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            PageHelper.startPage(pageNum, pageSize);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            List<ShareDTO> srlist = new ArrayList<ShareDTO>();
            List<House> hlist=new ArrayList<House>();
                hlist = iHouseMapper.getSameLayout(cityId,villageId,minSquare,maxSquare);
                for(House house : hlist){
                    ShareDTO sr = new ShareDTO();
                    ModelingLayout ml = modelingLayoutMapper.selectByPrimaryKey(house.getModelingLayoutId());
                    sr.setType("1");

                    if(house.getShowHouse()==0){
                        if(accessToken!=null) {
                            String houseName = (house.getResidential() == null ? "*" : house.getResidential())
                                    + (house.getBuilding() == null ? "*" : house.getBuilding()) + "栋"
                                    + (house.getUnit() == null ? "*" : house.getUnit()) + "单元" + (house.getNumber() == null ? "*" : house.getNumber()) + "号";
                            sr.setName(houseName);
                        }else{
                            String houseName = (house.getResidential() == null ? "*" : house.getResidential())
                                    + (house.getBuilding() == null ? "*" : house.getBuilding()) + "栋"
                                    + (house.getUnit() == null ? "*" : house.getUnit()) + "单元**号";
                            sr.setName(houseName);
                        }
                    }else{
                        sr.setName("*栋*单元*号");
                    }
                    Example example =new Example(HouseDesignImage.class);
                    example.createCriteria().andEqualTo("houseId",house.getId());
                    sr.setImageNum(houseDesignImageMapper.selectByExample(example).size()+"张图片");
                    sr.setJianzhumianji("建筑面积:"+(house.getBuildSquare()==null?"0":house.getBuildSquare())+"m²");//建筑面积
                    sr.setJvillageacreage("计算面积:"+(house.getSquare()==null?"0":house.getSquare())+"m²");//计算面积
                    String biaoqian=house.getLiangDian();//标签
                    String ss[]=new String[]{};
                    String ss2[]=new String[4];
                    int j=0;
                    if(biaoqian!=null&&!biaoqian.equals("")){
                        ss=biaoqian.substring(1, biaoqian.length()-1).split(",");
                    }
                    for(int i=0;i<ss.length;i++){
                        if(!ss[i].equals("无")&&ss[i]!=null&&!ss[i].equals("null")){
                            ss2[i]=ss[i];
                            j++;
                        }
                    }
                    int index=0;
                    String ss3[]=new String[j];
                    for (String string : ss2) {
                        if(string!=null && !string.equals("")){
                            ss3[index]=string;
                            index++;
                        }
                    }
                    sr.setBiaoqian(ss3);//亮点标签
                    if(accessToken!=null) {
                        sr.setPrice("0.00");//精算总价
                    }else{
                        sr.setPrice("****￥");//精算总价
                    }
                    sr.setHouseId(house.getId());
                    sr.setVillageId(house.getVillageId());//小区id
                    sr.setVillageName(house.getResidential());//小区名
                    sr.setLayoutId(house.getModelingLayoutId());//户型id
                    sr.setLayoutleft(ml==null?"":ml.getName());//户型名称
                    sr.setShow("免费使用");
                    sr.setShowHouse(house.getShowHouse());
                    sr.setUrl("www.baidu.com");
                    if(houseDesignImageMapper.selectByExample(example)!=null&&houseDesignImageMapper.selectByExample(example).size()>0){
                        sr.setImage(address + houseDesignImageMapper.selectByExample(example).get(0).getImageurl());//户型图片
                    }else{
                        sr.setImage("");//户型图片
                    }
                    srlist.add(sr);
                }
            PageInfo pageResult = new PageInfo(hlist);
            pageResult.setList(srlist);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }

    //装修指南
   public ServerResponse getRenovationManual(Integer type,String houseId) {
        try{
            Map<String, Object> returnMap=new HashMap<String, Object>();//返回对象
            List<Map<String, Object>> workerTypeList=new ArrayList<Map<String,Object>>();
            if(houseId==null||"".equals(houseId)|| houseFlowMapper.getHouseNot0(houseId).size()==0){//houseId为空查全部指南
                Example example =new Example(WorkerType.class);
                example.createCriteria().andEqualTo("state",0);
                List<WorkerType> wtList=workerTypeMapper.selectByExample(example);
                returnMap.put("isSava", 0);//0:不可保存;1:可保存
                for(WorkerType wt:wtList){
                    if(wt.getType()==1||wt.getType()==2||wt.getType()==3){
                        continue;
                    }
                    List<RenovationManual> listR= renovationManualMapper.getRenovationManualByWorkertyId(wt.getId());
                    Map<String, Object> wMap=new HashMap<String, Object>();
                    wMap.put("workerTypeName", wt.getName());
                    List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
                    for(RenovationManual r:listR){
                        Map<String, Object> map=new HashMap<String, Object>();
                        map.put("name", r.getName());//指南名称
                        map.put("urlName", r.getUrlName());//链接名称
                        map.put("url", r.getUrlName());//链接地址
                        map.put("isSelect", 0);//未选中
                        map.put("id", r.getId());//指南id
                        listMap.add(map);
                    }
                    wMap.put("rList", listMap);
                    workerTypeList.add(wMap);
                }
                returnMap.put("list", workerTypeList);//大list
            }else{//否则只查询施工中的指南
                List<HouseFlow> hfList = houseFlowMapper.getHouseNot0(houseId);//查询施工中的
                returnMap.put("isSava", 1);//0:不可保存;1:可保存
                for(HouseFlow hf:hfList){
                    String workerTypeId=hf.getWorkerTypeId();//工种id
                    if(workerTypeId!=null){
                        List<RenovationManual> rlist= renovationManualMapper.getRenovationManualByWorkertyId(workerTypeId);
                        Map<String, Object> wMap=new HashMap<String, Object>();
                        WorkerType workerType=workerTypeMapper.selectByPrimaryKey(workerTypeId);
                        if(workerType.getType()==1||workerType.getType()==2||workerType.getType()==3){
                            continue;
                        }
                        wMap.put("workerTypeName", workerType.getName());
                        List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
                        for(RenovationManual r:rlist){
                            Map<String, Object> map=new HashMap<String, Object>();
                            Example example=new Example(RenovationManualMember.class);
                            example.createCriteria().andEqualTo("renovationManualId",r.getId()).andEqualTo("houseId",houseId);
                            List<RenovationManualMember> rmList=renovationManualMemberMapper.selectByExample(example);
                            RenovationManualMember rm = new RenovationManualMember();
                            if(rmList.size()>0){
                                rm=rmList.get(0);
                            }
                            if(type==1){//如果只查未勾选
                                if(rm!=null&&rm.getState()==1){//跳过已勾选
                                    continue;
                                }
                            }
                            if(rm!=null){
                                if(rm.getState()==1){//已勾选
                                    map.put("isSelect", 1);//选中
                                }else{
                                    map.put("isSelect", 0);//未选中
                                }
                            }else{
                                map.put("isSelect", 0);//未选中
                            }
                            map.put("id", r.getId());//指南id
                            map.put("name", r.getName());//指南名称
                            map.put("urlName", r.getUrlName());//链接名称
                            map.put("url", r.getUrlName());//链接地址
                            listMap.add(map);
                        }
                        wMap.put("rList", listMap);
                        workerTypeList.add(wMap);
                    }
                }
                returnMap.put("list", workerTypeList);//大list
            }
            return ServerResponse.createBySuccess("获取装修指南成功",returnMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取装修指南失败");
        }
    }

    /**
     * 保存装修指南
     * @param houseId
     * @param savaList
     * @return
     */
    public ServerResponse savaRenovationManual(String houseId,String savaList){
        try{
            if(houseId==null||"".equals(houseId)){
                return ServerResponse.createByErrorMessage("系统出错,houseId不能为空");
            }
            House house=iHouseMapper.selectByPrimaryKey(houseId);
            if(savaList!=null){
                JSONArray jsonArr=JSONArray.parseArray(savaList);//格式化jsonArr
                for(int i=0;i<jsonArr.size();i++){
                    JSONObject obj=jsonArr.getJSONObject(i);
                    Example example=new Example(RenovationManualMember.class);
                    example.createCriteria().andEqualTo("renovationManualId",obj.getString("id")).andEqualTo("houseId",houseId);
                    List<RenovationManualMember> rmList=renovationManualMemberMapper.selectByExample(example);
                    if(rmList.size()==0){//如果为空则新增
                        RenovationManualMember rm=new RenovationManualMember();
                        rm.setHouseId(houseId);
                        rm.setMemberId(house.getMemberId());
                        rm.setRenovationManualId(obj.getString("id"));
                        rm.setState(obj.getInteger("state"));
                        renovationManualMemberMapper.insertSelective(rm);
                    }else{
                        RenovationManualMember rm =rmList.get(0);
                        rm.setState(obj.getInteger("state"));
                        renovationManualMemberMapper.updateByPrimaryKeySelective(rm);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("保存装修指南成功");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,保存装修指南失败");
        }
    }

    /**
     * 施工记录
     */
    public ServerResponse queryConstructionRecord(String houseId,Integer pageNum, Integer pageSize){
        try{
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            PageHelper.startPage(pageNum, pageSize);
            List<HouseFlowApply> hfaList=houseFlowApplyMapper.queryAllHfaByHouseId(houseId);
            List<Map<String,Object>> listMap=new ArrayList<>();
            for(HouseFlowApply hfa:hfaList){
                Map<String,Object> map = new HashMap<>();
                map.put("id",hfa.getId());
                Member member=memberMapper.selectByPrimaryKey(hfa.getWorkerId());
                map.put("workerHead",address+member.getHead());//工人头像
                map.put("workerTypeName",workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
                map.put("workerName",member.getName());//工人名称
                Example example=new Example(HouseWorker.class);
                example.createCriteria().andEqualTo("houseId",hfa.getHouseId()).andEqualTo("workerId",hfa.getWorkerId());
                List<HouseWorker> listHw=houseWorkerMapper.selectByExample(example);
                if(listHw.size()>0){
                    HouseWorker houseWorker = listHw.get(0);
                    if(houseWorker.getWorkType()==4){
                        map.put("isNormal","已更换");//施工状态
                    }else{
                        map.put("isNormal","正常施工");
                    }
                }else{
                    map.put("isNormal","正常施工");
                }
                map.put("content",hfa.getApplyDec());
                Example example2=new Example(HouseFlowApplyImage.class);
                example2.createCriteria().andEqualTo("houseFlowApplyId",hfa.getId());
                List<HouseFlowApplyImage> hfaiList= houseFlowApplyImageMapper.selectByExample(example2);
                String[] imgArr=new String[hfaiList.size()];
                for(int i=0;i<hfaiList.size();i++){
                    HouseFlowApplyImage hfai=hfaiList.get(i);
                    String string = hfai.getImageUrl();
                    imgArr[i]=address+string;
                }
                map.put("imgArr",imgArr);
                if(hfa.getApplyType()==0){//0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
                    map.put("applyType","每日完工申请");
                }else if(hfa.getApplyType()==1){
                    map.put("applyType","阶段完工申请");
                }else if(hfa.getApplyType()==2){
                    map.put("applyType","整体完工申请");
                }else if(hfa.getApplyType()==3){
                    map.put("applyType","停工申请");
                }else if(hfa.getApplyType()==4){
                    map.put("applyType","每日开工");
                }else if(hfa.getApplyType()==5||hfa.getApplyType()==6||hfa.getApplyType()==7){
                    map.put("applyType","巡查");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("createDate",sdf.format(hfa.getCreateDate()));
                listMap.add(map);
            }
            return ServerResponse.createBySuccess("查询施工记录成功",listMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
    }

    /**
     * 根据id查询房子信息
     * @return
     */
    public House getHouseById(String houseId){
        try{
            return iHouseMapper.selectByPrimaryKey(houseId);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
