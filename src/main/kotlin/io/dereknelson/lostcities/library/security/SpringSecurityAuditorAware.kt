package io.dereknelson.lostcities.library.security

import io.dereknelson.lostcities.common.Constants
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component
class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of(SecurityUtils.currentUserLogin.orElse(Constants.SYSTEM_ACCOUNT))
    }
}