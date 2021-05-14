package io.dereknelson.lostcities.api.users

import com.fasterxml.jackson.annotation.JsonProperty

class JwtTokenDto(
    @get:JsonProperty("id_token") var idToken: String
)