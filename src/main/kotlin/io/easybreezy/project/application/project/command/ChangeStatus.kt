package io.easybreezy.project.application.project.command

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Serializable
data class ChangeStatus(
    val name: String
) {
    @Transient
    lateinit var statusId: UUID
    @Transient
    lateinit var project: String
}
