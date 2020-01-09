package io.easybreezy.user.infrastructure

import io.easybreezy.user.model.User
import io.easybreezy.user.model.Users
import io.easybreezy.user.model.Repository as RepositoryInterface

class Repository : User.Repository(), RepositoryInterface {
    override fun findByToken(token: String): User {
        return find { Users.token eq token }.first()
    }

    override fun findByEmail(email: User.Email): User {
        return find { Users.email.address eq email.address() }.first()
    }
}
