package io.easybreezy.project.application.project.command

import io.easybreezy.infrastructure.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
data class NewCategory(
    val name: String,
    @Serializable(with = UUIDSerializer::class)
    val parent: UUID? = null
) {
    @Transient
    lateinit var project: String
}
