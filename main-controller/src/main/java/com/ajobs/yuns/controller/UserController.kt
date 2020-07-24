package com.ajobs.yuns.controller

import com.ajobs.yuns.component.MailSenderComp
import com.ajobs.yuns.mapperImp.ArticleMapperImp
import com.ajobs.yuns.mapperImp.UserMapperImp
import com.ajobs.yuns.pojo.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Controller
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.Callable
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
//@Scope("prototype")
@RequestMapping("/user")
open class UserController {

//    @GetMapping("/test")
//    @ResponseBody
//    fun test(): Callable<String> {
//        var callable = Callable<String> {
//            Thread.sleep(3000)
//            "someView"
//        }
//        println("wjx")
//        return callable
//    }

    @Autowired
    private lateinit var userMapperImp: UserMapperImp

    @Autowired
    private lateinit var articleMapperImp: ArticleMapperImp

    @Autowired
    private lateinit var mailSenderComp: MailSenderComp

    @Autowired
    @Qualifier("dbTransactionManager")
    private lateinit var dbTransactionManager: DataSourceTransactionManager

    /**
     * data:数据信息加密
     */
    @PostMapping(value = ["/login"])
    @ResponseBody
    fun login(username: String, data: String, httpServletRequest: HttpServletRequest): String? {
        if (userMapperImp.checkTableWhetherExisted() == null)
            return null
        var id: Int = userMapperImp.login(username, data) ?: return ""
        httpServletRequest.getSession(false) ?: httpServletRequest.session
        httpServletRequest.getSession(false).apply {
            setAttribute(id.toString(), 1)
            maxInactiveInterval = 60 * 60 * 3
        }
        return id.toString()
    }

    /**
     * data:数据信息加密
     */
//    @GetMapping(value = ["/main"])
//    fun gotoMain(@RequestParam(value = "data", required = false) data: String?, model: Model): String? {
//        model.addAttribute("id", data)
//        return "main"
//    }

    /**
     * data:数据信息加密
     */
    @PostMapping(value = ["/logout"])
    @ResponseBody
    fun logOut(id: String, httpServletRequest: HttpServletRequest): String? {
        httpServletRequest.getSession(false)?.run {
            removeAttribute(id)
            //当前session没有属性就将其清除
            if (!attributeNames.hasMoreElements()) {
                invalidate()
            }
        }
        return null
    }

    @PostMapping("/cwuc")
    @ResponseBody
    fun checkWhetherUserCommanding(@RequestParam(value = "id", required = false) id: String,
                                   httpServletRequest: HttpServletRequest): String? {
        return httpServletRequest.getSession(false)?.getAttribute(id)?.toString()
    }

    /**
     * 用户注册
     */
    @PostMapping(value = ["/registry"])
    @ResponseBody
    fun registry(profileBinaryData: MultipartFile, username: String, @RequestParam("data") password: String,
                 email: String, httpServletRequest: HttpServletRequest) = Callable {
        httpServletRequest.cookies?.run {
            var canRegistry = false
            for (cookie in this) {
                if (cookie.name == "maker") {
                    canRegistry = true
                    break
                }
            }
            if (!canRegistry)
                return@Callable null
        } ?: return@Callable null

        if (userMapperImp.checkTableWhetherExisted() == null)
            synchronized(this) {
                if (userMapperImp.checkTableWhetherExisted() == null) {
                    userMapperImp.createUserTable()
                }
            }
        var checkUser = ""
        userMapperImp.checkUser(username, email)?.let {
            checkUser = "userExisted"
        }
        //用户存在
        if (checkUser != "")
            return@Callable checkUser

        userMapperImp.registry(username, password, email)
        var userId: String? = null
        TransactionTemplate(dbTransactionManager).execute<Any?> { txStatus: TransactionStatus? ->
            try {
                //synchronized(this) {
                //排他锁
                //}
                userId = userMapperImp.login(username, password)?.toString()?.apply {
                    userMapperImp.updateUser(mutableListOf(profileBinaryData), mutableListOf("pictureUrl"), mutableListOf("id"), mutableListOf(this.toInt()))
                    /*
                     *根据userId新建一个用户表
                     */
                    userMapperImp.createUserResourceNameTable(this.toInt())
                    //创建文章表
                    articleMapperImp.createArticleTable(this.toInt())
                }

                httpServletRequest.getSession(false) ?: httpServletRequest.session
                httpServletRequest.getSession(false).apply {
                    setAttribute(userId.toString(), 1)
                    maxInactiveInterval = 60 * 60 * 3
                }
                return@execute userId

            } catch (e: java.lang.Exception) {
                txStatus?.setRollbackOnly()
                userId?.let {
                    userMapperImp.deleteUser(it.toInt())
                }
                //e.printStackTrace()
                return@execute null
            }
            return@execute null
        } as String?

    }

