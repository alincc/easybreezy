package io.easybreezy.hr.model.profile

import io.easybreezy.infrastructure.exposed.dao.AggregateRoot
import io.easybreezy.infrastructure.exposed.dao.Embeddable
import io.easybreezy.infrastructure.exposed.dao.EmbeddableColumn
import io.easybreezy.infrastructure.exposed.dao.PrivateEntityClass
import io.easybreezy.infrastructure.exposed.type.jsonb
import io.easybreezy.infrastructure.postgresql.PGEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.set
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.`java-time`.date
import java.time.LocalDate
import java.util.UUID

class Profile private constructor(id: EntityID<UUID>) : AggregateRoot<UUID>(id) {
    private var personalData by Profiles.personalData
    private var contactDetails by Profiles.contactDetails
    private var messengers by Messenger via Messengers

    class PersonalData private constructor() : Embeddable() {
        private var birthday by Profiles.PersonalData.birthday
        private var gender by Profiles.PersonalData.gender
        private var about by Profiles.PersonalData.about
        // private var name by Profiles.PersonalData.name

        companion object : EmbeddableClass<PersonalData>(Profiles) {
            override fun createInstance(): PersonalData {
                return PersonalData()
            }

            fun create(birthday: LocalDate, gender: Profiles.Gender, about: String) = PersonalData.new {
                this.birthday = birthday
                this.gender = gender
                this.about = about
            }
        }

        // class Name private constructor() : Embeddable() {
        //     private var firstName by Profiles.PersonalData.Name.firstName
        //     private var lastName by Profiles.PersonalData.Name.lastName
        //
        //     companion object : EmbeddableClass<Name>(Profiles) {
        //         override fun createInstance(): Name {
        //             return Name()
        //         }
        //
        //         fun create(firstName: String, lastName: String) = Name.new {
        //             this.firstName = firstName
        //             this.lastName = lastName
        //         }
        //     }
        // }
    }

    class ContactDetails private constructor() : Embeddable() {
        private var phones by Profiles.ContactDetails.phones

        companion object : EmbeddableClass<ContactDetails>(Profiles) {
            override fun createInstance(): ContactDetails {
                return ContactDetails()
            }

            fun create( phones: Set<Phone>) = ContactDetails.new {
                this.phones = phones
            }
        }
    }

    companion object : PrivateEntityClass<UUID, Profile>(object : Repository() {}) {
    }

    fun addMessenger(type: String, username: String) {
        messengers = SizedCollection(messengers + Messenger.create(this, Messengers.Type.valueOf(type.toLowerCase()), username))
    }

    fun updatePersonalData(personalData: PersonalData) {
        this.personalData = personalData
    }

    fun updateContactDetails(contactDetails: ContactDetails) {
        this.contactDetails = contactDetails
    }

    abstract class Repository : EntityClass<UUID, Profile>(
        Profiles, Profile::class.java
    ) {
        override fun createInstance(entityId: EntityID<UUID>, row: ResultRow?): Profile {
            return Profile(entityId)
        }
    }
}




// @Serializable
// class MessengerInfo(val messenger: Messenger, val username: String)

// @Serializable
// enum class Messenger {
//     SLACK, TELEGRAM, TWITTER, SKYPE
// }

@Serializable
class Phone(val number: String)

object Profiles : UUIDTable() {

    val personalData = PersonalData
    val contactDetails = ContactDetails
    val userId = uuid("user_id")

    object PersonalData : EmbeddableColumn<Profile.PersonalData>() {
        val birthday = date("birthday").nullable()
        val gender = customEnumeration(
            "gender",
            "profile_gender",
            { value -> Gender.valueOf(value as String) },
            { PGEnum("profile_gender", it) }).nullable()
        val about = text("about").nullable()
        // val name = Name

        // object Name : EmbeddableColumn<Profile.PersonalData.Name>() {
        //     val firstName = varchar("first_name", 25).nullable()
        //     val lastName = varchar("last_name", 25).nullable()
        // }
    }

    object ContactDetails : EmbeddableColumn<Profile.ContactDetails>() {
        val phones = jsonb("phones", Phone.serializer().set)
    }

    enum class Gender {
        MALE, FEMALE
    }
}
