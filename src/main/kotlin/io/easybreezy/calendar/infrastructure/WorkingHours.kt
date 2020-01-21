package io.easybreezy.calendar.infrastructure

import io.easybreezy.user.model.Users
import org.jetbrains.exposed.dao.UUIDTable

object WorkingHours : UUIDTable("working_hours") {
//    val day = date("day")
    val count = integer("count")
    val userId = Absences.reference("user_id", Users)
}