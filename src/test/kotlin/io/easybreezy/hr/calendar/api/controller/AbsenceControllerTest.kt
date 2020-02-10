package io.easybreezy.hr.calendar.api.controller

import io.easybreezy.hr.createAbsence
import io.easybreezy.hr.createWorkingHour
import io.easybreezy.rollbackTransaction
import io.easybreezy.testApplication
import io.easybreezy.testDatabase
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

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
                        json {
                            "startedAt" to "2023-07-13"
                            "endedAt" to "2023-08-13"
                            "reason" to "VACATION"
                            "userId" to memberId.toString()
                            "comment" to "Test Comment"
                        }.toString()
                    )
                }) {
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
                with(handleRequest(HttpMethod.Get, "/api/hr/absences")) {
                    Assertions.assertTrue(response.content?.contains("Test Comment")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testAbsenceUpdate() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                val absenceId = database.createAbsence(memberId)

                with(handleRequest(HttpMethod.Post, "/api/hr/absences/$absenceId") {
                    addHeader("Content-Type", "application/json")
                    setBody(
                        json {
                            "startedAt" to "2023-08-13"
                            "endedAt" to "2023-10-13"
                            "reason" to "DAYON"
                            "userId" to memberId.toString()
                            "comment" to "Test Comment 2"
                        }.toString()
                    )
                }) {
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
                with(handleRequest(HttpMethod.Get, "/api/hr/absences")) {
                    Assertions.assertTrue(response.content?.contains("Test Comment 2")!!)
                    Assertions.assertTrue(response.content?.contains("DAYON")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testRemoveAbsence() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                val absenceId = database.createAbsence(memberId)

                with(handleRequest(HttpMethod.Delete, "/api/hr/absences/$absenceId")) {
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }

                with(handleRequest(HttpMethod.Get, "/api/hr/absences")) {
                    Assertions.assertFalse(response.content?.contains("Test Comment")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testAbsencesListing() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                database.createAbsence(memberId)

                with(handleRequest(HttpMethod.Get, "/api/hr/absences")) {
                    println(response.content)
                    Assertions.assertTrue(response.content?.contains("Test Comment")!!)
                    Assertions.assertTrue(response.content?.contains("hasMore")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testAbsenceShow() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                val absenceId = database.createAbsence(memberId)

                with(handleRequest(HttpMethod.Get, "/api/hr/absences/$absenceId")) {
                    Assertions.assertTrue(response.content?.contains("Test Comment")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }



    @Test
    fun testWorkingHoursCreate() {
        val memberId = UUID.randomUUID()
        val database = testDatabase

        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                with(handleRequest(HttpMethod.Post, "/api/hr/absences/working-hours") {
                    addHeader("Content-Type", "application/json")
                    setBody(
                        json {
                            "userId" to memberId.toString()
                            "workingHours" to jsonArray {
                                +json {
                                    "day" to "2020-05-12"
                                    "count" to 5
                                }
                                +json {
                                    "day" to "2020-05-16"
                                    "count" to 6
                                }
                            }
                        }.toString()

                    )
                }) {
                    println(response.content)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }

                with(handleRequest(HttpMethod.Get, "/api/hr/absences/working-hours")) {
                    Assertions.assertTrue(response.content?.contains("2020-05-16")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testWorkingHoursUpdate() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                val workingHourId = database.createWorkingHour(memberId)

                with(handleRequest(HttpMethod.Put, "/api/hr/absences/working-hours") {
                    addHeader("Content-Type", "application/json")
                    setBody(
                        json {
                            "workingHours" to json {
                                workingHourId.toString() to json {
                                    "day" to "2020-09-19"
                                    "count" to 4
                                }
                                // ...
                            }
                        }.toString()
                    )
                }) {
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
                with(handleRequest(HttpMethod.Get, "/api/hr/absences/working-hours")) {
                    Assertions.assertTrue(response.content?.contains("2020-09-19")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testWorkingHoursDelete() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                val workingHourId = database.createWorkingHour(memberId)

                with(handleRequest(HttpMethod.Delete, "/api/hr/absences/working-hours") {
                    addHeader("Content-Type", "application/json")
                    setBody(
                        json {
                            "workingHours" to jsonArray {
                                +JsonPrimitive(workingHourId.toString())
                            }
                        }.toString()
                    )
                }) {
                    println(response.content)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
                with(handleRequest(HttpMethod.Get, "/api/hr/absences/working-hours")) {
                    Assertions.assertFalse(response.content?.contains("5")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testWorkingHoursListing() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                database.createWorkingHour(memberId)

                with(handleRequest(HttpMethod.Get, "/api/hr/absences/working-hours")) {
                    println(response.content)
                    Assertions.assertTrue(response.content?.contains("5")!!)
                    Assertions.assertTrue(response.content?.contains(memberId.toString())!!)
                    Assertions.assertTrue(response.content?.contains("hasMore")!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }

    @Test
    fun testWorkingHourShow() {
        val memberId = UUID.randomUUID()
        val database = testDatabase
        withTestApplication({ testApplication(memberId, emptySet(), database) }) {
            rollbackTransaction(database) {
                val workingHourId = database.createWorkingHour(memberId)

                with(handleRequest(HttpMethod.Get, "/api/hr/absences/working-hours/$workingHourId")) {
                    Assertions.assertTrue(response.content?.contains("5")!!)
                    Assertions.assertTrue(response.content?.contains(memberId.toString())!!)
                    Assertions.assertEquals(HttpStatusCode.OK, response.status())
                }
            }
        }
    }
}
