package com.ajobs.yuns.config

import com.ajobs.yuns.mapper.artcom.ArticleCommentMapper
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
//@MapperScan(basePackageClasses = [com.ajobs.yuns.mapper.artcom.ArticleCommentMapper::class], sqlSessionFactoryRef = "dbSqlSessionFactoryArt")
open class ArtDBConfig {
    /**
     * 评论区
     */
    @ConfigurationProperties(prefix = "custom.article.comment")
    @Bean("dataSourceArt")
    open fun dataSourceArt(): DataSource {
        return PooledDataSource().apply {
            poolMaximumActiveConnections=400
            poolMaximumIdleConnections=200
        }
    }
    /**
     * 注意：为事务管理器指定的 DataSource 必须和用来创建 SqlSessionFactoryBean 的是同一个数据源，否则事务管理器就无法工作了。
     */
    @Bean("dbTransactionManagerArt")
    open fun transactionManager(@Qualifier("dataSourceArt") ds: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(ds)
    }


    @Bean("dbSqlSessionFactoryArt")
    open fun sqlSessionFactoryArt(@Qualifier("dataSourceArt") ds: DataSource): SqlSessionFactory {
        return SqlSessionFactoryBean().apply {
            setDataSource(ds)
        }.`object`!!.apply {
            configuration.addMapper(ArticleCommentMapper::class.java)
        }
    }

    @Bean("dbSqlSessionTemplateArt")
    open fun sqlSessionTemplateArt(@Qualifier("dbSqlSessionFactoryArt") sqlSF: SqlSessionFactory): SqlSessionTemplate = SqlSessionTemplate(sqlSF)

    @Bean
    open fun articleCommentMapper(@Qualifier("dbSqlSessionTemplateArt") sqlSessionTemplate: SqlSessionTemplate) = sqlSessionTemplate.getMapper(ArticleCommentMapper::class.java)


}