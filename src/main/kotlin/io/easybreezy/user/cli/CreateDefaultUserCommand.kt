package io.easybreezy.user.cli

import io.easybreezy.application.HikariDataSource
import io.easybreezy.application.SystemConfiguration
import io.easybreezy.infrastructure.exposed.TransactionManager
import io.easybreezy.user.model.Email
import io.easybreezy.user.model.EmailTable
import io.easybreezy.user.model.Password
import io.easybreezy.user.model.User
import io.easybreezy.user.model.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select

class CreateDefaultUserCommand {

    companion object {
        private const val EMAIL = "admin@admin.my"

        @JvmStatic
        fun main(args: Array<String>): Unit = runBlocking {
            val configProvider = SystemConfiguration
            val dataSource = HikariDataSource(configProvider)
            val database = Database.connect(dataSource)
            val transaction = TransactionManager(database)
            transaction {
                if (Users.select { Users.email[EmailTable.email] eq EMAIL }.count().compareTo(0) == 0) {
                    User.createAdmin(
                        Email.create(EMAIL),
                        Password.create("123")
                    )
                }
            }
        }
    }
}
