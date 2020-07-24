package com.ajobs.yuns.controller.hall

import com.ajobs.yuns.controller.UserController
import com.ajobs.yuns.mapperImp.hall.HallArticleMapperImp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/pubart")
class HallPubArtController {

    @Autowired
    private lateinit var hallArticleMapperImp: HallArticleMapperImp

    @Autowired
    @Qualifier("dbTransactionManagerPub")
    private lateinit var dataSourceTransactionManager: DataSourceTransactionManager

    @Autowired
    private lateinit var userController: UserController

    @PostMapping("/part")
    @ResponseBody
    fun publishArt(@RequestParam("al") articleLinkName: String, @RequestParam("at") articleTitleName: String, @RequestParam("id") userId: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(userId, httpServletRequest) ?: return ""
        if (hallArticleMapperImp.checkTableWhetherExisted() == null) {
            synchronized(this) {
                if (hallArticleMapperImp.checkTableWhetherExisted() == null)
                    hallArticleMapperImp.createPublishArticleTable()
            }
        }

        return try {
            if (hallArticleMapperImp.selectSingleArt(articleLinkName, userId) == null)
                hallArticleMapperImp.insertArt(articleLinkName, "/yuns/user/${userId}/article/${articleLinkName}", articleTitleName, userId)
            "1"
        } catch (e: java.lang.Exception) {
            ""
        }

    }

    @PostMapping("/delart")
    @ResponseBody
    fun unPublishArt(@RequestParam("al") articleLinkName: String, @RequestParam("id") userId: String, httpServletRequest: HttpServletRequest): String? {
        userController.checkWhetherUserCommanding(userId, httpServletRequest) ?: return ""
        if (hallArticleMapperImp.checkTableWhetherExisted() == null)
            return ""
        return hallArticleMapperImp.delSingleArt(articleLinkName, userId)?.toString()

    }


    @PostMapping("/sartis", produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun pubArtInfo(offset: String, numberOfPage: String, @RequestParam(value = "id", required = false) id: String?,
                   @RequestParam("kw", required = false) kw: String?) = Callable {
        if (hallArticleMapperImp.checkTableWhetherExisted() == null)
            return@Callable null
        if (id == null) {
            if (kw == null)
                hallArticleMapperImp.selectPubArticleInfo(offset.toInt(), numberOfPage.toInt())
            else hallArticleMapperImp.selectPubArticleLikeInfo(kw, offset.toInt(), numberOfPage.toInt(), null)
        } else {
            if (kw == null)
                hallArticleMapperImp.selectIndicatorPubArticleInfo(offset.toInt(), numberOfPage.toInt(), id)
            else hallArticleMapperImp.selectPubArticleLikeInfo(kw, offset.toInt(), numberOfPage.toInt(), id)
        }
    }


    @PostMapping("/iartrc")
    @ResponseBody
    fun insertArtReadCount(@RequestParam("al") articleLinkName: String, @RequestParam("id") userId: String, httpServletRequest: HttpServletRequest) = Callable {
        //        userController.checkWhetherUserCommanding(userId, httpServletRequest) ?: return@Callable ""
        try {
            hallArticleMapperImp.insertArtReadCount(articleLinkName, userId)
            "1"
        } catch (e: Exception) {
            ""
        }
    }

    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }

}