package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.supplier.DjSupApplicationMapper;
import com.dangjia.acg.modle.supplier.DjSupApplication;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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



    /**
     * 根据店铺ID查询申请供应商列表
     * @param shopId
     * @return
     */
    public ServerResponse queryDjSupApplicationByShopID(PageDTO pageDTO,String shopId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example=new Example(DjSupApplication.class);
            example.createCriteria().andEqualTo(DjSupApplication.SHOP_ID,shopId)
                    .andEqualTo(DjSupApplication.DATA_STATUS,0);
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
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse insertSupplierApplicationShop(String supId, String shopId) {
        try {
            Example example=new Example(DjSupApplication.class);
            example.createCriteria().andEqualTo(DjSupApplication.SUP_ID,supId)
                    .andEqualTo(DjSupApplication.SHOP_ID,shopId)
                    .andNotEqualTo(DjSupApplication.APPLICATION_STATUS,2)
                    .andEqualTo(DjSupApplication.DATA_STATUS,0);
            if(djSupApplicationMapper.selectByExample(example).size()>0)
                return ServerResponse.createByErrorMessage("请勿重复申请");
            DjSupApplication djSupApplication=new DjSupApplication();
            djSupApplication.setShopId(shopId);
            djSupApplication.setSupId(supId);
            djSupApplication.setDataStatus(0);
            djSupApplication.setApplicationStatus("0");
            if(djSupApplicationMapper.insert(djSupApplication)>0)
                return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败:"+e);
        }
        return ServerResponse.createByErrorMessage("申请失败");
    }
}
