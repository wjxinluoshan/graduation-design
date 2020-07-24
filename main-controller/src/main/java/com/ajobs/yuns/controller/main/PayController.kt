package com.ajobs.yuns.controller.main

import com.ajobs.yuns.mapperImp.UserMapperImp
import com.alipay.api.AlipayApiException
import com.alipay.api.AlipayClient
import com.alipay.api.DefaultAlipayClient
import com.alipay.api.internal.util.AlipaySignature
import com.alipay.api.request.AlipayTradePagePayRequest
import com.alipay.api.request.AlipayTradeRefundRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/pc")
class PayController {

    private val APP_ID = "2021001151646029"
    private val APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDuYyLlmNPdA1LTV+0YVVc9eGRTDz70T6iLt/WRfXHRVj/i5xnwrnmZVULUjCVZ0SQP2RToUMY2hj3xQe5ax0pt++fjdzquAd5sO78XLMCxaAAVLEJm5RYhOSfHmvZ2dPycSQpnux/JH9pi6RZQbC9nO/ohovKZPZCwf+b4dGZ41sWXe/lVAkRUGSxV3yw+f7sV9ORz7LCEhR1oLItlsGgLVEfjgQtKVArPTfCZGcJBITZy/f1wXbLL+VUy9mVU8YgsT8XKjSSEB9K5hy6tLbW3LhWhc70NdroEeFf0WV7LY1LDRGhyeD316pWJUTQPgxGD21cjSc2rDlz5V57J5t5zAgMBAAECggEABMEPYhrRJtg5H6PiQ5j4G266OXnVXJP2HYs2p4cj7PPWZFUNjGI+ZJNxuMFuwULNgE9o/lUWDuJQe+4/ranJzzW14zBuiCfbYrcyUWkAEsmrLsDqNhkdft4f7/gkMCqXGxrNzVHLCXzMIyrY2hO+QkXM8DzKr8mtiAeU+KGBpalA/tb/EfTQXNFILQLon0EO2M2Uz6a8Vx5bYkqelt5YJvFqFnKTGlY1Puf8j7FlhVxGYy4b2HjQGohO1OSwR+D2AILbw1aebwFphvo04J54HYq2J5hUIV6aKKB7PoRWaXO9lARCRPatdhNmLGPWQJAKeAYoL5Sk9aVM33j2F/O9aQKBgQD6Yf/tHxgQEmwdzGcHrBRD4RyET9aNJokivwrA45uNSqPnDnrqYcfFhaneYF0f5oiYUf6UqvmjdX7jgnGnZnLhURnkCkL/8sc52a26GJeG5UtL2npW2d0dJVcUX2smJ50a1Dj+pkQ7lKWzVHhiZ4wzd+EOyzVbTOumZryxlyE9xwKBgQDzvD5d5aGrnNMOesM7dOgEpo+Wi/xJAJ1Gufnbc/3vERR2A1Clh1YYzZxXOuZP8N4BJjBfD0zVr1JqZylu4rH+HwIebommHCc5zUtIi1AdMIYzrpeE0O9Mt3Brigs5RL+Nk2j7eu2b/TUqU8rJWfOIDD8v6XKNu4ORAqyjw79J9QKBgQDHoqfKSsaG2gl2YOmtRlkKHkz8eiPZe2ZQW/6Y+WGncGHq8rKyerAsH3znn/HgY5F50baolwJggGhhwjoYfSkP2fhIyJ3PMaYDzDombUEz/1Lf8h0lbqRMft9FkSb/zwoGwDuJXsx8rPLJX4p0+5bf2q+JeiDa4SPrE3NULeoFNQKBgFVAc6QOLNVE3kHDSMRkKL9vruuefRjfwSM6650J3jkfhUOd9zhetQup51RPgr1VZ6GJRQQK6voW/8G94a0qV82l9OzGBxl14XaDSyDNsbD0BwzOtaq8FeeEhirnNvPKCyIu/Fpx7ehQ0abDdjulXdwpRHSyt+HBQ8yV3eLOFft9AoGAOtIFfZInxnu3EMGTeT6kUgV0CITzYNIDOLXo/QjPWAp7BltF+GeA/vCMhT8Z18CIKkqFkDS1yHr/B4AOIyFUhViH+jSnqvnz/a00GAxZOGJ2CmlyokelZbCiRZj0/AL9rCZTDJvuvZ3Z5xV5R99+O6fnrVTUW8ydlmwc4N2UVTw="
    private val FORMAT = "JSON"
    private val CHARSET = "utf-8"
    private val ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz935UsO/zEIZCe5d8+6ces7qUmG6okRsXJi1fSPZekRl3rsz/vBvTJeER7NAXod3W94M9cKXXCRN1DVUBiriOQdopu9FLu0qe4IFd0FtCuCKnzG7/6HTg9qC8UbbeZfvhlcnLHwGroykPtEu4p7iUkxPkKuKbU+zi4CHTzfvPCJ50U9ur3lLdvHZrp6ZEiXMmO5aBWDSqig7aKZCl4e9xxDxbqjhNFZMFmTlr1/zP43C9KKy023hh2Quy3zRXJLn5SymenO/Vgo5PtNO2xogfGGhKLCE/DwP7eLy/airN9/EgcNpx30dqYB5WhXMqvnP2QOGq04f1/pK+YTn0atbrQIDAQAB"
    private val SIGN_TYPE = "RSA2"

