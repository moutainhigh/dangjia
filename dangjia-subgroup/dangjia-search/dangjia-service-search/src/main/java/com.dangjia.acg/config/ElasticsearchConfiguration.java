package com.dangjia.acg.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: QiYuXiang
 * @date: 2018/5/14
 */
@Configuration
public class ElasticsearchConfiguration implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {
  private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

  @Value("${spring.data.elasticsearch.cluster-nodes}")
  private String clusterNodes ;

  @Value("${spring.data.elasticsearch.cluster-name}")
  private String clusterName;

  public static TransportClient client;

  @Override
  public void destroy() throws Exception {
    try {
      if (client != null) {
        client.close();
      }
    } catch (final Exception e) {
      logger.error("Error closing ElasticSearch client: ", e);
    }
  }

  @Override
  public TransportClient getObject() throws Exception {
    return client;
  }

  @Override
  public Class<TransportClient> getObjectType() {
    return TransportClient.class;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    buildClient();
  }

  protected void buildClient()  {
    try {
      PreBuiltTransportClient preBuiltTransportClient = new PreBuiltTransportClient(settings());
      if (!"".equals(clusterNodes)){
        for (String nodes:clusterNodes.split(",")) {
          String InetSocket [] = nodes.split(":");
          String  address = InetSocket[0];
          Integer  port = Integer.valueOf(InetSocket[1]);
          preBuiltTransportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(address),port));
        }
        client = preBuiltTransportClient;
      }
    } catch (Exception e) {
      logger.error("链接异常：",e);
    }
  }
  /**
   * 初始化默认的client
   */
  private Settings settings(){
    Settings settings = Settings.builder()
        .put("cluster.name",clusterName).build();
    client = new PreBuiltTransportClient(settings);
    return settings;
  }
}
