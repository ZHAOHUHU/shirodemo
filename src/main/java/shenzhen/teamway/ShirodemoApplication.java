package shenzhen.teamway;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@MapperScan("shenzhen.teamway.dao")
@SpringBootApplication
@EnableTransactionManagement
public class ShirodemoApplication {

    /*
    创建druid对象
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource getDatasource() {
        return new DruidDataSource();
    }

    //注入mapper的连接对象
    @Bean
    public SqlSessionFactory getobject() throws Exception {
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        //添加数据源
        bean.setDataSource(getDatasource());
        //添加扫描对象的位置
        bean.setMapperLocations(resolver.getResources("classpath*:/mapper/*Mapper.xml"));
        return bean.getObject();
    }

    public static void main(String[] args) {
        SpringApplication.run(ShirodemoApplication.class, args);
    }

    //数据库的事务管理
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(getDatasource());
    }
}
