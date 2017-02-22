package com.sksi.ecobee.manager.model

import com.fasterxml.jackson.annotation.JsonProperty

class EcobeeAuthorizeResponse {
    String ecobeePin
    String code
    String scope
    @JsonProperty("expires_in")
    Integer expiresIn
    Integer interval
}
