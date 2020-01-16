package io.easybreezy.project.model.team

import io.easybreezy.infrastructure.exposed.dao.AggregateRoot
import io.easybreezy.infrastructure.exposed.dao.PrivateEntityClass
import io.easybreezy.project.model.Project
import io.easybreezy.project.model.Projects
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.Table
import java.util.*

class Team private constructor(id: EntityID<UUID>) : AggregateRoot<UUID>(id) {
    private var name by Teams.name
    //    private val repositories by Repository referrersOn Teams.repositories
    private var members by Member via Members

    companion object : PrivateEntityClass<UUID, Team>(object : Repository() {}) {
        fun create(name: String): Team {
            return Team.new {
                this.name = name
            }
        }
    }

    fun addMember(member: UUID, info: Member.Info, role: Role) {
        members = SizedCollection(members + Member.create(this, member, role, info))
    }

    fun changeMemberRole(member: UUID, role: Role) {

    }

    fun removeMember(member: UUID) {

    }

    fun addRepository(url: String, type: io.easybreezy.project.model.team.Repository.Type) {

    }

    fun removeRepository(url: String) {

    }


    abstract class Repository : EntityClass<UUID, Team>(Teams, Team::class.java) {
        override fun createInstance(entityId: EntityID<UUID>, row: ResultRow?): Team {
            return Team(entityId)
        }
    }

}

enum class Status {
    ACTIVE,
    CLOSED
}

class Repository : UUIDEntity(EntityID(UUID.randomUUID(), Repositories)) {
    companion object : PrivateEntityClass<UUID, Repository>(object : UUIDEntityClass<Repository>(Repositories) {})
    enum class Type {
        GITLAB,
        GITHUB
    }
}

object Teams : UUIDTable() {
    val name = varchar("name", 25)
    val status = enumerationByName("status", 25, Status::class).default(Status.ACTIVE)
}

object Repositories : UUIDTable() {

}