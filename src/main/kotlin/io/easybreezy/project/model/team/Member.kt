package io.easybreezy.project.model.team

import io.easybreezy.infrastructure.exposed.dao.PrivateEntityClass
import io.easybreezy.infrastructure.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

class Member private constructor(id: EntityID<UUID>) : Entity<UUID>(id) {
    private var user by Members.user
    private var role by Role referencedOn Members.role
    private var team by Members.team

    companion object : PrivateEntityClass<UUID, Member>(object : Repository() {}) {
        fun create(team: Team, user: UUID, role: Role): Member {
            return Member.new {
                this.user = user
                this.team = team.id
                this.role = role
            }
        }
    }

    abstract class Repository : EntityClass<UUID, Member>(Members, Member::class.java) {
        override fun createInstance(entityId: EntityID<UUID>, row: ResultRow?): Member {
            return Member(entityId)
        }
    }
}

object Members : UUIDTable("project_members") {
    val team = reference("team", Teams)
    val user = uuid("user_id")
    val role = reference("role", Roles)
}
