package com.dangjia.acg.service.sale.store;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.sale.residential.ResidentialBuildingMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:58
 */
@Service
public class StoreManagementService {

    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;//小区
    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;
    @Autowired
    private SaleService saleService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;

    public ServerResponse storeManagementPage(String userToken, PageDTO pageDTO) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;
        List<StoreUserDTO> storeUserDTOS = iStoreUserMapper.getStoreUsers(store.getId(), null,4);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (StoreUserDTO storeUserDTO : storeUserDTOS) {
            String imageUrl = storeUserDTO.getUserHead();
            storeUserDTO.setUserHead(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        Example example = new Example(ModelingVillage.class);
        example.createCriteria().andIn(ModelingVillage.ID, Arrays.asList(store.getVillages().split(",")));
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ModelingVillage> modelingVillages = modelingVillageMapper.selectByExample(example);
        List<ResidentialRangeDTO> residentialRangeDTOList=new ArrayList<>();
        for (ModelingVillage modelingVillage : modelingVillages) {
            ResidentialRangeDTO residentialRangeDTO=new ResidentialRangeDTO();
            residentialRangeDTO.setVillageId(modelingVillage.getId());
            residentialRangeDTO.setVillagename(modelingVillage.getName());
            example = new Example(ResidentialBuilding.class);
            example.createCriteria().andEqualTo(ResidentialBuilding.STORE_ID,store.getId())
                    .andEqualTo(ResidentialBuilding.VILLAGE_ID,modelingVillage.getId());
            residentialRangeDTO.setList(residentialBuildingMapper.selectByExample(example));
            residentialRangeDTOList.add(residentialRangeDTO);
        }
        PageInfo pageResult = new PageInfo(residentialRangeDTOList);
        Map<String,Object> resultMap=new HashedMap();
        resultMap.put("storeUsers",storeUserDTOS);
        resultMap.put("residentialRangeDTOList",pageResult);
        resultMap.put("managerId",store.getUserId());
        resultMap.put("storeId",store.getId());
        return ServerResponse.createBySuccess("查询成功",resultMap);
    }


    public ServerResponse  addBuilding(String villageId, Date modifyDate, String building,String storeId) {
        Example example=new Example(ResidentialBuilding.class);
        example.createCriteria().andEqualTo(ResidentialBuilding.VILLAGE_ID,villageId)
                                .andEqualTo(ResidentialBuilding.STORE_ID,storeId)
                                .andEqualTo(ResidentialBuilding.BUILDING,building);
        if(residentialBuildingMapper.selectByExample(example).size()>0){
            return ServerResponse.createByErrorMessage("改楼栋已存在");
        }
        ResidentialBuilding residentialBuilding=new ResidentialBuilding();
        residentialBuilding.setVillageId(villageId);
        residentialBuilding.setModifyDate(CommonUtil.isEmpty(modifyDate)?null:modifyDate);
        residentialBuilding.setBuilding(building);
        residentialBuilding.setDataStatus(0);
        residentialBuilding.setStoreId(storeId);
        if(residentialBuildingMapper.insert(residentialBuilding)>0){
            return ServerResponse.createBySuccessMessage("添加成功");
        }
        return ServerResponse.createByErrorMessage("添加失败");
    }

}
