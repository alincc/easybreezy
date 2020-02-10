package io.easybreezy.user.application

import com.google.inject.Inject
import io.easybreezy.infrastructure.ktor.Error
import io.easybreezy.infrastructure.ktor.validate
import io.easybreezy.user.model.Email
import io.easybreezy.user.model.Repository
import io.easybreezy.user.model.User
import org.valiktor.Constraint
import org.valiktor.Validator
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull

class Validation @Inject constructor(private val repository: Repository) {

    object Unique : Constraint {
        override val name: String
            get() = "User with this email already exist"
    }

    private fun <E> Validator<E>.Property<String?>.isUnique(): Validator<E>.Property<String?> =
        this.validate(Unique) { value ->
            repository.findByEmail(Email.create(value!!)) !is User
        }

    fun onInvite(command: Invite): List<Error> {
        return validate(command) {
            validate(Invite::email).isNotBlank().isNotNull().isUnique()
        }
    }
}
