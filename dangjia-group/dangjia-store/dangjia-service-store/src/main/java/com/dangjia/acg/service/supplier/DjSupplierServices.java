package com.dangjia.acg.service.supplier;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.supplier.DjSupplierMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:22
 */
@Service
public class DjSupplierServices {

    @Autowired
    private DjSupplierMapper djSupplierMapper;


    /**
     * 供应商基础信息维护
     *
     * @param djSupplier
     * @return
     */
    public ServerResponse updateBasicInformation(DjSupplier djSupplier) {
        try {
            if (CommonUtil.isEmpty(djSupplier.getName()))
                return ServerResponse.createByErrorMessage("用户名不能为空");
            if (CommonUtil.isEmpty(djSupplier.getTelephone()))
                return ServerResponse.createByErrorMessage("电话号码不能为空");
            if (CommonUtil.isEmpty(djSupplier.getAddress()))
                return ServerResponse.createByErrorMessage("地址不能为空");
            if (CommonUtil.isEmpty(djSupplier.getEmail()))
                return ServerResponse.createByErrorMessage("邮件不能为空");
            if (CommonUtil.isEmpty(djSupplier.getCheckPeople()))
                return ServerResponse.createByErrorMessage("联系人不能为空");
            if (djSupplierMapper.updateByPrimaryKeySelective(djSupplier) > 0)
                return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
        return ServerResponse.createByErrorMessage("编辑失败");
    }


    /**
     * 选择供货列表
     *
     * @param supId
     * @param searchKey
     * @return
     */
    public ServerResponse querySupplyList(PageDTO pageDTO, String supId, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Storefront> storefronts = djSupplierMapper.querySupplyList(supId, searchKey);
            PageInfo pageResult = new PageInfo(storefronts);
            if (storefronts.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商商品列表
     * @param pageDTO
     * @param supId
     * @return
     */
    public ServerResponse querySupplierGoods(PageDTO pageDTO, String supId) {
        return null;
    }
}