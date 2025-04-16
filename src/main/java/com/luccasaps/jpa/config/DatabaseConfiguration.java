package com.luccasaps.jpa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {


    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${spring.datasource.driver-class-name}")
    String driver;


    //@Bean
//    public DataSource dataSource(){
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//
//        ds.setUrl(url);
//        ds.setUsername(username);
//        ds.setPassword(password);
//        ds.setDriverClassName(driver);
//
//        return ds;
//    }

    //@Bean
//    public DataSource hikariDataSource(){
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(url);
//        config.setUsername(username);
//        config.setPassword(password);
//        config.setDriverClassName(driver);
//
//        config.setMaximumPoolSize(10); // maximo de conexões
//        config.setMinimumIdle(1); // tamaho inicial do pool
//        config.setPoolName("library-db-pool");
//        config.setMaxLifetime(600000);//10 minutos (600 mil ms)
//        config.setConnectionTimeout(60000);//1 minuto (60 mil ms)
//        config.setConnectionTestQuery("SELECT 1");//query teste
//
//        return new HikariDataSource(config);
//    }

}
