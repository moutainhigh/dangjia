package com.dangjia.acg.service.product;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.product.DjBasicsMaintainMapper;
import com.dangjia.acg.modle.product.DjBasicsMaintain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class DjBasicsMaintainService {
    @Autowired
    private DjBasicsMaintainMapper djBasicsMaintainMapper;


    /**
     * 查询配置关键名称
     * @param name
     * @return
     */
    public ServerResponse queryMatchWord(String name){
        Example example = new Example(DjBasicsMaintain.class);
        if(!CommonUtil.isEmpty(name)){
            example.createCriteria().andLike(DjBasicsMaintain.SEARCH_ITEM, "%" + name + "%");
            List<DjBasicsMaintain> list = djBasicsMaintainMapper.selectByExample(example);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", list);
        }
        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
    }


    public ServerResponse addKeywords(String keywordName, String searchItem){

        DjBasicsMaintain djBasicsMaintain=new DjBasicsMaintain();
        djBasicsMaintain.setKeywordName(keywordName);
        djBasicsMaintain.setSearchItem(searchItem);
        return null;
    }
}
