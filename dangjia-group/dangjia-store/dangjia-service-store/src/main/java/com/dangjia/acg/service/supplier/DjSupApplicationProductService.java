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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:46
 */
@Service
public class DjSupApplicationProductService {

    @Autowired
    private DjSupApplicationMapper djSupApplicationMapper;

    /**
     * 查询待审核的供应商品
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse getExaminedProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已供商品
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse getSuppliedProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 全部打回
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse rejectAllProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 部分通过
     *
     * @param request
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse rejectPartProduct(HttpServletRequest request, String supId, String shopId) {
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
