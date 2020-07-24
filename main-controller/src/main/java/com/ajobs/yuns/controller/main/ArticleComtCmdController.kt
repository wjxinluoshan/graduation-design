package com.ajobs.yuns.controller.main

import com.ajobs.yuns.controller.UserController
import com.ajobs.yuns.mapperImp.ArticleCommentMapperImp
import com.ajobs.yuns.mapperImp.ArticleMapperImp
import com.ajobs.yuns.pojo.ArticleComment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Controller
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList

@Controller
@RequestMapping("/artcomc")
class ArticleComtCmdController {

    @Autowired
    private lateinit var articleCommentMapperImp: ArticleCommentMapperImp

    @Autowired
    private lateinit var articleMapperImp: ArticleMapperImp

    @Autowired
    @Qualifier("dbTransactionManagerArt")
    private lateinit var dbTransactionManagerArt: DataSourceTransactionManager

    @Autowired
    private lateinit var userController: UserController

    /**
     * id   ;   articleLinkName:*****.html
     * cId  ;   email ;   comment
     */
    @PostMapping("/rec")
    @ResponseBody
    fun receiveArtComment(@RequestParam articleCommentInfo: Map<String, String>): String? {
        //避免攻击，检测表文章是否存在
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(articleCommentInfo["id"]?.toInt(), "/yuns/user/${articleCommentInfo["id"]?.toInt()}/article/${articleCommentInfo["articleLinkName"]}") != null) {
                val transactionTemplate = TransactionTemplate(dbTransactionManagerArt)
                return transactionTemplate.execute { txStatus: TransactionStatus? ->
                    try {
                        //检测comment 表是否存在
                        if (articleCommentMapperImp.checkTableWhetherExisted(
                                        "_${articleCommentInfo["id"]?.toInt()}_${articleCommentInfo["articleLinkName"]?.replace(".html", "")}"
                                ) == null)
                            synchronized(this@ArticleComtCmdController) {
                                if (articleCommentMapperImp.checkTableWhetherExisted(
                                                "_${articleCommentInfo["id"]?.toInt()}_${articleCommentInfo["articleLinkName"]?.replace(".html", "")}"
                                        ) == null)
                                    articleCommentMapperImp.createArticleCommentTable(articleCommentInfo["id"]?.toInt(), articleCommentInfo["articleLinkName"])
                            }
                        var lastComId = articleCommentMapperImp.articleCommentLastId(articleCommentInfo["id"]?.toInt(), articleCommentInfo["articleLinkName"])

                        lastComId = if (lastComId == null) {
                            "1"
                        } else
                            (lastComId.toLong() + 1).toString()

                        /**
                         * 检测是否包含rId
                         */
                        articleCommentInfo["cId"]?.let {

                            var map = articleCommentMapperImp.selectSingleRecordArticleComment(
                                    articleCommentInfo["id"],
                                    articleCommentInfo["articleLinkName"],
                                    it
                            )
                            if (map==null || map["comId"] == null || map["comId"] == "")
                                return@let
                            /**
                             * 插入回复标记 responseId  commentId
                             */
                            articleCommentMapperImp.inertArticleResponseComment(
                                    articleCommentInfo["id"]?.toInt(),
                                    articleCommentInfo["articleLinkName"],
                                    lastComId,
                                    it
                            )
                        }
                        /**
                         * 插入数据
                         */
                        articleCommentMapperImp.inertArticleRecordComment(articleCommentInfo["id"]?.toInt(),
                                articleCommentInfo["articleLinkName"],
                                lastComId,
                                articleCommentInfo["email"], articleCommentInfo["comment"], Timestamp(Date().time))
                    } catch (e: java.lang.Exception) {
                        txStatus?.setRollbackOnly()
                        e.printStackTrace()
                        ""
                    }
                    "1"
                }
            } else {
                return ""
            }
        } catch (e: Exception) {
            // e.printStackTrace()
            return ""
        }
    }


    @PostMapping("/delc")
    @ResponseBody
    fun delRecordArticleComment(id: String, articleLinkName: String, @RequestParam("comIds") delRecordComments: Array<String>
                                , httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return ""
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                /**
                 * 递归删除
                 */
                val transactionTemplate = TransactionTemplate(dbTransactionManagerArt)

                fun delComments(delRecordComments: List<String>) {
                    var willDelRids = mutableListOf<String>()
                    delRecordComments.forEach {
                        transactionTemplate.execute { txStatus: TransactionStatus? ->
                            try {
                                //根据cId查询回复表中的rId
                                articleCommentMapperImp.selectResponseArticleRIDs(id.toInt(), articleLinkName, it).forEach {
                                    willDelRids.add(it)
                                    articleCommentMapperImp.delRecordArticleComment(id.toInt(), articleLinkName, it)
                                    articleCommentMapperImp.delVerifyArticleComment(id.toInt(), articleLinkName, it)
                                }
                                articleCommentMapperImp.delRecordArticleComment(id.toInt(), articleLinkName, it)
//                                articleCommentMapperImp.delResponseArticleComment(id.toInt(), articleLinkName, null, it)
                                articleCommentMapperImp.delResponseArticleComment(id.toInt(), articleLinkName, it)
                                articleCommentMapperImp.delVerifyArticleComment(id.toInt(), articleLinkName, it)
                            } catch (e: java.lang.Exception) {
                                txStatus?.setRollbackOnly()
                                e.printStackTrace()
                            }
                            null
                        }

                    }
                    if (willDelRids.isNotEmpty())
                        delComments(willDelRids)
                }
                delComments(delRecordComments.toList())
            } else
                return ""
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return ""
        }
        return "1"
    }

    @PostMapping("/recvf")
    @ResponseBody
    fun receiveVerifyArticleComment(id: String, articleLinkName: String, @RequestParam("comIds") verifyRecordComments: Array<String>
                                    , httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return ""
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                verifyRecordComments.forEach {
                    if (articleCommentMapperImp.selectSingleVerifyArticleComment(id, articleLinkName, it.toInt()) == null) {
                        articleCommentMapperImp.inertArticleVerifyComment(id.toInt(), articleLinkName, it.toInt())
                    }
                }
            } else
                return ""

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

        return "1"
    }

    @PostMapping("/qrec")
    @ResponseBody
    fun queryRecordArticleComment(id: String, articleLinkName: String, offset: String, numberOfPage: String): ArticleComment? {
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                if (articleCommentMapperImp.checkTableWhetherExisted("_${id.toInt()}_${articleLinkName.replace(".html", "")}") != null) {
                    var map = articleCommentMapperImp.selectRecordArticleComments(id, articleLinkName, offset.toInt(), numberOfPage.toInt())
                    return ArticleComment().apply {
                        map.let {
                            this.comIds = it["comIds"]
                            this.emails = it["emails"]
                            this.comments = it["comments"]
                            this.dateTimes = it["dataTimes"]
                        }
                    }
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @PostMapping("/qrec2")
    @ResponseBody
    fun queryRecordArticleComment(id: String, articleLinkName: String, comIds: Array<String>): ArticleComment? {
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                val emailList = ArrayList<String>()
                val commentList = ArrayList<String>()
                comIds.forEach {
                    articleCommentMapperImp.selectSingleRecordArticleComment(id, articleLinkName, it)?.let {
                        emailList.add(it["email"].toString())
                        commentList.add(it["commentContent"].toString())
                    }
                }
                return ArticleComment().apply {
                    emails = emailList
                    comments = commentList
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    @PostMapping("/qver")
    @ResponseBody
    fun queryVerifyArticleComment(id: String, articleLinkName: String, offset: String, numberOfPage: String): ArticleComment? {
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                if (articleCommentMapperImp.checkTableWhetherExisted("_${id.toInt()}_${articleLinkName.replace(".html", "")}") != null) {
                    val comIdList = ArrayList<String>()
                    val emailList = ArrayList<String>()
                    val commentList = ArrayList<String>()
                    val dateTimeList = ArrayList<String>()
                    val rIdList = ArrayList<String>()
                    val cIdList = ArrayList<String>()
                    //降序查询验证表中的数据
                    articleCommentMapperImp.selectVerifyArticleComments(id, articleLinkName, offset.toInt(), numberOfPage.toInt()).forEach {
                        //根据comId查询记录表中的数据
                        var map = articleCommentMapperImp.selectSingleRecordArticleComment(id, articleLinkName, it.toString())
                        comIdList.add(map["comId"].toString())
                        emailList.add(map["email"].toString())
                        commentList.add(map["commentContent"].toString())
                        val dataTime: String = map["commentDataTime"].toString()
                        val index = dataTime.indexOf(".")
                        dateTimeList.add(dataTime.substring(0, index))
                        var rId = it.toString()
                        //根据rId查询回答表中的cId
                        articleCommentMapperImp.selectResponseArticleCIDs(id.toInt(), articleLinkName, rId)?.let {
                            rIdList.add(rId)
                            cIdList.add(it)
                        }
                    }
                    return ArticleComment().apply {
                        comIds = comIdList
                        emails = emailList
                        comments = commentList
                        dateTimes = dateTimeList
                        rIds = rIdList
                        cIds = cIdList
                    }
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @PostMapping("/vernum")
    @ResponseBody
    fun verifyArticleCommentsLikeNum(id: String, articleLinkName: String): String {
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                if (articleCommentMapperImp.checkTableWhetherExisted("_${id.toInt()}_${articleLinkName.replace(".html", "")}") != null) {
                    return articleCommentMapperImp.selectVerifyArticleLikeCommentsNumber(id, articleLinkName).toString()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    @PostMapping("/cver")
    @ResponseBody
    fun checkCommentsVerified(id: String, articleLinkName: String, comIds: Array<String>): List<String>? {
        try {
            if (articleMapperImp.selectSingleRecordArticleLink(id.toInt(), "/yuns/user/${id.toInt()}/article/${articleLinkName}") != null) {
                if (articleCommentMapperImp.checkTableWhetherExisted("_${id.toInt()}_${articleLinkName.replace(".html", "")}") != null) {
                    var list = ArrayList<String>()
                    comIds.forEach {
                        if (articleCommentMapperImp.selectSingleVerifyArticleComment(id, articleLinkName, it.toInt()) != null) {
                            list.add("1")
                        } else {
                            list.add("0")
                        }
                    }
                    return list
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }

}