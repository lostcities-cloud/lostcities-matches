package io.dereknelson.lostcities.library.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.stream.Stream

/**
 * Utility class for Spring Security.
 */
object SecurityUtils {
    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    val currentUserLogin: Optional<String>
        get() {
            val securityContext = SecurityContextHolder.getContext()
            return Optional.ofNullable(extractPrincipal(securityContext.authentication))
        }

    private fun extractPrincipal(authentication: Authentication?): String? {
        if (authentication == null) {
            return null
        } else if (authentication.principal is UserDetails) {
            val springSecurityUser = authentication.principal as UserDetails
            return springSecurityUser.username
        } else if (authentication.principal is String) {
            return authentication.principal as String
        }
        return null
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    val currentUserJWT: Optional<String?>
        get() {
            val securityContext = SecurityContextHolder.getContext()
            return Optional.ofNullable(securityContext.authentication)
                .filter { authentication: Authentication -> authentication.credentials is String }
                .map { authentication: Authentication -> authentication.credentials as String }
        }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    val isAuthenticated: Boolean
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            return authentication != null &&
                    getAuthorities(authentication).noneMatch { anObject: String? ->
                        AuthoritiesConstants.ANONYMOUS.equals(
                            anObject
                        )
                    }
        }

    /**
     * If the current user has a specific authority (security role).
     *
     *
     * The name of this method comes from the `isUserInRole()` method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    fun isCurrentUserInRole(authority: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null &&
                getAuthorities(authentication).anyMatch { anObject: String? -> authority.equals(anObject) }
    }

    private fun getAuthorities(authentication: Authentication): Stream<String> {
        return authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
    }
}