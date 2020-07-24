package com.ajobs.yuns.controller.main

import com.ajobs.yuns.mapperImp.ResourceNameMapperImp
import com.ajobs.yuns.pojo.ResourceName
import com.ajobs.yuns.component.UserDataComp
import com.ajobs.yuns.controller.UserController
import com.ajobs.yuns.mapperImp.UserMapperImp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/fcc")
@Scope("prototype")
class FileCmdController {

    private val FAILURE = "1";
    private val SUCCESS = "-1"

    private val USER_DIR_TYPE_ONE = "doc"
    private val USER_DIR_TYPE_TWO = "ores"

    @Autowired
    private lateinit var userDataComp: UserDataComp

    @Autowired
    private lateinit var resourceNameMapper: ResourceNameMapperImp

    @Autowired
    private lateinit var userMapperImp: UserMapperImp

    @Autowired
    @Qualifier("dbTransactionManager")
    private lateinit var dataSourceTransactionManager: DataSourceTransactionManager

    @Autowired
    private lateinit var userController: UserController

    /**
     * 上传文件
     */
    @PostMapping("/uf")
    @ResponseBody
    fun uploadFile(@RequestParam(value = "files") files: Array<MultipartFile>,
                   id: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable null
        try {
            userDataComp.uploadFiles(files, id, USER_DIR_TYPE_ONE)
            SUCCESS
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            FAILURE
        }
    }

    /**
     * 得到file name
     */
    @PostMapping("/fnm")
    @ResponseBody
    fun fileNames(id: String, fileType: String, @RequestParam(value = "kw", required = false) kw: String?,
                  @RequestParam(value = "offset", required = false) offset: String?,
                  @RequestParam(value = "numberOfPage", required = false) numberOfPage: String?) = Callable {
        ResourceName().apply {
            rnames = if (fileType == USER_DIR_TYPE_ONE)
                resourceNameMapper.selectResourceName(id.toInt(), "doc_name", kw, offset?.run {
                    toInt()
                }, numberOfPage?.run {
                    toInt()
                })
            else
                resourceNameMapper.selectResourceName(id.toInt(), "resource_name", kw, offset?.run {
                    toInt()
                }, numberOfPage?.run {
                    toInt()
                })

        }
    }


    /**
     * 上传的文件中可以包含照片，符合逻辑
     */
    @PostMapping("/ud")
    @ResponseBody
    fun uploadDir(@RequestParam(value = "files") files: Array<MultipartFile>,
                  id: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable null
        try {
            userDataComp.uploadFiles(files, id, USER_DIR_TYPE_TWO)
            SUCCESS
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            FAILURE
        }
    }


    @PostMapping("/fdel")
    @ResponseBody
    fun delF(filesName: Array<String>, id: String, fileType: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable null
        try {
            userDataComp.delFiles(filesName, id, fileType)
        } catch (e: Exception) {
            e.printStackTrace()
            return@Callable FAILURE
        }
        SUCCESS
    }

    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }


//    @PostMapping(value = ["/df"], produces = ["text/html; charset=UTF-8"])
//    fun downloadFile() {
//        /**
//         *  try {
//        //           文件名的中文解码
//        FileInputStream(File(URLDecoder.decode(filePath, "utf-8"))).use {
//        response.contentType = "application/x-download"
//        var fileName: String
//        filePath.split("/").run {
//        fileName = this[lastIndex]
//        }
//        response.addHeader("Content-Disposition", "attachment;filename=$fileName")
//        IOUtils.copy(it, response.outputStream)
//        }
//        } catch (e: Exception) {
//        e.printStackTrace()
//        }
//         */
//    }

}