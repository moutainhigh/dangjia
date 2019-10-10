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
     * 根据供应商查询关联店铺
     * @param supId
     * @return
     */
    public List<DjSupApplication> queryDjSupApplicationBySupId(String supId) {
        try {
            Example example=new Example(DjSupApplication.class);
            example.createCriteria().andEqualTo(DjSupApplication.SUP_ID,supId)
                    .andEqualTo(DjSupApplication.DATA_STATUS,0);
            List<DjSupApplication> djSupApplications = djSupApplicationMapper.selectByExample(example);
            if(djSupApplications.size()>0)
                return djSupApplications;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


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
}
