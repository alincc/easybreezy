package io.easybreezy.hr.calendar.api.controller

import com.google.gson.Gson
import io.easybreezy.*
import io.easybreezy.infrastructure.ktor.auth.Session
import io.easybreezy.infrastructure.ktor.auth.UserPrincipal
import io.easybreezy.testDatabase
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.DEFAULT_REPETITION_ATTEMPTS
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.util.*

class AbsenceControllerTest {
    @Test
    fun testAbsenceCreate() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                with(handleRequest(HttpMethod.Post, "/api/hr/absences") {
                    addHeader("Content-Type", "application/json")
                    setBody(
                        Gson().toJson(
                            mapOf(
                                "startedAt" to "2023-07-13",
                                "endedAt" to "2023-08-13",
                                "reason" to "VACATION",
                                "userId" to memberId,
                                "comment" to "Test Comment",
                                "location" to "Belarus"
                            )
                        )
                    )
                }) {
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
                with(handleRequest(HttpMethod.Get, "/api/hr/absences")) {
                    println(response.content)
                    Assertions.assertTrue(response.content?.contains("Test Comment")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }
}