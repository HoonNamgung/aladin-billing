package com.example.api.users.infra.google.client

import com.example.api.config.oauth.GoogleProperties
import com.example.api.users.dto.GoogleUserInfoResponse
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GoogleHttpClient(private val props: GoogleProperties) {

    private val restTemplate = RestTemplate()

    fun getAccessToken(code: String): String {
        val params = mapOf(
            "code" to code,
            "client_id" to props.clientId,
            "client_secret" to props.clientSecret,
            "redirect_uri" to props.redirectUri,
            "grant_type" to "authorization_code"
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val body = params.entries.joinToString("&") { "${it.key}=${it.value}" }

        val response = restTemplate.postForEntity(
            props.tokenUrl,
            HttpEntity(body, headers),
            Map::class.java
        )
        return response.body?.get("access_token")?.toString()
            ?: throw IllegalArgumentException("Failed to get access token")
    }

    fun getUserInfo(accessToken: String): GoogleUserInfoResponse {
        val headers = HttpHeaders().apply {
            setBearerAuth(accessToken)
        }
        val response = restTemplate.exchange(
            props.userInfoUrl,
            HttpMethod.GET,
            HttpEntity(null, headers),
            GoogleUserInfoResponse::class.java
        )

        return response.body ?: throw IllegalArgumentException("Failed to fetch user info")
    }
}
