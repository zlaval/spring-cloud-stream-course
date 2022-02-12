package com.zlrx.twitter.adapter.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "twitter")
data class TwitterConfiguration(
    var accessToken: String = "",
    var accessTokenSecret: String = "",
    var apiKey: String = "",
    var apiSecret: String = ""
)
