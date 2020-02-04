package io.easybreezy.user.api.controller

import com.google.inject.Inject
import io.easybreezy.infrastructure.ktor.Controller
import io.easybreezy.infrastructure.ktor.respondOk
import io.easybreezy.infrastructure.ktor.respondData
import io.easybreezy.infrastructure.query.QueryExecutor
import io.easybreezy.user.application.Confirm
import io.easybreezy.user.application.Handler
import io.easybreezy.user.application.Invite
import io.easybreezy.user.application.Validation
import io.easybreezy.user.application.queryobject.UserQO
import io.easybreezy.user.application.queryobject.UsersQO
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserController @Inject constructor(
    private val handler: Handler,
    private val validation: Validation,
    private val queryExecutor: QueryExecutor
) : Controller() {

    suspend fun index() {
        call.respondData(queryExecutor.execute(UsersQO()))
    }

    suspend fun me(id: UUID) {
        call.respondData(queryExecutor.execute(UserQO(id)))
    }

    suspend fun invite(command: Invite) {
        validation.onInvite(command)
        handler.handleInvite(command)

        call.respondOk()
    }

    suspend fun confirm(command: Confirm) {
        handler.handleConfirm(command)

        call.respondOk()
    }
}
