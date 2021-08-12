package io.dereknelson.lostcities.matches.exceptions

import java.io.Serializable

class FieldError (
    val objectName: String,
    val field: String,
    val message: String
) : Serializable