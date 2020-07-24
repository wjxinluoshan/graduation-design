package com.ajobs.yuns.controller.main

import com.ajobs.yuns.article.service.ArticleFileCmdImp
import com.ajobs.yuns.component.UserDataComp
import com.ajobs.yuns.controller.UserController
import com.ajobs.yuns.mapperImp.ArticleMapperImp
import com.ajobs.yuns.mapperImp.hall.HallArticleMapperImp
import com.ajobs.yuns.pojo.ArticleInfo
import com.ajobs.yuns.pojo.ResourceName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Controller
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/artcc")
class ArticleCmdController {


    private val FAILURE = "0";
    private val SUCCESS = "1"

    @Autowired
    private lateinit var articleFileCmdImp: ArticleFileCmdImp

    @Autowired
    private lateinit var articleMapperImp: ArticleMapperImp

    @Autowired
    private lateinit var userDataComp: UserDataComp

    @Autowired
    private lateinit var hallArticleMapperImp: HallArticleMapperImp

    @Autowired
    @Qualifier("dbTransactionManagerPub")
    private lateinit var dbTransactionManagerPub: DataSourceTransactionManager


    @Autowired
    @Qualifier("dbTransactionManager")
    private lateinit var dbTransactionManager: DataSourceTransactionManager

    @Autowired
    private lateinit var userController: UserController

    @PostMapping("/uart")
    @ResponseBody
    fun uploadArt(titleName: String,
                  content: String,
                  id: String, et: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable FAILURE
        try {
            articleFileCmdImp.createArticle(id, titleName, content, et)
        } catch (e: Exception) {
            e.printStackTrace()
            return@Callable FAILURE
        }
        SUCCESS
    }

    @PostMapping("/artname")
    @ResponseBody
    fun artName(id: String, @RequestParam(value = "kw", required = false) kw: String?,
                @RequestParam(value = "offset", required = false) offset: String?,
                @RequestParam(value = "numberOfPage", required = false) numberOfPage: String?) = Callable {
        ResourceName().apply {
            var map = articleMapperImp.selectArtLinkAndName(id.toInt(), kw, offset?.run { toInt() }, numberOfPage?.run { toInt() })
            rnames = map["rnames"];
            rlinks = map["rlinks"]
        }
    }


    @PostMapping("/artdel")
    @ResponseBody
    fun delArt(filesName: Array<String>, id: String, fileType: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable FAILURE
        try {
            userDataComp.delFiles(filesName, id, fileType)
        } catch (e: java.lang.Exception) {
            return@Callable FAILURE
        }
        SUCCESS
    }

    @PostMapping("/artcont")
    @ResponseBody
    fun articleMainContent(id: String, articleLinkName: String) = Callable {
        try {
            return@Callable ArticleInfo().apply {
                title = articleMapperImp.selectSingleArticleNameUseLink(id.toInt(), "/yuns/user/${id}/article/${articleLinkName}")
                content = articleFileCmdImp.articleMainContent(id, articleLinkName)
            }
        } catch (e: Exception) {
        }
        null
    }

    @PostMapping("/arttitle")
    @ResponseBody
    fun articleTitleAName(id: String, articleLinkName: String): String? {
        return articleMapperImp.selectSingleArticleNameUseLink(id.toInt(), "/yuns/user/${id}/article/${articleLinkName}")
    }

    @PostMapping("/edtart")
    @ResponseBody
    fun editArt(titleName: String,
                content: String,
                id: String, articleLinkName: String, et: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable null
        val transactionTemplate = TransactionTemplate(dbTransactionManager)
        transactionTemplate.execute { txStatus: TransactionStatus? ->
            try {
                articleMapperImp.updateArticleLinkAndName(id.toInt(), "/yuns/user/${id}/article/${articleLinkName}", titleName)
                articleFileCmdImp.editArticleContent(id, titleName, content, articleLinkName, et)
                TransactionTemplate(dbTransactionManagerPub).execute<Any?> { txStatus: TransactionStatus? ->
                    try {
                        if (hallArticleMapperImp.checkTableWhetherExisted() != null)
                            hallArticleMapperImp.updateArtTitleName(articleLinkName, id, titleName)
                        return@execute null
                    } catch (e: java.lang.Exception) {
                        txStatus?.setRollbackOnly()
                        e.printStackTrace()
                        throw e
                    }
                }
            } catch (e: java.lang.Exception) {
                txStatus?.setRollbackOnly()
                e.printStackTrace()
                return@execute FAILURE
            }
            return@execute SUCCESS
        }
    }

    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }

}