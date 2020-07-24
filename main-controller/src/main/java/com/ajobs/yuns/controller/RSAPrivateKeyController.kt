package com.ajobs.yuns.controller

import com.ajobs.yuns.tool.RSAKeyPairGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller("rSAPrivateKeyController")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequestMapping("/k")
class RSAPrivateKeyController {

    @Autowired
    private lateinit var rsaKeyPairGenerator: RSAKeyPairGenerator

    @PostMapping("/pk")
    @ResponseBody
    fun privateKey(): String = rsaKeyPairGenerator.base64StringPrivateKey

    @PostMapping("/pubk")
    @ResponseBody
    fun publicKey(): String = rsaKeyPairGenerator.base64StringPublicKey
}