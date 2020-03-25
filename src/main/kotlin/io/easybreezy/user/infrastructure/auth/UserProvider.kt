package io.easybreezy.user.infrastructure.auth

import com.google.inject.Inject
import io.easybreezy.infrastructure.ktor.auth.PrincipalProvider
import io.easybreezy.infrastructure.ktor.auth.UserPrincipal
import io.easybreezy.user.infrastructure.UserRepository
import io.easybreezy.user.model.EmailTable
import io.easybreezy.user.model.Status
import io.easybreezy.user.model.Users
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.jwt.JWTCredential
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserProvider @Inject constructor(private val repository: UserRepository) : PrincipalProvider<UserPrincipal> {
    override fun load(credential: UserPasswordCredential, clientIp: String): UserPrincipal? {
        return transaction {
            val resultRow =
                Users.select { (Users.email[EmailTable.email] eq credential.name) and (Users.status eq Status.ACTIVE) }
                    .singleOrNull()
            resultRow?.let {
                val user = repository.wrapRow(it)
                if (user.password().isValid(credential.password)) {
                    UserPrincipal(UUID.fromString(resultRow[Users.id].toString()), setOf())
                } else null
            }
        }
    }

    override fun refresh(principal: UserPrincipal): UserPrincipal? {
        return UserPrincipal(principal.id, principal.roles)
    }

    fun load(credential: JWTCredential): UserPrincipal? {
        return UserPrincipal(UUID.fromString(credential.payload.subject), setOf())
    }
}
