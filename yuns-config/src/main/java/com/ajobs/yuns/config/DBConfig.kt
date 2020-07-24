package com.ajobs.yuns.config

import com.ajobs.yuns.mapper.main.ArticleMapper
import com.ajobs.yuns.mapper.main.ResourceNameMapper
import com.ajobs.yuns.mapper.main.UserMapper
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource


@Configuration
//@MapperScan(basePackages = ["com.ajobs.yuns.mapper.main"], sqlSessionFactoryRef = "dbSqlSessionFactory")
open class DBConfig {

    /*
     *将DriverManagerDataSource改成连接池
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean("dataSource")
    open fun datasource(): DataSource {
        return PooledDataSource().apply {
            poolMaximumActiveConnections=400
            poolMaximumIdleConnections=200
        }
    }

    /**
     * 注意：为事务管理器指定的 DataSource 必须和用来创建 SqlSessionFactoryBean 的是同一个数据源，否则事务管理器就无法工作了。
     */
    @Bean("dbTransactionManager")
    open fun transactionManager(@Qualifier("dataSource") ds: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(ds)
    }

    @Bean("dbSqlSessionFactory")
    open fun sqlSessionFactory(@Qualifier("dataSource") ds: DataSource): SqlSessionFactory {
        return SqlSessionFactoryBean().apply {
            setDataSource(ds)

        }.`object`!!.apply {
            configuration.addMappers("com.ajobs.yuns.mapper.main")
        }
    }

    @Bean("dbSqlSessionTemplate")
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    open fun sqlSessionTemplate(@Qualifier("dbSqlSessionFactory") sqlSF: SqlSessionFactory): SqlSessionTemplate = SqlSessionTemplate(sqlSF)


    @Bean
    open fun userMapper(@Qualifier("dbSqlSessionTemplate") sqlSessionTemplate: SqlSessionTemplate) = sqlSessionTemplate.getMapper(UserMapper::class.java)

    @Bean
    open fun resourceNameMapper(@Qualifier("dbSqlSessionTemplate") sqlSessionTemplate: SqlSessionTemplate) = sqlSessionTemplate.getMapper(ResourceNameMapper::class.java)

    @Bean
    open fun articleMapper(@Qualifier("dbSqlSessionTemplate") sqlSessionTemplate: SqlSessionTemplate) = sqlSessionTemplate.getMapper(ArticleMapper::class.java)


}