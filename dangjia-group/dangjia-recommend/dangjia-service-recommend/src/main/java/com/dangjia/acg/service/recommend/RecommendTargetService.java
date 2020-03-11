package com.dangjia.acg.service.recommend;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.common.enums.RecommendTargetType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductViewDTO;
import com.dangjia.acg.mapper.recommend.IRecommendTargetMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseChoiceCase;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import com.dangjia.acg.service.home.HomeModularService;
import com.dangjia.acg.service.house.HouseChoiceCaseService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.storefront.StorefrontProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 推荐目标服务类
 * @author: luof
 * @date: 2020-3-9
 */
@Service
public class RecommendTargetService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(RecommendTargetService.class);

    @Autowired
    private IRecommendTargetMapper recommendTargetMapper;

    @Autowired
    private StorefrontProductService storefrontProductService;

    @Autowired
    private HomeModularService homeModularService;

    @Autowired
    private HouseChoiceCaseService houseChoiceCaseService;

    @Autowired
    private HouseService houseService;

    /**
     * @Description: 查询推荐目标列表
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse queryList(String itemSubId, Integer targetType, String targetName) {

        if( null == itemSubId || itemSubId.equals("") ){
            return ServerResponse.createByErrorMessage("参数[子项id]为空!");
        }

        List<RecommendTargetInfo> recommendTargetInfoList = recommendTargetMapper.queryList(itemSubId, targetType, targetName);

        if ( recommendTargetInfoList == null || recommendTargetInfoList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", recommendTargetInfoList);
    }

    /**
     * @Description: 删除单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse deleteSingle(String id){

        if( null == id || id.equals("") ){
            return ServerResponse.createByErrorMessage("参数[id]为空!");
        }

        int rows = recommendTargetMapper.deleteSingle(id);

        if( rows != 1 ){
            return ServerResponse.createByErrorMessage("删除失败");
        }
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    /**
     * @Description: 查询单个推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse querySingle(String id){

        if( null == id || id.equals("") ){
            return ServerResponse.createByErrorMessage("参数[id]为空!");
        }

        RecommendTargetInfo recommendTargetInfo = recommendTargetMapper.querySingle(id);

        if ( recommendTargetInfo == null ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", recommendTargetInfo);
    }

    /**
     * @Description: 设置单个推荐目标参数
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse setSingle(String id, Integer sort, Integer clickNumber){

        if( null == id || id.equals("") ){
            return ServerResponse.createByErrorMessage("参数[id]为空!");
        }

        Date now = new Date();
        int rows = recommendTargetMapper.updateSingle(id, now, sort, clickNumber);

        if( rows != 1 ){
            return ServerResponse.createByErrorMessage("设置失败");
        }
        return ServerResponse.createBySuccessMessage("设置成功");
    }

    /**
     * @Description: 查询可选推荐目标列表
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse queryOptionalList(Integer targetType, String targetName, PageDTO pageDTO){

        if( null == targetType || targetType.equals("") ){
            return ServerResponse.createByErrorMessage("参数[targetType]为空!");
        }

        RecommendTargetType type = RecommendTargetType.getInstance(targetType);
        if( null == type ){
            return ServerResponse.createByErrorMessage("参数[targetType]超出范围!");
        }

        List<RecommendTargetInfo> recommendTargetInfoList = null;
        // 商品
        if( RecommendTargetType.GOODS.getCode().intValue() == type.getCode().intValue()){
            recommendTargetInfoList = queryGoodsOptionalList(targetName, pageDTO);
        }

        // 攻略(指南)
        else if( RecommendTargetType.MANUAL.getCode().intValue() == type.getCode().intValue() ){
            recommendTargetInfoList = queryManualOptionalList(targetName, pageDTO);
        }

        // 案例
        else if( RecommendTargetType.CASE.getCode().intValue() == type.getCode().intValue() ){
            recommendTargetInfoList = queryHouseCaseOptionalList(targetName, pageDTO);
        }

        // 工地
        else if( RecommendTargetType.SITE.getCode().intValue() == type.getCode().intValue() ){
            recommendTargetInfoList = queryHouseSiteOptionalList(targetName, pageDTO);
        }

        if( recommendTargetInfoList == null ){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }

        PageInfo pageResult = new PageInfo(recommendTargetInfoList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    // 查询商品可选列表
    private List<RecommendTargetInfo> queryGoodsOptionalList(String name, PageDTO pageDTO){

        ServerResponse serverResponse = storefrontProductService.queryProductGroundByKeyWord(name, pageDTO);

        if( serverResponse.isSuccess() ){
            List<RecommendTargetInfo> recommendTargetInfoList = new ArrayList<RecommendTargetInfo>();
            PageInfo<BasicsStorefrontProductViewDTO> pageResult = (PageInfo)serverResponse.getResultObj();
            List<BasicsStorefrontProductViewDTO> basicsStorefrontProductViewDTOList =pageResult.getList();
            for( BasicsStorefrontProductViewDTO dto : basicsStorefrontProductViewDTOList ){
                RecommendTargetInfo recommendTargetInfo = new RecommendTargetInfo();
                recommendTargetInfo.setTargetId(dto.getId());
                recommendTargetInfo.setTargetName(dto.getProductName());
                recommendTargetInfo.setImage(dto.getStorefrontProduct().getImage());
                recommendTargetInfoList.add(recommendTargetInfo);
            }
            return recommendTargetInfoList;
        }
        return null;
    }

    // 查询攻略（指南）可选列表
    private List<RecommendTargetInfo> queryManualOptionalList(String name, PageDTO pageDTO){

        ServerResponse serverResponse = homeModularService.queryRenovationManualList(name, pageDTO);

        if( serverResponse.isSuccess() ){
            List<RecommendTargetInfo> recommendTargetInfoList = new ArrayList<RecommendTargetInfo>();
            PageInfo<RenovationManual> pageResult =  (PageInfo)serverResponse.getResultObj();
            List<RenovationManual> renovationManualList = pageResult.getList();
            for( RenovationManual renovationManual : renovationManualList ){
                RecommendTargetInfo recommendTargetInfo = new RecommendTargetInfo();
                recommendTargetInfo.setTargetId(renovationManual.getId());
                recommendTargetInfo.setTargetName(renovationManual.getName());
                recommendTargetInfo.setImage(renovationManual.getImage());
                recommendTargetInfoList.add(recommendTargetInfo);
            }
            return recommendTargetInfoList;
        }
        return null;
    }

    // 查询案例可选列表
    private List<RecommendTargetInfo> queryHouseCaseOptionalList(String title, PageDTO pageDTO){

        ServerResponse serverResponse = houseChoiceCaseService.queryHouseChoiceCasesList(title, pageDTO);

        if( serverResponse.isSuccess() ) {
            List<RecommendTargetInfo> recommendTargetInfoList = new ArrayList<RecommendTargetInfo>();
            PageInfo<HouseChoiceCase> pageResult = (PageInfo)serverResponse.getResultObj();
            List<HouseChoiceCase> houseChoiceCaseList = pageResult.getList();
            for( HouseChoiceCase houseChoiceCase : houseChoiceCaseList ){
                RecommendTargetInfo recommendTargetInfo = new RecommendTargetInfo();
                recommendTargetInfo.setTargetId(houseChoiceCase.getId());
                recommendTargetInfo.setTargetName(houseChoiceCase.getTitle());
                recommendTargetInfo.setImage(houseChoiceCase.getImage());
                recommendTargetInfoList.add(recommendTargetInfo);
            }
            return recommendTargetInfoList;
        }
        return null;
    }

    // 查询工地可选列表
    private List<RecommendTargetInfo> queryHouseSiteOptionalList(String residential, PageDTO pageDTO){

        ServerResponse serverResponse = houseService.queryHouseListByResidential(residential, pageDTO);

        if( serverResponse.isSuccess() ) {
            List<RecommendTargetInfo> recommendTargetInfoList = new ArrayList<RecommendTargetInfo>();
            PageInfo<House> pageResult = (PageInfo)serverResponse.getResultObj();
            List<House> houseList = pageResult.getList();
            for( House house : houseList ){
                RecommendTargetInfo recommendTargetInfo = new RecommendTargetInfo();
                recommendTargetInfo.setTargetId(house.getId());
                recommendTargetInfo.setTargetName(house.getHouseName());
                recommendTargetInfo.setImage(house.getImage());
                recommendTargetInfo.setVisitState(house.getVisitState());
                recommendTargetInfoList.add(recommendTargetInfo);
            }
            return recommendTargetInfoList;
        }
        return null;
    }

    /**
     * @Description: 批量新增推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse addBatch(String itemSubId, Integer targetType, ArrayList<RecommendTargetInfo> targetList) {

        try{

            if( null == itemSubId || itemSubId.equals("") ){
                return ServerResponse.createByErrorMessage("参数[itemSubId]为空!");
            }
            if( null == targetType ){
                return ServerResponse.createByErrorMessage("参数[targetType]为空!");
            }
            if( null == targetList || targetList.size() < 1 ){
                return ServerResponse.createByErrorMessage("参数[targetList]为空!");
            }
            for( RecommendTargetInfo target : targetList ){
                if( null == target.getTargetId() ){
                    return ServerResponse.createByErrorMessage("实际内容中有[targetId]为空!");
                }
                if( null == target.getTargetName() ){
                    return ServerResponse.createByErrorMessage("实际内容中有[targetName]为空!");
                }
            }

            Date now = new Date();
            for( RecommendTargetInfo target : targetList ){

                // 过滤重复
                String targetId = target.getTargetId();
                if( recommendTargetMapper.queryCount(itemSubId, targetType, targetId) > 0 ){
                    continue;
                }

                RecommendTargetInfo recommendTargetInfo = new RecommendTargetInfo();
                recommendTargetInfo.setCreateDate(now);
                recommendTargetInfo.setModifyDate(now);
                recommendTargetInfo.setDataStatus(0);
                recommendTargetInfo.setItemSubId(itemSubId);
                recommendTargetInfo.setTargetType(targetType);
                recommendTargetInfo.setTargetId(targetId);
                recommendTargetInfo.setTargetName(target.getTargetName());
                recommendTargetInfo.setImage(target.getImage()==null?null:target.getImage().toString());
                recommendTargetInfo.setSort(target.getSort()==null?0:target.getSort());
                recommendTargetInfo.setClickNumber(target.getClickNumber()==null?0:target.getClickNumber());

                if( recommendTargetMapper.insert(recommendTargetInfo) < 1 ){
                    logger.error("新增操作失败:"+JSON.toJSONString(recommendTargetInfo));
                    return ServerResponse.createByErrorMessage("新增操作失败");
                }
            }

            return ServerResponse.createBySuccessMessage("新增操作成功");

        }catch(Exception e){
            logger.error("新增新增推荐目标,操作失败", e);
            return ServerResponse.createByErrorMessage("新增操作失败");
        }


    }

    /**
     * @Description: 批量新增推荐目标
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse add(RecommendTargetInfo target){

        try {


            if( null == target ){
                return ServerResponse.createByErrorMessage("参数[target]为空!");
            }
            if( null == target.getItemSubId() ){
                return ServerResponse.createByErrorMessage("参数[itemSubId]为空!");
            }
            if( null == target.getTargetType() ){
                return ServerResponse.createByErrorMessage("参数[targetType]为空!");
            }
            if( null == target.getTargetId() ){
                return ServerResponse.createByErrorMessage("参数[targetId]为空!");
            }
            if( null == target.getTargetName() ){
                return ServerResponse.createByErrorMessage("参数[targetName]为空!");
            }

            // 过滤重复
            String targetId = target.getTargetId();
            if( recommendTargetMapper.queryCount(target.getItemSubId(), target.getTargetType(), target.getTargetId()) > 0 ){
                return ServerResponse.createByErrorMessage("目标内容重复!");
            }

            Date now = new Date();
            target.setCreateDate(now);
            target.setModifyDate(now);
            target.setDataStatus(0);
            target.setImage(target.getImage()==null?"":target.getImage());
            target.setSort(target.getSort()==null?0:target.getSort());
            target.setClickNumber(target.getClickNumber()==null?0:target.getClickNumber());

            if( recommendTargetMapper.addSingle(target) < 1 ){
                logger.error("新增操作失败:"+JSON.toJSONString(target));
                return ServerResponse.createByErrorMessage("新增操作失败");
            }
            return ServerResponse.createBySuccessMessage("新增操作成功");
        }catch(Exception e){
            logger.error("新增新增推荐目标,操作失败", e);
            return ServerResponse.createByErrorMessage("新增操作失败");
        }
    }

    /**
     * @Description: 查询推荐目标 分页
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse queryPage(PageDTO pageDTO, String itemSubId, Integer targetType) {

        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<RecommendTargetInfo> recommendTargetInfoList = recommendTargetMapper.queryListOfOrder(itemSubId, targetType, null);
        if ( recommendTargetInfoList == null || recommendTargetInfoList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        PageInfo pageResult = new PageInfo(recommendTargetInfoList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }
}
