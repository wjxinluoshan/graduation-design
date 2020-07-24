package com.ajobs.yuns.component

import com.ajobs.yuns.tool.RSAKeyPairGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Component("rSAPrivateKeyComponent")
class RSAPrivateKeyComponent {

    @Autowired
    private lateinit var rsaKeyPairGenerator: RSAKeyPairGenerator

    fun privateKey(): String = rsaKeyPairGenerator.base64StringPrivateKey
}