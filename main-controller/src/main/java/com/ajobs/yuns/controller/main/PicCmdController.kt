package com.ajobs.yuns.controller.main

import com.ajobs.yuns.mapperImp.ResourceNameMapperImp
import com.ajobs.yuns.pojo.ResourceName
import com.ajobs.yuns.component.UserDataComp
import com.ajobs.yuns.controller.UserController
import com.ajobs.yuns.mapperImp.UserMapperImp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.Callable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/pcc")
class PicCmdController {


    private val FAILURE = "0";
    private val SUCCESS = "1"

    @Autowired
    private lateinit var userDataComp: UserDataComp

    @Autowired
    private lateinit var resourceNameMapper: ResourceNameMapperImp

    @Autowired
    private lateinit var userMapperImp: UserMapperImp

    @Autowired
    private lateinit var userController: UserController

//    @Autowired
//    @Qualifier("dbTransactionManager")
//    private lateinit var dataSourceTransactionManager: DataSourceTransactionManager

    @PostMapping("/up")
    @ResponseBody
    fun uploadPic(@RequestParam(value = "files") files: Array<MultipartFile>,
                  id: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable null
        try {
            userDataComp.uploadFiles(files, id, "pic")
            SUCCESS
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            FAILURE
        }
    }

    @PostMapping("/dp")
    fun downloadPic() {

    }

    @PostMapping("/pnm")
    @ResponseBody
    fun picName(id: String, @RequestParam(value = "kw", required = false) kw: String?,
                @RequestParam(value = "offset", required = false) offset: String?,
                @RequestParam(value = "numberOfPage", required = false) numberOfPage: String?) = Callable {
        ResourceName().apply {
            rnames = resourceNameMapper.selectResourceName(id.toInt(), "pic_name", kw, offset?.run {
                toInt()
            }, numberOfPage?.run {
                toInt()
            })
        }
    }

    @PostMapping("/pdel")
    @ResponseBody
    fun delPic(filesName: Array<String>, id: String, httpServletRequest: HttpServletRequest) = Callable {
        userController.checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable null
        try {
            userDataComp.delFiles(filesName, id, "pic")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return@Callable FAILURE
        }
        SUCCESS
    }

    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }


}