package io.easybreezy.hr.application.hr.command

import io.easybreezy.infrastructure.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ApplySalary(
    val amount: Int,
    val comment: String,
    @Serializable(with = LocalDateSerializer::class)
    val appliedAt: LocalDate = LocalDate.now()
)