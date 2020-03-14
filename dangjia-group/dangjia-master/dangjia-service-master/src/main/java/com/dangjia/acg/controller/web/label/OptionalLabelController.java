package com.dangjia.acg.controller.web.label;

import com.dangjia.acg.api.web.label.OptionalLabelAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.label.OptionalLabel;
import com.dangjia.acg.service.label.OptionalLabelServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/18
 * Time: 9:50
 */
@RestController
public class OptionalLabelController implements OptionalLabelAPI {
    @Autowired
    private OptionalLabelServices optionalLabelServices;

    @Override
    @ApiMethod
    public ServerResponse addOptionalLabel(String jsonStr) {
        return optionalLabelServices.addOptionalLabel(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse queryOptionalLabel(PageDTO pageDTO) {
        return optionalLabelServices.queryOptionalLabel(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryOptionalLabelById(String id) {
        return optionalLabelServices.queryOptionalLabelById(id);
    }


    @Override
    @ApiMethod
    public ServerResponse delOptionalLabel(String id) {
        return optionalLabelServices.delOptionalLabel(id);
    }

    @Override
    @ApiMethod
    public ServerResponse editOptionalLabel(String jsonStr) {
        try {
            return optionalLabelServices.editOptionalLabel(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("标题已存在");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse queryActuarialOptionalLabel() {
        return optionalLabelServices.queryActuarialOptionalLabel();
    }
}