    @Autowired
    private lateinit var userMapperImp: UserMapperImp

    @Autowired
    @Qualifier("dbTransactionManager")
    private lateinit var dbTransactionManager: DataSourceTransactionManager

    /**
     * 返回支付页面
     */
    @PostMapping("/pp")
    fun returnPayPage(otn: String, httpServletResponse: HttpServletResponse) {

        val alipayClient: AlipayClient = DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE) //获得初始化的AlipayClient

        val alipayRequest = AlipayTradePagePayRequest().apply {
            //        alipayRequest.returnUrl = "http://domain.com/CallBack/return_url.jsp"
            notifyUrl = "http://119.3.182.178:8080/yuns/pc/nu" //在公共参数中设置回跳和通知地址

            bizContent = "{" +
                    "    \"out_trade_no\":\"${otn}\"," +
                    "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                    "    \"total_amount\":10.00," +
                    "    \"subject\":\"上传空间扩容\"," +
                    "    \"body\":\"本次购买可以将您的上传空间容量增至***\"" +
                    "  }" //填充业务参数

        } //创建API对应的request
        var form: String? = ""
        try {
            form = alipayClient.pageExecute(alipayRequest).body //调用SDK生成表单
        } catch (e: AlipayApiException) {
            e.printStackTrace()
        }
        httpServletResponse.run {
            contentType = "text/html;charset=$CHARSET"
            writer.write(form) //直接将完整的表单html输出到页面
            writer.flush()
            writer.close()
        }
    }

    /**
     * 接收异步通知:还是有失败的机率所以需要使用分布式，但是目前解决不了呀
     */
    @PostMapping("/nu")
    fun notifyUrl(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        var map = httpServletRequest.parameterMap as Map<String, String>
        var signVerified = AlipaySignature.rsaCheckV1(map, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE)  //调用SDK验证签名
        if (signVerified) {
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            //商户订单号
            val out_trade_no = String(httpServletRequest.getParameter("out_trade_no").toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            //支付宝交易号
            val trade_no = String(httpServletRequest.getParameter("trade_no").toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            //得到用户的id
            //将更新用的数据
            try {
                userMapperImp.updateUser(mutableListOf("*****"), mutableListOf("maxStorage"), mutableListOf("id"), mutableListOf(out_trade_no[out_trade_no.length - 1].toInt()))
            } catch (e: Exception) {
                e.printStackTrace()
                //数据更新失败
                /**
                 * 记录异常的out_trade_no;  下次恢复
                 */
                userMapperImp.createExtendStorageErrTable()
                val transactionTemplate = TransactionTemplate(dbTransactionManager)
                transactionTemplate.execute<Any?> { txStatus: TransactionStatus? ->
                    try {
                        userMapperImp.insertErr(out_trade_no)
                    } catch (e: java.lang.Exception) {
                        txStatus?.setRollbackOnly()
                        e.printStackTrace()
                        /**
                         * 退款
                         */
                        var alipayClient: AlipayClient? = DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY)
                        val request = AlipayTradeRefundRequest()
                        request.bizContent = "{" +
                                "    \"out_trade_no\":\"$out_trade_no\"," +
                                "    \"trade_no\":\"$trade_no\"," +
                                "    \"refund_amount\":10.00," +
                                "  }"
                        val response = alipayClient!!.execute(request)
                        if (response.isSuccess) {
                            println("调用成功")
                        } else {
                            println("调用失败")
                        }
                    }
                    null
                }
            }
            httpServletResponse.writer.println("success")
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            httpServletResponse.writer.println("failure")

        }

    }


}