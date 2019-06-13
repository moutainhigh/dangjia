package com.dangjia.acg.service.home;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import org.springframework.stereotype.Service;

/**
 * @author Ruking.Cheng
 * @descrilbe TODO
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 3:22 PM
 */
@Service
public class HomeService {


    public ServerResponse getAppHomeCollocation() {
        return null;
    }

    public ServerResponse setAppHomeCollocation(String userId, String masterpieceIds) {
        return null;
    }

    public ServerResponse getAppHomeCollocationHistory(PageDTO pageDTO) {
        return null;
    }

    public ServerResponse getHomeMasterplateList(PageDTO pageDTO) {
        return null;
    }

    public ServerResponse addHomeMasterplate(String name, String image, String url, String userId) {
        return null;
    }

    public ServerResponse delHomeMasterplate(String id) {
        return null;
    }

    public ServerResponse upDataHomeMasterplate(String id, String name, String image, String url, String userId) {
        return null;
    }
}
