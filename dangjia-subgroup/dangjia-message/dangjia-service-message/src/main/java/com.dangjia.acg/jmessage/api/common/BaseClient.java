package com.dangjia.acg.jmessage.api.common;

import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.connection.IHttpClient;
import cn.jiguang.common.connection.NativeHttpClient;
import cn.jmessage.api.common.JMessageConfig;
import com.google.gson.Gson;

public class BaseClient {

    protected IHttpClient _httpClient;
    protected String _baseUrl;
    protected Gson _gson = new Gson();

    /**
     * Create a JMessage Base Client
     *
     * @param appKey The KEY of one application on JPush.
     * @param masterSecret API access secret of the appKey.
     * @param proxy The proxy, if there is no proxy, should be null.
     * @param config The client configuration. Can use JMessageConfig.getInstance() as default.
     */
    public BaseClient(String appKey, String masterSecret, HttpProxy proxy, JMessageConfig config) {
        ServiceHelper.checkBasic(appKey, masterSecret);
        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        this._baseUrl = (String) config.get(JMessageConfig.API_HOST_NAME);
        this._httpClient = new NativeHttpClient(authCode, proxy, config.getClientConfig());
    }

    public void setHttpClient(IHttpClient httpClient) {
        this._httpClient = httpClient;
    }

}
