package com.sksi.ecobee.manager.model

import com.fasterxml.jackson.annotation.JsonProperty

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class EcobeeAccessTokenResponse {
    @JsonProperty("access_token")
    String accessToken
    @JsonProperty("token_type")
    String tokenType
    @JsonProperty("expires_in")
    Integer expiresIn
    @JsonProperty("refresh_token")
    String refreshToken
    String scope
}
