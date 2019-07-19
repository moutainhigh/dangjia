package com.dangjia.acg.sql.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.util.StringUtils;
import com.dangjia.acg.util.DataSourceType;
import com.dangjia.acg.util.DynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AutoConfigureBefore(SessionAutoConfiguration.class)
public class DruidConfig {
    private Logger logger = LoggerFactory.getLogger(DruidConfig.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.filters}")
    private String filters;

    @Value("{spring.datasource.connectionProperties}")
    private String connectionProperties;


    @Bean(value = "dataSource")
    @Primary    //优先使用，多数据源
    public DataSource dataSource() {
        DataSource dataSource=getDruidDataSource(username, password, dbUrl);
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        //配置多数据源
        Map<Object,Object> map = new HashMap<>();
        map.put(DataSourceType.DANGJIA.getName(), dataSource);	//key需要跟ThreadLocal中的值对应

        //长沙数据源
        if(!StringUtils.isEmpty(dbUrl)) {
            DataSource csDataSource = getDruidDataSource(username, password,dbUrl);
            if (csDataSource != null) {
                map.put(DataSourceType.CS_CHANGSHA.getName(), csDataSource);
            }
        }

        //株洲数据源
        String zzdbUrl=dbUrl.replaceAll("changsha","zhuzhou");
        DataSource csDataSource = getDruidDataSource(username, password,zzdbUrl);
        if (csDataSource != null) {
            map.put(DataSourceType.ZZ_CHANGSHA.getName(), csDataSource);
        }
        //深圳数据源
        String szdbUrl=dbUrl.replaceAll("changsha","shenzhen");
        csDataSource = getDruidDataSource(username, password,szdbUrl);
        if (csDataSource != null) {
            map.put(DataSourceType.SZ_CHANGSHA.getName(), csDataSource);
        }
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }


    public DataSource getDruidDataSource(String username, String password, String dbUrl){
        DruidDataSource datasource = new DruidDataSource();

        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setInitialSize(this.initialSize);
        datasource.setMinIdle(this.minIdle);
        datasource.setMaxActive(this.maxActive);
        datasource.setMaxWait(this.maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        datasource.setValidationQuery(this.validationQuery);
        datasource.setTestWhileIdle(this.testWhileIdle);
        datasource.setTestOnBorrow(this.testOnBorrow);
        datasource.setTestOnReturn(this.testOnReturn);
        datasource.setPoolPreparedStatements(this.poolPreparedStatements);
        datasource.setQueryTimeout(30);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(this.maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(this.filters);
        } catch (SQLException e) {
            this.logger.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(this.connectionProperties);

        return datasource;
    }
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();

        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return filterRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", "QiYuXiang");
        reg.addInitParameter("loginPassword", "123456");
        return reg;
    }
}
