package com.dangjia.acg.service.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.product.DjBasicsMaintainMapper;
import com.dangjia.acg.modle.product.DjBasicsMaintain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public ServerResponse addKeywords(String keywordName, String searchItem){

        DjBasicsMaintain djBasicsMaintain=new DjBasicsMaintain();
        djBasicsMaintain.setKeywordName(keywordName);
        djBasicsMaintain.setSearchItem(searchItem);
        return null;
    }
}
