package com.ajobs.yuns.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import kotlin.random.Random


@Component
//@ConfigurationProperties(prefix = "spring.mail")
class MailSenderComp {
    @Autowired
    private lateinit var javaMailSender: JavaMailSender
    @Autowired
    private var executor: ExecutorService? = null

    var whoVerifyPswRequest: ConcurrentHashMap<String, String>? = null
    private var whoVerifyPswRequestTimer: ConcurrentHashMap<String, Timer>? = null

    //发送邮件的邮箱账号
    @Value("\${spring.mail.username}")
    lateinit var username: String

    /**
     * 寻找密码;
     */
    fun sendVerifyPswCode(toEmail: String, tempCode: String) {
//        if (executor == null)
//            synchronized(this) {
//                if (executor == null) executor = Executors.newCachedThreadPool()
//            }
        var verifyCode = Random.nextInt(100000, 1000000).toString()

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA)
        val date = Date()
        sendSimpleMail(toEmail, "密码设置", "<div><img src='cid:myLogo'><a href='http://localhost:8888/yuns/'>个人数据资源分享网络操作平台提醒您</a>：</div><br>\n" +
                "    <div style='margin-left:30px'><span>您的密码设置操作的验证码为:</span> <span style='margin-left:10px;color:red;border-bottom: 1px black dashed;'><b> $verifyCode </b></span></div><br>" +
                "<div style='margin-left:30px'>邮件发送时间大致为：<b>" + simpleDateFormat.format(date) + "</b>   验证码有效时长大致到： <b>" + simpleDateFormat.format(Date(date.time + 5 * 60 * 1000)) + "</b></div>", tempCode, verifyCode)
    }

    /**
     * 发送邮件
     */
    private fun sendSimpleMail(toEmail: String, subjectMail: String, mailText: String, code: String?, verifyCode: String) {
        executor?.execute {
            try {
                javaMailSender.send(MimeMessageHelper(javaMailSender.createMimeMessage(), true, "UTF-8").run {
                    setFrom(username)
                    setTo(toEmail)
                    setSubject(subjectMail)
                    setText(mailText, true)
                    addInline("myLogo", ClassPathResource("static/img/logo.png"));
                    setSentDate(Calendar.getInstance().run {
                        timeZone = TimeZone.getTimeZone(ZoneId.of(ZoneId.SHORT_IDS["CTT"]))
                        time
                    })
                    mimeMessage
                })
                if (code != null) {
                    /**
                     * 发送成功后
                     */
                    if (whoVerifyPswRequest == null)
                        synchronized(this) {
                            if (whoVerifyPswRequest == null) {
                                whoVerifyPswRequest = ConcurrentHashMap()
                            }
                        }

                    //添加一个cookie的值
                    whoVerifyPswRequest!![code] = verifyCode
                    removeWhoVerifyPswRequestEle2(code)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //throw e
            }
        }

    }

    /**
     * 自动5m 删除 timer he code
     */
    private fun removeWhoVerifyPswRequestEle2(key: String) {
        if (whoVerifyPswRequestTimer == null)
            synchronized(this) {
                if (whoVerifyPswRequestTimer == null) {
                    whoVerifyPswRequestTimer = ConcurrentHashMap()
                }
            }
        whoVerifyPswRequestTimer!![key] = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    whoVerifyPswRequest?.remove(key)
                    whoVerifyPswRequestTimer!!.remove(key)
                }
            }, 1000 * 60 * 5)
        }
    }

    /**
     *删除code和cancel timer
     */
    fun removeWhoVerifyPswRequestEle(key: String) {
            //根据c
            whoVerifyPswRequest?.remove(key)
            whoVerifyPswRequestTimer?.run {
                this[key]?.cancel()
                remove(key)
            }
    }

}