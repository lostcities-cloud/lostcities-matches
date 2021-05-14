package io.dereknelson.lostcities.common

/**
 * Application constants.
 */
object Constants {
    // Regex for acceptable logins
    const val LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$"
    const val SYSTEM_ACCOUNT = "system"
    const val DEFAULT_LANGUAGE = "en_US"
    const val ANONYMOUS_USER = "anonymoususer"
}