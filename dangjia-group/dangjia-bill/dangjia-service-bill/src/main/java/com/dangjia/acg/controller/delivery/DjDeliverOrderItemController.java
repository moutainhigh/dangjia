package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliverOrderItemAPI;
import com.dangjia.acg.service.delivery.DjDeliverOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DjDeliverOrderItemController implements DjDeliverOrderItemAPI {

    @Autowired
    private DjDeliverOrderItemService djDeliverOrderItemService;





}