    @PostMapping(value = ["/info"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun info(id: String): User? {
        return userMapperImp.userInfo(id.toInt())
    }

    @PostMapping(value = ["/infos"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun infos(offset: String, numberOfPage: String): User? {
        if (userMapperImp.checkTableWhetherExisted() == null) return null
        return User().apply {
            var map = userMapperImp.userInfos(offset.toInt(), numberOfPage.toInt())
            comIds = map?.get("comIds")
            usernames = map?.get("usernames")
            pictureUrls = map?.get("pictureUrls")
        }
    }

    @PostMapping(value = ["/uns"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun userNames(ids: Array<String>): List<String>? {
        var list = mutableListOf<String>()
        ids.forEach {
            val uname = userMapperImp.getUserName(it.toInt())
            if (uname != null)
                list.add(uname)
        }
        return list
    }

    @PostMapping(value = ["/lquis"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun likeQueryUserInfos(kw: String): Map<String, List<String>>? {
        return userMapperImp.selectLikeUserInfos(kw)
    }

    /*
     *邮箱请求
     */
    @PostMapping("/fpwd")
    @ResponseBody
    fun findPassword(email: String, code: String, httpServletResponse: HttpServletResponse) {
        mailSenderComp.sendVerifyPswCode(email, code)
        //添加cookie
        httpServletResponse.addCookie(Cookie(code, code).apply {
            maxAge = 300
        })
    }

    @PostMapping("/eamilsf")
    @ResponseBody
    fun emailSucOrFail(@RequestParam(value = "vCode", required = false) vCode: String?,
                       @RequestParam(value = "vCookie", required = false) vCookie: String?,
                       httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): String? {

        var code: String? = null
        var cookieCode = ""
        httpServletRequest.cookies?.run {
            for (cookie in this) {
                //删除邮箱操作指定的cookie
                vCookie?.run {
                    if (cookie.name == vCookie) {
                        httpServletResponse.addCookie(Cookie(vCookie, "").apply {
                            maxAge = 1
                        })
                    }
                    return null
                }
                code = mailSenderComp.whoVerifyPswRequest?.get(cookie.value)
                if (code != null) {
                    cookieCode = cookie.value
                    break
                }
            }
        }

        if (vCode != null) {
            if (code == vCode) {
                /*
                 *注册和修改密码
                 */
                httpServletResponse.addCookie(Cookie("maker", "code").apply {
                    maxAge = 300
                })
                mailSenderComp.removeWhoVerifyPswRequestEle(cookieCode)
                return "1"
            }
        } else {
            if (code != null) {
                return "1"
            }
        }
        return null
    }

    @PostMapping("/epsw")
    @ResponseBody
    fun editPsw(@RequestParam(value = "data") password: String, email: String, httpServletRequest: HttpServletRequest): String? {
        httpServletRequest.cookies?.run {
            var canRegistry = false
            for (cookie in this) {
                if (cookie.name == "maker") {
                    canRegistry = true
                    break
                }
            }
            if (!canRegistry)
                return null
        } ?: return null
        return try {
            var result = userMapperImp.updateUser(mutableListOf(password), mutableListOf("password"),
                    mutableListOf("email"), mutableListOf(email))
            if (result != null && result == 1)
                "1"
            else
                null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }

    }

    @PostMapping("/upi")
    @ResponseBody
    fun updateUserInfo(
            id: String,
            @RequestParam(value = "uname", required = false) username: String?,
            @RequestParam(value = "data", required = false) password: String?,
            @RequestParam(value = "pf", required = false) profile: MultipartFile?,
            @RequestParam(value = "email", required = false) email: String?,
            httpServletRequest: HttpServletRequest
    ) = Callable {
        checkWhetherUserCommanding(id, httpServletRequest) ?: return@Callable ""
        var uname = username
        var uemail = email
        if (username != null) {
            if (email == null) {
                if (userMapperImp.checkUser(username, null) == 1) {
                    uname = null
                }
            } else {
                if (userMapperImp.checkUser(username, email) == 1) {
                    uname = null
                    uemail = null
                }
            }
        } else if (email != null) {
            if (username == null) {
                if (userMapperImp.checkUser(null, email) == 1)
                    uemail = null
            } else {
                if (userMapperImp.checkUser(username, email) == 1) {
                    uname = null
                    uemail = null
                }
            }
        }

        /**
         * List<? super String> values, List<String> fieldNames,
        List<String> conditionFieldNames, List<? super String> conditionValues
         */
        /**
         * List<? super String> values, List<String> fieldNames,
        List<String> conditionFieldNames, List<? super String> conditionValues
         */
        var values = mutableListOf<Any>()
        var fieldNames = mutableListOf<String>()
        uname?.let {
            values.add(it)
            fieldNames.add("username")
        }
        password?.let {
            values.add(it)
            fieldNames.add("password")
        }
        profile?.let {
            values.add(it)
            fieldNames.add("pictureUrl")
        }
        uemail?.let {
            values.add(it)
            fieldNames.add("email")
        }
        try {
            if (values.size > 0)
                userMapperImp.updateUser(values, fieldNames, mutableListOf("id"), mutableListOf(id.toInt()))
            if ((username != null || email != null) && (uname == null && uemail == null)) {
                "0"
            } else {
                "1"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    @ExceptionHandler
    private fun error(e: Exception, httpServletResponse: HttpServletResponse) {
        e.printStackTrace()
        httpServletResponse.sendRedirect("/yuns/error/500.html")
    }

}