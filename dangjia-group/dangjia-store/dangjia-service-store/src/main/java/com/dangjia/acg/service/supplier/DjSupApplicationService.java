package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.supplier.DjSupApplicationMapper;
import com.dangjia.acg.mapper.supplier.DjSupplierMapper;
import com.dangjia.acg.modle.supplier.DjSupApplication;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:46
 */
@Service
public class DjSupApplicationService {

    @Autowired
    private DjSupApplicationMapper djSupApplicationMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private DjSupplierMapper djSupplierMapper;





    /**
     *  店铺-审核供货列表
     * @param pageDTO
     * @param keyWord
     * @param shopId
     * @return
     */
    public ServerResponse queryDjSupApplicationProductByShopID(PageDTO pageDTO, String keyWord, String shopId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example=new Example(DjSupApplication.class);
            example.createCriteria().andEqualTo(DjSupApplication.SHOP_ID,shopId)
                    .andEqualTo(DjSupApplication.DATA_STATUS,0)
                    .andEqualTo(DjSupApplication.APPLICATION_STATUS,1);
            List<DjSupApplication> djSupApplications = djSupApplicationMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(djSupApplications);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商申请供应店铺
     * @param userId
     * @param cityId
     * @param shopId
     * @return
     */
    public ServerResponse insertSupplierApplicationShop(String userId,String cityId, String shopId) {
        try {
            DjSupplier djSupplier = djSupplierMapper.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无供应商信息");
            Example example=new Example(DjSupApplication.class);
            example.createCriteria().andEqualTo(DjSupApplication.SUP_ID,djSupplier.getId())
                    .andEqualTo(DjSupApplication.SHOP_ID,shopId)
                    .andNotEqualTo(DjSupApplication.APPLICATION_STATUS,2)
                    .andEqualTo(DjSupApplication.DATA_STATUS,0);
            if(djSupApplicationMapper.selectByExample(example).size()>0)
                return ServerResponse.createByErrorMessage("请勿重复申请");
            example=new Example(DjSupApplication.class);
            example.createCriteria().andEqualTo(DjSupApplication.SUP_ID,djSupplier.getId())
                    .andEqualTo(DjSupApplication.SHOP_ID,shopId)
                    .andEqualTo(DjSupApplication.APPLICATION_STATUS,2)
                    .andEqualTo(DjSupApplication.DATA_STATUS,0);
            DjSupApplication djSupApplication1 = djSupApplicationMapper.selectOneByExample(example);
            if(null!=djSupApplication1){
                djSupApplication1.setApplicationStatus("0");
                djSupApplicationMapper.updateByPrimaryKeySelective(djSupApplication1);
                return ServerResponse.createBySuccessMessage("申请成功");
            }
            DjSupApplication djSupApplication=new DjSupApplication();
            djSupApplication.setShopId(shopId);
            djSupApplication.setSupId(djSupplier.getId());
            djSupApplication.setDataStatus(0);
            djSupApplication.setApplicationStatus("0");
            djSupApplication.setCityId(cityId);
            if(djSupApplicationMapper.insert(djSupApplication)>0)
                return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败:"+e);
        }
        return ServerResponse.createByErrorMessage("申请失败");
    }


    /**
     * 上传合同
     * @param id
     * @param contract
     * @return
     */
    public ServerResponse uploadContracts(String id,String contract) {
        try {
            DjSupApplication djSupApplication=new DjSupApplication();
            djSupApplication.setId(id);
            djSupApplication.setContract(contract);
            djSupApplicationMapper.updateByPrimaryKeySelective(djSupApplication);
            return ServerResponse.createBySuccessMessage("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败:"+e);
        }
    }


    /**
     * 查看合同
     * @param id
     * @return
     */
    public ServerResponse queryContracts(String id) {
        try {
            DjSupApplication djSupApplication = djSupApplicationMapper.selectByPrimaryKey(id);
            if(djSupApplication==null)
            {
                return ServerResponse.createByErrorMessage("没有查询到合同!");
            }
            if(StringUtils.isNotBlank(djSupApplication.getContract())) {
                String[] split = djSupApplication.getContract().split(",");
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (int i = 0; i < split.length; i++) {
                    split[i] = imageAddress + split[i];
                }
                return ServerResponse.createBySuccess("查询成功",split);
            }
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败:"+e);
        }
    }
}
