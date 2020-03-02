package io.easybreezy.hr.application.profile.queryobject

import io.easybreezy.hr.model.profile.ContactDetailsTable
import io.easybreezy.hr.model.profile.Messengers
import io.easybreezy.hr.model.profile.NameTable
import io.easybreezy.hr.model.profile.PersonalData
import io.easybreezy.hr.model.profile.PersonalDataTable
import io.easybreezy.hr.model.profile.Profiles
import io.easybreezy.infrastructure.exposed.toUUID
import io.easybreezy.infrastructure.query.QueryObject
import io.easybreezy.infrastructure.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ProfileQO(private val userId: UUID) : QueryObject<Profile> {
    override suspend fun getData(): Profile {
        return transaction {
            (Profiles leftJoin Messengers).select {
                Profiles.userId eq userId
            }.toProfiles().single()
        }
    }
}

fun Iterable<ResultRow>.toProfiles(): List<Profile> {
    return fold(mutableMapOf<UUID, Profile>()) { map, resultRow ->
        val profile = resultRow.toProfile()
        val messengerId = resultRow.getOrNull(Messengers.id)
        val messenger = messengerId?.let { resultRow.toMessenger() }
        val current = map.getOrDefault(profile.id, profile)
        map[profile.id] = current.copy(messengers = current.messengers?.plus(listOfNotNull(messenger)))
        map
    }.values.toList()
}

fun ResultRow.toProfilesOne(): Profile {
    // return fold(mutableMapOf<UUID, Profile>()) { map, resultRow ->
    val profile = this.toProfile()
    val messengerId = this.getOrNull(Messengers.id)
    val messenger = messengerId?.let { this.toMessenger() }
    profile.messengers = profile.copy(messengers = profile.messengers?.plus(listOfNotNull(messenger))).messengers

    // profile.messengers = profile.messengers?.plus(listOfNotNull(messenger))

    return profile

    // val current = map.getOrDefault(profile.id, profile)
    // map[profile.id] = current.copy(messengers = current.messengers?.plus(listOfNotNull(messenger)))
    // map
    // }.values.toList()
}

private fun ResultRow.toProfile(): Profile {

    this[Profiles.contactDetails[ContactDetailsTable.phones]]
    return Profile(
        id = this[Profiles.id].toUUID(),
        gender = "asf"
    )
}

private fun ResultRow.toMessenger(): Messenger {
    return Messenger(
        type = this[Messengers.type].toString(),
        username = this[Messengers.username]
    )
}

@Serializable
data class Profile(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val gender: String,
    val about: String? = null,
    val workStack: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    var messengers: List<Messenger>? = null
)

@Serializable
data class Messenger(val type: String, val username: String)
