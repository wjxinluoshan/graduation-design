package com.ajobs.yuns.article.service

import com.ajobs.yuns.article.fileintf.ArticleFileCmd
import com.ajobs.yuns.component.Npl
import com.ajobs.yuns.component.UserDataComp
import com.ajobs.yuns.mapperImp.ArticleMapperImp
import com.ajobs.yuns.mapperImp.UserMapperImp
import com.ajobs.yuns.tool.FileHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Service
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import kotlin.collections.ArrayList


@Service
class ArticleFileCmdImp : ArticleFileCmd {

    @Autowired
    private lateinit var articleMapperImp: ArticleMapperImp

    @Autowired
    @Qualifier("dbTransactionManager")
    private lateinit var dbTransactionManager: DataSourceTransactionManager

    @Autowired
    private lateinit var npl: Npl

    @Autowired
    private lateinit var executor: ExecutorService

    @Autowired
    private lateinit var userMapperImp: UserMapperImp


    override fun articleMainContent(userId: String?, articleLinkName: String?): String {
        var stringBuilder = StringBuilder()
        BufferedReader(InputStreamReader(FileInputStream(File("${FileHelper.getClasspath()}/${FileHelper.getUserPath()}", "${userId}/article/${articleLinkName}")), Charsets.UTF_8)).use {
            it.lines().forEach {
                stringBuilder.append(it).append("\r\n")
            }
        }
        var content = stringBuilder.toString()
        return if (content.contains("<div class=\"show_article-content_div\">"))
            "---richEditor---${stringBuilder.toString().split("<div class=\"show_article-content_div\">")[1].split("</div><div class=\"show_comment_area_div\" id=\"show_comment_area_div\">")[0]}"
        else
           "---mdEditor---${stringBuilder.toString().split("<textarea id=\"append-test\" style=\"display:none;\">")[1].split("</textarea></div><div class=\"show_comment_area_div\" id=\"show_comment_area_div\">")[0]}"

    }

    override fun editArticleContent(userId: String, title: String, content: String, articleLinkName: String, et: String) {
        var futures = ArrayList<Future<Int>>(3)
            writeContentToFile(userId, title, content, articleLinkName, et,true)?.let {
                futures.add(executor.submit({
                    npl.deleteOrUpdateUserDocument(mutableListOf("/yuns/user/${userId}/article/${articleLinkName}"), userId, npl.ARTICLE, true, title, "/yuns/user/${userId}/article/${articleLinkName}")
                }, 1))
                futures.add(executor.submit({
                    npl.deleteOrUpdateHallDocument(mutableListOf("/yuns/user/${userId}/article/${articleLinkName}"), npl.ARTICLE, true, title, "/yuns/user/${userId}/article/${articleLinkName}")
                }, 1))
                for (future in futures) {
                    future.get()
                }
            }
    }

    override fun createArticle(userId: String, title: String, content: String, et: String) { //生成随机的8位数
        val random = Random()
        var randomNumber: Int
        while (true) {
            randomNumber = random.nextInt(10000000) + 10000000
            var artLink = "/yuns/user/${userId}/article/${randomNumber}.html"
            var articleLink = articleMapperImp.selectSingleRecordArticleLink(userId.toInt(), artLink)
            if (articleLink == null) {
//                articleMapperImp.insertArticleLinkAndName(userId.toInt(), artLink, title)
//                writeContentToFile(userId, title, content, "${randomNumber}.html")
                val transactionTemplate = TransactionTemplate(dbTransactionManager)
                transactionTemplate.execute<Any?> { txStatus: TransactionStatus? ->
                    try {
                        articleMapperImp.insertArticleLinkAndName(userId.toInt(), artLink, title)
                        executor.submit {
                            writeContentToFile(userId, title, content, "${randomNumber}.html", et,false)
                        }.get()
                        executor.submit {
                            npl.createUserDocument(mutableListOf(title), mutableListOf(artLink), userId, npl.ARTICLE)
                        }.get()
                    } catch (e: Exception) {
                        txStatus?.setRollbackOnly()
                        throw e
                    }
                    null
                }
                break
            }
        }
    }

    private fun writeContentToFile(userId: String, title: String, content: String, filename: String, et: String?,update:Boolean):Boolean? {
        File(FileHelper.getClasspath(), FileHelper.getUserPath()).let {
            /**
             * 读样板
             */
            var stringBuilder = StringBuilder()
            var templateFileName: String = if (et == "0")
                "article_template.html"
            else
                "article_template_md.html"
            BufferedReader(InputStreamReader(FileInputStream(File(it.path, templateFileName)), Charsets.UTF_8)).use {
                it.forEachLine {
                    stringBuilder.append(it)
                }
            }
            /**
             * 生成文章
             */
            var articleContent = stringBuilder.toString().replace("---yuns_title---", title)
            articleContent = articleContent.replace("---yuns_article_title---", title)
            articleContent = articleContent.replace("---yuns_content---", content)
            File(it.path, "${userId}/article").let {
                if (!it.exists())
                    it.mkdirs()
                File(it.path, filename).let {
                    //读出文章更新前的内容
                    var preFileContent = BufferedReader(InputStreamReader(FileInputStream(it))).readLines().run {
                       var stringBuilder=StringBuilder()
                        for (s in this) {
                            stringBuilder.append(s)
                        }
                        stringBuilder.toString()
                    }
                    //在这里进行文章的编写
                    //1.读出原先文章的大小  kb,为了更新
                    var preSize = it.length() / 1024

                    val transactionTemplate = TransactionTemplate(dbTransactionManager)
                return    transactionTemplate.execute<Boolean?> { txStatus: TransactionStatus? ->
                        try {

                            FileOutputStream(it).use {
                                it.write(articleContent.toByteArray())
                            }
                            //2.重写之后的文件大小
                            var newSize = it.length() / 1024
                            //检查文件的的容量
                            var storage: Long
                            var maxStorage: Long
                            userMapperImp.userInfo(userId.toInt()).let {
                                storage = it.storage.toLong()
                                maxStorage = it.maxStorage.toLong()
                            }

                            var newStorage = storage + (newSize - preSize)
                            if (newStorage > maxStorage) {
                                throw java.lang.Exception("内存不足")
                            }
                            userMapperImp.updateUser(mutableListOf(newStorage), mutableListOf("storage")
                                    , mutableListOf("id"), mutableListOf(userId.toInt()))

                        } catch (e: Exception) {
                            txStatus?.setRollbackOnly()
                            //更新失败，将恢复更新前的文章内容
                            if(update){
                                FileOutputStream(it).use {
                                    it.write(preFileContent.toByteArray())
                                }
                                return@execute false
                            }
                                it.delete()
                            //throw e
                        }
                        null
                    }
                }
            }
        }
    }
}
