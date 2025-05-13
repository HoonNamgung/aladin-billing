package com.example.api.config.oauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "google")
class GoogleProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var tokenUrl: String
    lateinit var userInfoUrl: String
    lateinit var redirectUri: String
}
