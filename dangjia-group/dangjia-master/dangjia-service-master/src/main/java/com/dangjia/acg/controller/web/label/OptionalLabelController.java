package com.dangjia.acg.controller.web.label;

import com.dangjia.acg.api.web.label.OptionalLabelAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
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
    public ServerResponse addOptionalLabel(OptionalLabel optionalLabel) {
        return optionalLabelServices.addOptionalLabel(optionalLabel);
    }

    @Override
    @ApiMethod
    public ServerResponse queryOptionalLabel(String id) {
        return optionalLabelServices.queryOptionalLabel(id);
    }


    @Override
    @ApiMethod
    public ServerResponse delOptionalLabel(String id) {
        return optionalLabelServices.delOptionalLabel(id);
    }

    @Override
    @ApiMethod
    public ServerResponse editOptionalLabel(OptionalLabel optionalLabel) {
        return optionalLabelServices.editOptionalLabel(optionalLabel);
    }
}
