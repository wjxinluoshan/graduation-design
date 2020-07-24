package com.ajobs.yuns.controller.hall

import com.ajobs.yuns.controller.UserController
import com.ajobs.yuns.mapperImp.hall.HallResMapperImp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/pubres")
class HallPubResController {


    @Autowired
    private lateinit var hallResMapperImp: HallResMapperImp

    @Autowired
    private lateinit var userController: UserController

    @PostMapping("/ppic")
    @ResponseBody
    fun insertPic(picName: String, id: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return ""
        if (hallResMapperImp.checkTableWhetherExisted("tb_publish_pic") == null) {
            synchronized(this) {
                if (hallResMapperImp.checkTableWhetherExisted("tb_publish_pic") == null)
                    hallResMapperImp.createPublishPicTable()
            }
        }
        return try {
            if (hallResMapperImp.selectSinglePic(picName, id) == null)
                hallResMapperImp.insertPic(picName.run {
                    var arr = split("?name=")
                    arr[arr.size - 1]
                }, picName + "?date=${System.currentTimeMillis()}", id)
            "1"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

    }

    @PostMapping("/pdoc")
    @ResponseBody
    fun insertDoc(docName: String, id: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id,httpServletRequest)?:return ""
        if (hallResMapperImp.checkTableWhetherExisted("tb_publish_doc") == null) {
            synchronized(this) {
                if (hallResMapperImp.checkTableWhetherExisted("tb_publish_doc") == null)
                    hallResMapperImp.createPublishDocTable()
            }
        }
        return try {
            if (hallResMapperImp.selectSingleDoc(docName, id) == null)
                hallResMapperImp.insertDoc(docName.run {
                    var arr = split("?name=")
                    arr[arr.size - 1]
                }, docName + "?date=${System.currentTimeMillis()}", id)
            "1"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }

    }

    @PostMapping("/pores")
    @ResponseBody
    fun insertOres(oresName: String, id: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id,httpServletRequest)?:return ""
        if (hallResMapperImp.checkTableWhetherExisted("tb_publish_ores") == null) {
            synchronized(this) {
                if (hallResMapperImp.checkTableWhetherExisted("tb_publish_ores") == null)
                    hallResMapperImp.createPublishOresTable()
            }
        }
        return try {
            if (hallResMapperImp.selectSingleOres(oresName, id) == null)
                hallResMapperImp.insertOres(oresName.run {
                    var arr = split("?name=")
                    arr[arr.size - 1]
                }, oresName + "?date=${System.currentTimeMillis()}", id)
            "1"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }

    }

    @PostMapping("/delp")
    @ResponseBody
    fun delPic(picName: String, id: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id, httpServletRequest)?:return ""
        try {
            hallResMapperImp.delSinglePic(picName + "%", id)
        } catch (e: Exception) {
            return ""
        }
        return "1"
    }

    @PostMapping("/deld")
    @ResponseBody
    fun delDoc(docName: String, id: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id, httpServletRequest)?:return ""
        try {
            hallResMapperImp.delSingleDoc(docName + "%", id)
        } catch (e: java.lang.Exception) {
            return ""
        }
        return "1"
    }

    @PostMapping("/delo")
    @ResponseBody
    fun delOres(oresName: String, id: String, httpServletRequest: HttpServletRequest): String {
        userController.checkWhetherUserCommanding(id, httpServletRequest)?:return ""
        try {
            hallResMapperImp.delSingleOres(oresName + "%", id)
        } catch (e: java.lang.Exception) {
            return ""
        }
        return "1"
    }

    @PostMapping("/qpics", produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun selectPics(offset: String, numberOfPage: String, @RequestParam(value = "id", required = false) id: String?) = Callable {
        if (hallResMapperImp.checkTableWhetherExisted("tb_publish_pic") == null)
            return@Callable null
        if (id == null)
            hallResMapperImp.selectPics(offset.toInt(), numberOfPage.toInt())
        else {
            hallResMapperImp.selectIndicatorPics(offset.toInt(), numberOfPage.toInt(), id)
        }
    }

    @PostMapping("/qdocs", produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun selectDocs(offset: String, numberOfPage: String, @RequestParam(value = "id", required = false) id: String?) = Callable {
        if (hallResMapperImp.checkTableWhetherExisted("tb_publish_doc") == null)
            return@Callable null
        if (id == null)
            hallResMapperImp.selectDocs(offset.toInt(), numberOfPage.toInt())
        else
            hallResMapperImp.selectIndicatorDocs(offset.toInt(), numberOfPage.toInt(), id)
    }

    @PostMapping("/qoreses", produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun selectOreses(offset: String, numberOfPage: String, @RequestParam(value = "id", required = false) id: String?) = Callable {
        if (hallResMapperImp.checkTableWhetherExisted("tb_publish_ores") == null)
            return@Callable null
        if (id == null)
            hallResMapperImp.selectOreses(offset.toInt(), numberOfPage.toInt())
        else
            hallResMapperImp.selectIndicatorOreses(offset.toInt(), numberOfPage.toInt(), id)
    }

    @PostMapping("/idpc")
    @ResponseBody
    fun insertDownloadPicCount(@RequestParam("rn") picName: String, id: String, httpServletRequest: HttpServletRequest) = Callable {
//        userController.checkWhetherUserCommanding(id, httpServletRequest)?:return@Callable ""
        try {
            hallResMapperImp.insertDownloadPicCount(picName, id)
        } catch (e: Exception) {
            e.printStackTrace()
            return@Callable ""
        }
        "1"
    }

    @PostMapping("/iddc")
    @ResponseBody
    fun insertDownloadDocCount(@RequestParam("rn") docName: String, id: String, httpServletRequest: HttpServletRequest) = Callable {
//        userController.checkWhetherUserCommanding(id, httpServletRequest)?:return@Callable ""
        try {
            hallResMapperImp.insertDownloadDocCount(docName, id)
        } catch (e: Exception) {
            return@Callable ""
        }
        "1"
    }

    @PostMapping("/idorsc")
    @ResponseBody
    fun insertDownloadOresCount(@RequestParam("rn") oresName: String, id: String, httpServletRequest: HttpServletRequest) = Callable {
//        userController.checkWhetherUserCommanding(id, httpServletRequest)?:return@Callable ""
        try {
            hallResMapperImp.insertDownloadOresCount(oresName, id)
        } catch (e: Exception) {
            e.printStackTrace()
            return@Callable ""
        }
        "1"
    }

    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        e.printStackTrace()
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }

}