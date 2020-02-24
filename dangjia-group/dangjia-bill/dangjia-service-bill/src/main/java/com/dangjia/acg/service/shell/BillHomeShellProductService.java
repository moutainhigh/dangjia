package com.dangjia.acg.service.shell;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.mapper.shell.IBillHomeShellProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:31
 */
@Service
public class BillHomeShellProductService {
    protected static final Logger logger = LoggerFactory.getLogger(BillHomeShellProductService.class);

    @Autowired
    private IBillHomeShellProductMapper billHomeShellProductMapper;
    /**
     * 当家贝商品列表
     * @param pageDTO 分页
     * @param productType 商品类型：1实物商品 2虚拟商品
     * @param searchKey 商品名称/编码
     * @return
     */
    public ServerResponse queryHomeShellProductList( PageDTO pageDTO, String productType, String searchKey){
        try{

            return ServerResponse.createBySuccess("查询成功","");
        }catch(Exception e){
            logger.error("查询失败");
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }

    /**
     * 商品详情
     * @param shellProductId 当家贝商品ID
     * @return
     */
    public ServerResponse queryHomeShellProductInfo(String shellProductId){
        try{

            return ServerResponse.createBySuccess("查询成功","");
        }catch(Exception e){
            logger.error("查询失败");
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }

    /**
     * 添加修改商品
     * @param homeShellProductDTO 商品内容
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editHomeShellProductInfo( HomeShellProductDTO homeShellProductDTO,String cityId){

        return ServerResponse.createBySuccessMessage("保存成功");
    }

    /**
     * 删除商品(修改商品的状态为删除状态）
     * @param shellProductId 当家贝商品ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteHomeShellProduct(String shellProductId){

        return ServerResponse.createBySuccessMessage("删除成功");
    }

}
