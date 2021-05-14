package io.dereknelson.lostcities.library.security.jwt

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import javax.annotation.PostConstruct

@Service
class TokenProvider(

) {
    private val log = LoggerFactory.getLogger(TokenProvider::class.java)
    private var secretKey: String? = null
    private var tokenValidityInMilliseconds: Long = 0
    private var tokenValidityInMillisecondsForRememberMe: Long = 0

    private var secret: String = "ZmNhZmUyNzNkNTE1ZTdiZDA2MmJjNWY4MWE2NzFlMTRkMmViNGE3M2E0YTRiYjg1ZGMxMDY1NGZkNjhhMTdmMjI4OTA5NTUzMzkyZjI1NDUyNjFlY2M3MjBkY2Y2OTAwMGU3NDQwYWMxNmZiNTJjZmZjMzkxMmU1OGZmYzQxOGU="
    //@Value("application.authentication.jwt.token-validity-in-seconds")
    private var tokenValidityInSeconds: String = "0"
    //@Value("application.security.authentication.jwt.token-validity-in-seconds-for-remember-me")
    private var tokenValidityInSecondsForRememberMe: String = "0"

    @PostConstruct
    fun init() {
        secretKey = secret
        tokenValidityInMilliseconds = 1000 * tokenValidityInSeconds.toLong()
        tokenValidityInMillisecondsForRememberMe =
            1000 * tokenValidityInSecondsForRememberMe.toLong()
    }

    fun createToken(authentication: Authentication, rememberMe: Boolean): String {
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))
        val now = Date().time
        val validity: Date
        validity = if (rememberMe) {
            Date(now + tokenValidityInMillisecondsForRememberMe)
        } else {
            Date(now + tokenValidityInMilliseconds)
        }
        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(SignatureAlgorithm.HS512, secretKey!!)
            .setExpiration(validity)
            .compact()
    }

    fun getAuthentication(token: String?): Authentication {
        val claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body
        val authorities: Collection<GrantedAuthority> = Arrays.stream(
            claims[AUTHORITIES_KEY].toString().split(",".toRegex()).toTypedArray()
        )
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList())
        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            log.info("Invalid JWT signature.")
            log.trace("Invalid JWT signature trace: {}", e)
        } catch (e: MalformedJwtException) {
            log.info("Invalid JWT token.")
            log.trace("Invalid JWT token trace: {}", e)
        } catch (e: ExpiredJwtException) {
            log.info("Expired JWT token.")
            log.trace("Expired JWT token trace: {}", e)
        } catch (e: UnsupportedJwtException) {
            log.info("Unsupported JWT token.")
            log.trace("Unsupported JWT token trace: {}", e)
        } catch (e: IllegalArgumentException) {
            log.info("JWT token compact of handler are invalid.")
            log.trace("JWT token compact of handler are invalid trace: {}", e)
        }
        return false
    }

    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }
}