package org.refit.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = {
        "classpath:/application.properties", //로컬용
        "file:/home/ubuntu/app-blue/refit.env", //EC2용 (blue)
        "file:/home/ubuntu/app-green/refit.env" //EC2용 (green)
}, ignoreResourceNotFound = true)
@ComponentScan(basePackages = {
        "org.refit.spring.auth",
        "org.refit.spring.receipt",
        "org.refit.spring.mapper",
        "org.refit.spring.test",
        "org.refit.spring.security",
        "org.refit.spring.ceo",
        "org.refit.spring.reward"
})
@MapperScan(basePackages = {"org.refit.spring.mapper"})
public class RootConfig {
    //application 전역에 필요한 Bean은 RootConfig에서 등록
    //@Service, @Repository, @Component 등
    @Value("${jdbc.driver}") String driver;
    @Value("${jdbc.url}") String url;
    @Value("${jdbc.username}") String username;
    @Value("${jdbc.password}") String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setConfigLocation(
                applicationContext.getResource("classpath:/mybatis-config.xml"));
        sqlSessionFactory.setDataSource(dataSource());
        return (SqlSessionFactory) sqlSessionFactory.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
        return manager;
    }

    //OpenAPI 등 외부 HTTP API 서버에 요청을 보낼 수 있도록 Spring에서 제공하는 HTTP 클라이언트인 RestTemplate을 빈으로 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //외부 API 응답(JSON 문자열)을 Java 객체 또는 JsonNode로 파싱하기 위해 ObjectMapper를 빈으로 등록
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
