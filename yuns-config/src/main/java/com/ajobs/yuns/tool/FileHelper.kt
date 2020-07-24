package com.ajobs.yuns.tool

import org.springframework.util.ResourceUtils

class FileHelper {

    companion object {
        fun getClasspath(): String = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX).path

        fun getUserPath(): String = "static/user"

    }
}