package io.dereknelson.lostcities.domains.user.entity

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
data class AuthorityEntity (
    @Id
    @Column(length = 50)
    var name: @NotNull @Size(max = 50) String
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is AuthorityEntity) {
            false
        } else name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hashCode(name)
    }

    // prettier-ignore
    override fun toString(): String {
        return "Authority{" +
                "name='" + name + '\'' +
                "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}