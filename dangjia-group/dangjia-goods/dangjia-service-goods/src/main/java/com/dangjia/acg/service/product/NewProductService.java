package com.dangjia.acg.service.product;

import com.dangjia.acg.mapper.product.INewProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewProductService {
    private static Logger LOG = LoggerFactory.getLogger(NewProductService.class);

    @Autowired
    private INewProductMapper iNewProductMapper;
}
