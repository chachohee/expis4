package com.expis.common.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    /*// ERROR : HikariPool-1 - jdbcUrl is required with driverClassName.
    @Bean(name = "routingDataSource")
    public DynamicRoutingDataSource routingDataSource(
            @Qualifier("t50DataSource") DataSource t50DataSource,
            @Qualifier("fa50DataSource") DataSource fa50DataSource ) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> targetDataSource = new HashMap<>();
        targetDataSource.put("T50", t50DataSource);
        targetDataSource.put("FA50", fa50DataSource);

        routingDataSource.setTargetDataSources(targetDataSource);
        routingDataSource.setDefaultTargetDataSource(t50DataSource);

        return routingDataSource;
    }

    @Primary
    @Bean(name="t50DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.t50")
    public DataSource t50DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name="fa50DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.fa50")
    public DataSource fa50DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }*/

    @Bean
    public DataSource dataSource() {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        HikariDataSource t50DataSource = new HikariDataSource();
//        t50DataSource.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/IETM");
        t50DataSource.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/pdb");
        t50DataSource.setUsername("t50");
//        t50DataSource.setPassword("USR!@Block2");
        t50DataSource.setPassword("expist50");
        t50DataSource.setDriverClassName("oracle.jdbc.OracleDriver");

        HikariDataSource fa50DataSource = new HikariDataSource();
        fa50DataSource.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/IETM");
        fa50DataSource.setUsername("fa50");
        fa50DataSource.setPassword("USR!@Block2");
        fa50DataSource.setDriverClassName("oracle.jdbc.OracleDriver");

        HikariDataSource kt1DataSource = new HikariDataSource();
        kt1DataSource.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/pdb");
        kt1DataSource.setUsername("kt1");
        kt1DataSource.setPassword("expiskt1");
        kt1DataSource.setDriverClassName("oracle.jdbc.OracleDriver");

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("T50", t50DataSource);
        targetDataSources.put("FA50", fa50DataSource);
        targetDataSources.put("KT1", kt1DataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(t50DataSource);

        return routingDataSource;
    }
}
