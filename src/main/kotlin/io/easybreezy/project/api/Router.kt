package io.easybreezy.project.api

import com.google.inject.Inject
import io.easybreezy.infrastructure.ktor.GenericPipeline
import io.easybreezy.infrastructure.ktor.Response
import io.easybreezy.infrastructure.ktor.Router
import io.easybreezy.infrastructure.ktor.auth.Auth
import io.easybreezy.infrastructure.ktor.auth.UserPrincipal
import io.easybreezy.infrastructure.ktor.get
import io.easybreezy.infrastructure.ktor.post
import io.easybreezy.infrastructure.ktor.postParams
import io.easybreezy.project.api.controller.ProjectController
import io.easybreezy.project.api.controller.TeamController
import io.easybreezy.project.application.project.command.ChangeCategory
import io.easybreezy.project.application.project.command.ChangeRole
import io.easybreezy.project.application.project.command.ChangeSlug
import io.easybreezy.project.application.project.command.New
import io.easybreezy.project.application.project.command.NewCategory
import io.easybreezy.project.application.project.command.NewRole
import io.easybreezy.project.application.project.command.RemoveCategory
import io.easybreezy.project.application.project.command.RemoveRole
import io.easybreezy.project.application.project.command.WriteDescription
import io.easybreezy.project.application.project.queryobject.Project
import io.easybreezy.project.application.team.command.ActivateTeam
import io.easybreezy.project.application.team.command.ChangeMemberRole
import io.easybreezy.project.application.team.command.CloseTeam
import io.easybreezy.project.application.team.command.NewMember
import io.easybreezy.project.application.team.command.NewTeam
import io.easybreezy.project.application.team.command.RemoveMember
import io.easybreezy.project.model.team.Role
import io.ktor.application.Application
import io.ktor.auth.authenticate
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.Serializable
import java.util.UUID

class Router @Inject constructor(
    application: Application,
    genericPipeline: GenericPipeline
) : Router(application, genericPipeline) {

    init {
        application.routing {
            authenticate(*Auth.user) {
                route("/api/projects") {
                    projectRoutes()
                }
                route("/api/teams") {
                    // authorize(setOf(Activity.MEMBER)) {
                    teamRoutes()
                    // }
                }
            }
        }
    }

    private fun Route.teamRoutes() {
        route("") {
            data class Team(val teamId: UUID)
            post<Response.Either<Response.Ok, Response.Errors>, NewTeam>("/add") { command ->
                controller<TeamController>(this).newTeam(command)
            }
            get<Response.Data<io.easybreezy.project.application.team.queryobject.Team>, Team>("/{teamId}") { params ->
                controller<TeamController>(this).show(params.teamId)
            }
            post<Response.Either<Response.Ok, Response.Errors>, NewMember, Team>("/{teamId}/members/add") { command, params ->
                command.team = params.teamId
                controller<TeamController>(this).newMember(command)
            }

            post<Response.Ok, ActivateTeam, Team>("/{teamId}/activate") { _, params ->
                controller<TeamController>(this).activate(params.teamId)
            }

            post<Response.Ok, CloseTeam, Team>("/{teamId}/close") { _, params ->
                controller<TeamController>(this).close(params.teamId)
            }

            data class TeamMember(val teamId: UUID, val memberId: UUID)
            post<Response.Either<Response.Ok, Response.Errors>, RemoveMember, TeamMember>("/{teamId}/members/{memberId}/remove") { command, params ->
                command.memberId = params.memberId
                command.team = params.teamId
                controller<TeamController>(this).removeMember(command)
            }

            post<Response.Either<Response.Ok, Response.Errors>, ChangeMemberRole, TeamMember>("/{teamId}/members/{memberId}/change-role") { command, params ->
                command.team = params.teamId
                command.memberId = params.memberId
                controller<TeamController>(this).changeMemberRole(command)
            }
        }
    }

    private fun Route.projectRoutes() {
        route("") {
            @Serializable
            data class NewRequest(val name: String, val description: String, val slug: String? = null) {
                fun makeCommand(principal: UUID): New {
                    return New(principal, name, description, slug)
                }
            }
            post<Response.Either<Response.Ok, Response.Errors>, NewRequest>("") { request ->
                controller<ProjectController>(this).create(request.makeCommand(resolvePrincipal<UserPrincipal>()))
            }
            get<Response.Listing<Project>>("") {
                controller<ProjectController>(this).list()
            }
            get<Response.Data<List<Role.Permission>>>("/permissions") {
                controller<ProjectController>(this).permissions()
            }

        }
        route("/{slug}") {
            @Serializable
            data class SlugParam(val slug: String)
            get<Response.Data<Project>, SlugParam>("") { params ->
                controller<ProjectController>(this).show(params.slug)
            }

            post<Response.Either<Response.Ok, Response.Errors>, SlugParam, SlugParam>("/change-slug") { new, params ->
                controller<ProjectController>(this).changeSlug(ChangeSlug(params.slug, new.slug))
            }

            postParams<Response.Ok, SlugParam>("/activate") { params ->
                controller<ProjectController>(this).activate(params.slug)
            }
            postParams<Response.Ok, SlugParam>("/suspend") { params ->
                controller<ProjectController>(this).suspendProject(params.slug)
            }
            postParams<Response.Ok, SlugParam>("/close") { params ->
                controller<ProjectController>(this).close(params.slug)
            }
            post<Response.Either<Response.Ok, Response.Errors>, WriteDescription, SlugParam>("/write-description") { command, params ->
                command.project = params.slug
                controller<ProjectController>(this).writeDescription(command)
            }

            data class ProjectRole(val slug: String, val roleId: UUID)
            post<Response.Either<Response.Ok, Response.Errors>, NewRole, SlugParam>("/roles/add") { command, params ->
                command.project = params.slug
                controller<ProjectController>(this).addRole(command)
            }
            post<Response.Either<Response.Ok, Response.Errors>, ChangeRole, ProjectRole>("/roles/{roleId}/change") { command, params ->
                command.project = params.slug
                command.roleId = params.roleId
                controller<ProjectController>(this).changeRole(command)
            }
            post<Response.Either<Response.Ok, Response.Errors>, RemoveRole, ProjectRole>("/roles/{roleId}/remove") { command, params ->
                command.roleId = params.roleId
                command.project = params.slug
                controller<ProjectController>(this).removeRole(command)
            }

            data class ProjectCategory(val slug: String, val categoryId: UUID)
            post<Response.Either<Response.Ok, Response.Errors>, NewCategory, SlugParam>("/categories/add") { command, params ->
                command.project = params.slug
                controller<ProjectController>(this).addCategory(command)
            }
            post<Response.Either<Response.Ok, Response.Errors>, ChangeCategory, ProjectCategory>("/categories/{categoryId}/change") { command, params ->
                command.project = params.slug
                command.categoryId = params.categoryId
                controller<ProjectController>(this).changeCategory(command)
            }
            post<Response.Either<Response.Ok, Response.Errors>, RemoveCategory, ProjectCategory>("/categories/{categoryId}/remove") { command, params ->
                command.categoryId = params.categoryId
                command.project = params.slug
                controller<ProjectController>(this).removeCategory(command)
            }
        }
    }
}
