package com.expis.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Slf4j
@Configuration
@MapperScan(basePackages = {"com.expis.**.dao"}, sqlSessionFactoryRef = "sqlSessionFactory", sqlSessionTemplateRef = "sqlSessionTemplate")
public class MybatisConfig {

//    @Bean(name="sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
////        sqlSessionFactoryBean.setTypeAliasesPackage("com.expis.user.dto"); // dto 패키지의 이름을 생략할 수 있도록
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
//        return sqlSessionFactoryBean.getObject();
//    }

//    ERRROR : HikariPool-1 - jdbcUrl is required with driverClassName.
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("routingDataSource") DataSource routingDataSource) throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(routingDataSource);
//        // sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
//        return sqlSessionFactoryBean.getObject();
//    }

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.expis.community.dto,com.expis.manage.dto,com.expis.ietm.dto"); // dto 패키지의 이름을 생략할 수 있도록
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));

        // MyBatis 콘솔쿼리
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class); // SQL 로그를 출력
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }
}
