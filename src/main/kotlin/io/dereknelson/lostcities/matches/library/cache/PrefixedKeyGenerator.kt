package io.dereknelson.lostcities.matches.library.cache

import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import java.io.Serializable
import java.lang.reflect.Method
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class PrefixedKeyGenerator(gitProperties: GitProperties?, buildProperties: BuildProperties?) : KeyGenerator {
    val prefix: String
    private fun generatePrefix(gitProperties: GitProperties?, buildProperties: BuildProperties?): String {
        var shortCommitId: String? = null
        if (gitProperties != null) {
            shortCommitId = gitProperties.shortCommitId
        }
        var time: Instant? = null
        var version: String? = null
        if (buildProperties != null) {
            time = buildProperties.time
            version = buildProperties.version
        }
        val p: Any = ObjectUtils.firstNonNull(shortCommitId, time, version, RandomStringUtils.randomAlphanumeric(12))!!
        return if (p is Instant) {
            DateTimeFormatter.ISO_INSTANT.format(p)
        } else p.toString()
    }

    override fun generate(target: Any, method: Method, vararg params: Any): Any {
        return PrefixedSimpleKey(prefix, method.name, *params)
    }

    init {
        prefix = generatePrefix(gitProperties, buildProperties)
    }
}

private class PrefixedSimpleKey(prefix: String, methodName: String, vararg elements: Any?) : Serializable {
    private val prefix: String
    private val params: Array<Any?>
    private val methodName: String
    private var hashCode: Int
    override fun equals(other: Any?): Boolean {
        return this === other ||
            other is PrefixedSimpleKey && prefix == other.prefix && methodName == other.methodName &&
            Arrays.deepEquals(params, other.params)
    }

    override fun hashCode(): Int {
        return hashCode
    }

    override fun toString(): String {
        return prefix + " " + javaClass.simpleName + methodName + " [" + StringUtils.arrayToCommaDelimitedString(
            params
        ) + "]"
    }

    init {
        Assert.notNull(prefix, "Prefix must not be null")
        Assert.notNull(elements, "Elements must not be null")
        this.prefix = prefix
        this.methodName = methodName
        params = arrayOfNulls(elements.size)
        System.arraycopy(elements, 0, params, 0, elements.size)
        hashCode = prefix.hashCode()
        hashCode = 31 * hashCode + methodName.hashCode()
        hashCode = 31 * hashCode + Arrays.deepHashCode(params)
    }
}
