package com.ajobs.yuns.component

import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Component
@WebFilter(urlPatterns = ["/*"], asyncSupported = true)
class WebFilter : Filter {

    private var illegalRequestMap = ConcurrentHashMap<String, String>()
    private var illegalRequestTimerMap = ConcurrentHashMap<String, Timer>()

    private var illegalRequestCountMap = ConcurrentHashMap<String, Array<Long>>()
    private var illegalRequestCountMapTimer = ConcurrentHashMap<String, Timer>()

    private val checkRMArr = arrayOf("/yuns/artcomc/rec",
            "/yuns/pubart/iartrc", "/yuns/pubres/idpc", "/yuns/pubres/iddc", "/yuns/pubres/idorsc",
            "/yuns/user/login", "/yuns/user/fpwd")

    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        var request = (p0 as HttpServletRequest)
        var requestPath = request.requestURI
        var canCheckIllegalAccess = true
        request.getSession(false)?.apply {
            while (attributeNames.hasMoreElements()) {
                if (getAttribute(attributeNames.nextElement()) == 1) {
                    illegalRequestCountMap.remove(request.remoteAddr)
                    illegalRequestCountMapTimer.remove(illegalRequestCountMapTimer[request.remoteAddr].run {
                        this?.cancel()
                        request.remoteAddr
                    })
                    canCheckIllegalAccess = false
                    break
                }
            }
        }
        try {
            if (canCheckIllegalAccess && checkRMArr.run {
                        for (s in this) {
                            if (requestPath.endsWith(s)) {
                                return@run true
                            }
                        }
                        false
                    }
            ) {
                illegalRequestMap[request.remoteAddr]?.run {
                    p1?.run {
                        reset()
                        characterEncoding = "UTF-8"
                        writer.print("警告，怀疑你在恶意攻击网站!!!")
                    }
                    return
                }
                //接收评论的时间间隔设置为2s
                var judgeTime = if (requestPath.endsWith(checkRMArr[0])) 2000 else 1000
                illegalRequestCountMap[request.remoteAddr] = illegalRequestCountMap[request.remoteAddr]?.apply {
                    this[0] = this[0] + 1
                } ?: arrayOf(1, System.currentTimeMillis()).apply {
                    illegalRequestCountTimer(request.remoteAddr)
                }

                //邮箱请求码请求每分钟200次
                var judgeCount = if (requestPath.endsWith(checkRMArr[6])) 200 else 30
                request.getSession(false)?.apply {
                    var preTime = illegalRequestCountMap[request.remoteAddr]!![1]
                    var key = request.remoteAddr
                    //一分钟的请求频次
                    if (illegalRequestCountMap[request.remoteAddr]!![0] > judgeCount) {
                        if (System.currentTimeMillis() - preTime < 60000) {
                            illegalRequestCountMap.remove(key)
                            return illegalAccessUrl(key, this)
                        }
                        illegalRequestCountMap.remove(key)
                    }
                    //两次请求间的频次
                    if (!requestPath.run {
                                if (this.endsWith(checkRMArr[5]))
                                    return@run true
                                false
                            } && System.currentTimeMillis() - preTime < judgeTime) {
                        return illegalAccessUrl(key, this)
                    }

                } ?: request.session.apply {
                    //                setAttribute("time", System.currentTimeMillis())
                    maxInactiveInterval = 60
                }
                illegalRequestCountMap[request.remoteAddr]?.run {
                    this[1] = System.currentTimeMillis()
                }
            }
        } catch (e: Exception) {
            p1?.run {
                reset()
                characterEncoding = "UTF-8"
                (this as HttpServletResponse).sendRedirect("/yuns/error/404.html")
                return
            }
        }
        p1?.characterEncoding = "UTF-8"
        p2?.doFilter(p0, p1)
    }

    private fun illegalRequestCountTimer(key: String) {
        illegalRequestCountMapTimer[key] = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    illegalRequestCountMap.remove(key)
                    illegalRequestCountMapTimer.remove(key)
                }
            }, 60 * 1000)
        }
    }

    private fun illegalAccessUrl(key: String, session: HttpSession) {
        illegalRequestMap[key] = ""
        illegalRequestTimerMap[key] = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    illegalRequestMap.remove(key)
                    illegalRequestTimerMap.remove(key)
                }
            }, 60 * 30 * 1000)
        }
        illegalRequestCountMap.remove(key)
        session.invalidate()
        return
    }
}


