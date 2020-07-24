package com.ajobs.yuns.config

import com.ajobs.yuns.mapper.hall.HallArticleMapper
import com.ajobs.yuns.mapper.hall.HallResMapper
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
//@MapperScan(basePackages = ["com.ajobs.yuns.mapper.hall"], sqlSessionFactoryRef = "dbSqlSessionFactoryPub")
open class PubConfig {

    @ConfigurationProperties(prefix = "custom.pub.comment")
    @Bean("dataSourcePub")
    open fun datasource(): DataSource {
        return PooledDataSource().apply {
            poolMaximumActiveConnections=400
            poolMaximumIdleConnections=200
        }
    }

    /**
     * 注意：为事务管理器指定的 DataSource 必须和用来创建 SqlSessionFactoryBean 的是同一个数据源，否则事务管理器就无法工作了。
     */
    @Bean("dbTransactionManagerPub")
    open fun transactionManager(@Qualifier("dataSourcePub") ds: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(ds)
    }

    @Bean("dbSqlSessionFactoryPub")
    open fun sqlSessionFactory(@Qualifier("dataSourcePub") ds: DataSource): SqlSessionFactory {
        return SqlSessionFactoryBean().apply {
            setDataSource(ds)
        }.`object`!!.apply {
            configuration.addMappers("com.ajobs.yuns.mapper.hall")
        }
    }

    @Bean("dbSqlSessionTemplatePub")
    open fun sqlSessionTemplate(@Qualifier("dbSqlSessionFactoryPub") sqlSF: SqlSessionFactory): SqlSessionTemplate = SqlSessionTemplate(sqlSF)

    @Bean
    open fun hallArticleMapper(@Qualifier("dbSqlSessionTemplatePub") sqlSessionTemplate: SqlSessionTemplate) = sqlSessionTemplate.getMapper(HallArticleMapper::class.java)

    @Bean
    open fun hallResMapper(@Qualifier("dbSqlSessionTemplatePub") sqlSessionTemplate: SqlSessionTemplate) = sqlSessionTemplate.getMapper(HallResMapper::class.java)


}