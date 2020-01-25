package io.easybreezy.infrastructure.event.profile

import io.easybreezy.infrastructure.event.Event
import io.easybreezy.infrastructure.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MessengerUsernameChanged(
    @Serializable(with = UUIDSerializer::class) val profile: UUID,
    val type: String
) : Event {
    override val key
        get() = Companion

    companion object : Event.Key<MessengerUsernameChanged>
}
