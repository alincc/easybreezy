package io.easybreezy.user.model

import io.easybreezy.infrastructure.event.user.Confirmed
import io.easybreezy.infrastructure.event.user.Invited
import io.easybreezy.infrastructure.exposed.dao.AggregateRoot
import io.easybreezy.infrastructure.exposed.dao.PrivateEntityClass
import io.easybreezy.infrastructure.exposed.dao.embedded
import io.easybreezy.infrastructure.exposed.type.jsonb
import io.easybreezy.infrastructure.ktor.auth.Role
import io.easybreezy.infrastructure.postgresql.PGEnum
import kotlinx.serialization.set
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime
import java.util.UUID

class User private constructor(id: EntityID<UUID>) : AggregateRoot<UUID>(id) {
    private var email by Users.email
    private var password by Users.password
    private var roles by Users.roles
    private var status by Users.status
    private var token by Users.token
    private var createdAt by Users.createdAt

    fun confirm(password: Password, firstName: String, lastName: String) {
        this.password = password
        this.status = Status.ACTIVE
        resetToken()

        this.addEvent(Confirmed(this.id.value, firstName, lastName))
    }

    fun email(): String {
        return this.email.address
    }

    private fun resetToken() {
        token = null
    }

    companion object : PrivateEntityClass<UUID, User>(object : Repository() {}) {
        fun invite(email: Email, roles: MutableSet<Role>): User {
            return User.new {
                this.email = email
                this.roles = roles
                this.status = Status.WAIT_CONFIRM
                this.token = Token.generate()
                this.createdAt = LocalDateTime.now()
                this.addEvent(Invited(this.id.value))
            }
        }

        fun createAdmin(email: Email, password: Password): User {
            return User.new {
                this.email = email
                this.password = password
                this.roles = mutableSetOf(Role.ADMIN)
                this.status = Status.ACTIVE
            }
        }
    }

    abstract class Repository : EntityClass<UUID, User>(Users, User::class.java) {
        override fun createInstance(entityId: EntityID<UUID>, row: ResultRow?): User {
            return User(entityId)
        }
    }
}

enum class Status {
    ACTIVE, WAIT_CONFIRM
}

object Users : UUIDTable() {
    val token = varchar("token", 255).nullable()
    val status = customEnumeration(
        "status",
        "user_status",
        { value -> Status.valueOf(value as String) },
        { PGEnum("user_status", it) }).default(Status.ACTIVE)
    val roles = jsonb("roles", Role.serializer().set)
    val password = embedded<Password>(PasswordTable)
    val email = embedded<Email>(EmailTable)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}
