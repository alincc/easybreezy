package io.easybreezy.hr.api

import com.google.inject.Inject
import io.easybreezy.hr.api.controller.AbsenceController
import io.easybreezy.hr.api.controller.CalendarController
import io.easybreezy.hr.api.controller.EmployeeController
import io.easybreezy.hr.api.controller.EventController
import io.easybreezy.hr.api.controller.LocationController
import io.easybreezy.hr.api.controller.VacationController
import io.easybreezy.hr.application.RemainingTime
import io.easybreezy.hr.application.RemainingTimes
import io.easybreezy.hr.application.absence.CreateAbsence
import io.easybreezy.hr.application.absence.UpdateAbsence
import io.easybreezy.hr.application.absence.queryobject.Absence
import io.easybreezy.hr.application.absence.queryobject.Absences
import io.easybreezy.hr.application.absence.queryobject.IsAbsenceOwner
import io.easybreezy.hr.application.absence.queryobject.UserAbsences
import io.easybreezy.hr.application.calendar.command.AddHoliday
import io.easybreezy.hr.application.calendar.command.EditCalendar
import io.easybreezy.hr.application.calendar.command.EditHoliday
import io.easybreezy.hr.application.calendar.command.ImportCalendar
import io.easybreezy.hr.application.calendar.command.RemoveHoliday
import io.easybreezy.hr.application.calendar.queryobject.Calendars
import io.easybreezy.hr.application.calendar.queryobject.Holidays
import io.easybreezy.hr.application.event.command.AddParticipants
import io.easybreezy.hr.application.event.command.ChangeConditions
import io.easybreezy.hr.application.event.command.ChangeVisitStatus
import io.easybreezy.hr.application.event.command.Enter
import io.easybreezy.hr.application.event.command.Open
import io.easybreezy.hr.application.event.command.UpdateDetails
import io.easybreezy.hr.application.event.queryobject.CanViewEvent
import io.easybreezy.hr.application.event.queryobject.Event
import io.easybreezy.hr.application.event.queryobject.IsEventOwner
import io.easybreezy.hr.application.hr.command.ApplyPosition
import io.easybreezy.hr.application.hr.command.ApplySalary
import io.easybreezy.hr.application.hr.command.Fire
import io.easybreezy.hr.application.hr.command.Hire
import io.easybreezy.hr.application.hr.command.SpecifySkills
import io.easybreezy.hr.application.hr.command.UpdateBio
import io.easybreezy.hr.application.hr.command.UpdateBirthday
import io.easybreezy.hr.application.hr.command.WriteNote
import io.easybreezy.hr.application.hr.queryobject.Employee
import io.easybreezy.hr.application.hr.queryobject.EmployeeDetails
import io.easybreezy.hr.application.location.AssignLocation
import io.easybreezy.hr.application.location.CreateLocation
import io.easybreezy.hr.application.location.EditUserLocation
import io.easybreezy.hr.application.location.queryobject.IsUserLocationOwner
import io.easybreezy.hr.application.location.queryobject.Locations
import io.easybreezy.hr.application.location.queryobject.UserLocation
import io.easybreezy.hr.application.location.queryobject.UserLocations
import io.easybreezy.hr.application.location.queryobject.UsersLocations
import io.easybreezy.infrastructure.ktor.GenericPipeline
import io.easybreezy.infrastructure.ktor.Response
import io.easybreezy.infrastructure.ktor.Router
import io.easybreezy.infrastructure.ktor.auth.Activity
import io.easybreezy.infrastructure.ktor.auth.Auth
import io.easybreezy.infrastructure.ktor.auth.UserPrincipal
import io.easybreezy.infrastructure.ktor.auth.authorize
import io.easybreezy.integration.openapi.ktor.delete
import io.easybreezy.integration.openapi.ktor.deleteWithBody
import io.easybreezy.integration.openapi.ktor.get
import io.easybreezy.integration.openapi.ktor.post
import io.easybreezy.integration.openapi.ktor.postParams
import io.easybreezy.infrastructure.query.QueryExecutor
import io.ktor.application.Application
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.locations.locations
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import java.util.UUID

class Router @Inject constructor(
    application: Application,
    genericPipeline: GenericPipeline,
    private val queryExecutor: QueryExecutor
) : Router(application, genericPipeline) {

    init {
        application.routing {
            route("/api/hr") {
                authenticate(*Auth.user) {
                    absencesRouting()
                    locationsRouting()
                    employeeRouting()
                    calendarsRouting()
                    eventsRouting()
                    vacationRouting()
                }
            }
        }
    }

    private fun Route.absencesRouting() {
        route("/absences") {
            authorize(setOf(Activity.ABSENCES_MANAGE), {
                val principal = principal<UserPrincipal>()
                principal!!.id == receive<CreateAbsence>().userId
            }
            ) {
                post<Response.Either<Response.Ok, Response.Errors>, CreateAbsence>("") { command ->
                    controller<AbsenceController>(this).createAbsence(
                        command
                    )
                }
            }

            authorize(setOf(Activity.ABSENCES_MANAGE), {
                val principal = principal<UserPrincipal>()
                val absenceId = locations.resolve<ID>(this).id
                queryExecutor.execute(IsAbsenceOwner(absenceId, principal!!.id))
            }
            ) {
                post<Response.Either<Response.Ok, Response.Errors>, UpdateAbsence, ID>("/{id}") {
                        command, params ->
                    controller<AbsenceController>(this).updateAbsence(
                        params.id,
                        command
                    )
                }
            }

            authorize(setOf(Activity.ABSENCES_MANAGE)) {
                postParams<Response.Ok, ID>("/{id}/approve") { params ->
                    controller<AbsenceController>(this).approveAbsence(
                        params.id
                    )
                }
                delete<Response.Ok, ID>("/{id}") { params ->
                    controller<AbsenceController>(this).removeAbsence(
                        params.id
                    )
                }
            }

            authorize(setOf(Activity.ABSENCES_LIST, Activity.ABSENCES_MANAGE)) {
                get<Response.Data<Absences>>("") { controller<AbsenceController>(this).absences() }
            }

            get<Response.Data<Absence>, ID>("/{id}") { params ->
                controller<AbsenceController>(this).showAbsence(
                    params.id
                )
            }
            get<Response.Data<UserAbsences>>("/me") {
                controller<AbsenceController>(this).userAbsences(resolvePrincipal<UserPrincipal>())
            }
        }

        route("/user") {
            authorize(setOf(Activity.ABSENCES_LIST, Activity.ABSENCES_MANAGE), {
                val principal = principal<UserPrincipal>()
                principal?.id == locations.resolve<UserId>(this).userId
            }) {
                get<Response.Data<UserAbsences>, UserId>("/{userId}/absences") { params ->
                    controller<AbsenceController>(this).userAbsences(params.userId)
                }
            }
        }
    }

    private fun Route.locationsRouting() {
        route("/locations") {
            authorize(setOf(Activity.LOCATIONS_MANAGE)) {
                post<Response.Either<Response.Ok, Response.Errors>, CreateLocation>("") { command ->
                    controller<LocationController>(this).createLocation(command)
                }
                delete<Response.Ok, ID>("/{id}") { params ->
                    controller<LocationController>(this).removeLocation(params.id)
                }
            }
            authorize(setOf(Activity.LOCATIONS_LIST)) {
                get<Response.Data<Locations>>("") {
                    controller<LocationController>(this).locations()
                }
            }
        }

        route("/user-locations") {
            authorize(setOf(Activity.USER_LOCATIONS_MANAGE), {
                val principal = principal<UserPrincipal>()
                principal!!.id == receive<AssignLocation>().userId
            }) {
                post<Response.Either<Response.Ok, Response.Errors>, AssignLocation>("") { command ->
                    controller<LocationController>(this).assignLocation(
                        command
                    )
                }
            }

            authorize(setOf(Activity.USER_LOCATIONS_MANAGE), {
                val principal = principal<UserPrincipal>()
                val userLocationId = locations.resolve<ID>(this).id
                queryExecutor.execute(IsUserLocationOwner(userLocationId, principal!!.id))
            }) {
                post<Response.Either<Response.Ok, Response.Errors>, EditUserLocation, ID>("/{id}") {
                        command, params ->
                    controller<LocationController>(this).editUserLocation(
                        params.id,
                        command
                    )
                }
            }

            authorize(setOf(Activity.USER_LOCATIONS_LIST)) {
                get<Response.Data<UsersLocations>>("") {
                    controller<LocationController>(this).usersLocations()
                }
            }

            get<Response.Data<UserLocation>, ID>("/{id}") { params ->
                controller<LocationController>(this).showUserLocation(params.id)
            }
        }

        route("/user") {
            authorize(setOf(Activity.USER_LOCATIONS_LIST), {
                val principal = principal<UserPrincipal>()
                principal?.id == locations.resolve<UserId>(this).userId
            }) {
                get<Response.Data<UserLocations>, UserId>("/{userId}/locations") { params ->
                    controller<LocationController>(this).userLocations(params.userId)
                }
            }
        }
    }

    private fun Route.employeeRouting() {
        route("/employees") {
            authorize(setOf(Activity.EMPLOYEES_LIST)) {
                get<Response.Listing<Employee>>("") {
                    controller<EmployeeController>(this).employees()
                }
            }
        }

        route("/employee/{userId}") {
            authorize(setOf(Activity.EMPLOYEES_MANAGE)) {
                post<Response.Either<Response.Ok, Response.Errors>, ApplyPosition, UserId>("/apply-position") {
                        command, params ->
                    controller<EmployeeController>(this).applyPosition(
                        command,
                        params.userId,
                        resolvePrincipal<UserPrincipal>()
                    )
                }
                post<Response.Either<Response.Ok, Response.Errors>, ApplySalary, UserId>("/apply-salary") {
                        command, params ->
                    controller<EmployeeController>(this).applySalary(
                        command,
                        params.userId,
                        resolvePrincipal<UserPrincipal>()
                    )
                }
                post<Response.Either<Response.Ok, Response.Errors>, Hire, UserId>("/hire") { command, params ->
                    controller<EmployeeController>(this).hire(command, params.userId, resolvePrincipal<UserPrincipal>())
                }
                post<Response.Either<Response.Ok, Response.Errors>, Fire, UserId>("/fire") { command, params ->
                    controller<EmployeeController>(this).fire(command, params.userId, resolvePrincipal<UserPrincipal>())
                }
                post<Response.Either<Response.Ok, Response.Errors>, WriteNote, UserId>("/write-note") {
                        command, params ->
                    controller<EmployeeController>(this).writeNote(
                        command,
                        params.userId,
                        resolvePrincipal<UserPrincipal>()
                    )
                }
            }

            authorize(setOf(Activity.EMPLOYEES_MANAGE), {
                val principal = principal<UserPrincipal>()
                principal!!.id == locations.resolve<UserId>(this).userId
            }) {
                post<Response.Either<Response.Ok, Response.Errors>, SpecifySkills, UserId>("/specify-skills") {
                        command, params ->
                    controller<EmployeeController>(this).specifySkills(command, params.userId)
                }
                post<Response.Either<Response.Ok, Response.Errors>, UpdateBio, UserId>("/update-bio") { command, params ->
                    controller<EmployeeController>(this).updateBio(command, params.userId)
                }
                post<Response.Either<Response.Ok, Response.Errors>, UpdateBirthday, UserId>("/update-birthday") {
                        command, params ->
                    controller<EmployeeController>(this).updateBirthday(command, params.userId)
                }
            }

            get<Response.Data<EmployeeDetails>, UserId>("") { params ->
                controller<EmployeeController>(this).employee(params.userId)
            }
        }
    }

    private fun Route.calendarsRouting() {
        route("/calendars") {
            holidaysRouting()
            authorize(setOf(Activity.CALENDARS_MANAGE)) {
                post<Response.Either<Response.Ok, Response.Errors>, ImportCalendar>("") { command ->
                    controller<CalendarController>(this).importCalendar(command)
                }
                post<Response.Either<Response.Ok, Response.Errors>, EditCalendar, ID>("/{id}") { command, params ->
                    controller<CalendarController>(this).editCalendar(params.id, command)
                }
                delete<Response.Ok, ID>("/{id}") { params ->
                    controller<CalendarController>(this).removeCalendar(
                        params.id
                    )
                }
            }

            authorize(setOf(Activity.CALENDARS_LIST)) {
                get<Response.Data<Calendars>>("") {
                    controller<CalendarController>(this).calendars()
                }
            }
        }
    }

    private fun Route.holidaysRouting() {
        route("/holidays") {
            authorize(setOf(Activity.HOLIDAYS_MANAGE)) {
                post<Response.Either<Response.Ok, Response.Errors>, AddHoliday>("") { command ->
                    controller<CalendarController>(this).addHoliday(command)
                }

                post<Response.Either<Response.Ok, Response.Errors>, EditHoliday, ID>("/{id}") { command, params ->
                    controller<CalendarController>(this).editHoliday(params.id, command)
                }

                deleteWithBody<Response.Either<Response.Ok, Response.Errors>, RemoveHoliday>("") { command ->
                    controller<CalendarController>(this).removeHoliday(command)
                }
            }

            get<Response.Data<Holidays>, ID>("/{id}") { params ->
                controller<CalendarController>(this).holidays(params.id)
            }
        }
    }

    private fun Route.eventsRouting() {
        route("/events") {
            authorize(setOf(Activity.EVENTS_MANAGE)) {
                post<Response.Either<Response.Ok, Response.Errors>, Open>("") { command ->
                    controller<EventController>(this).openEvent(resolvePrincipal<UserPrincipal>(), command)
                }
            }

            route("/{id}") {
                authorize(setOf(Activity.EVENTS_MANAGE), {
                    val principal = principal<UserPrincipal>()
                    val event = locations.resolve<ID>(this).id
                    queryExecutor.execute(IsEventOwner(event, principal!!.id))
                }) {
                    post<Response.Either<Response.Ok, Response.Errors>, UpdateDetails, ID>("") { command, params ->
                        controller<EventController>(this).updateEventDetails(params.id, command)
                    }
                    post<Response.Either<Response.Ok, Response.Errors>, AddParticipants, ID>("/participants") {
                            command, params ->
                        controller<EventController>(this).addParticipants(params.id, command)
                    }
                    postParams<Response.Ok, ID>("/cancel") { params ->
                        controller<EventController>(this).cancelEvent(params.id)
                    }
                    post<Response.Either<Response.Ok, Response.Errors>, ChangeConditions, ID>("/conditions") {
                            command, params ->
                        controller<EventController>(this).updateEventConditions(params.id, command)
                    }
                }
                authorize(setOf(Activity.EVENTS_LIST), {
                    val id = principal<UserPrincipal>()!!.id
                    val event = locations.resolve<ID>(this).id
                    queryExecutor.execute(CanViewEvent(event, id))
                }) {
                    get<Response.Data<Event>, ID>("") { params ->
                        controller<EventController>(this).event(params.id)
                    }
                }
                route("/participants") {
                    authorize(setOf(Activity.EVENTS_MANAGE), {
                        val principal = principal<UserPrincipal>()
                        principal!!.id == receive<Enter>().employeeId
                    }) {
                        post<Response.Either<Response.Ok, Response.Errors>, Enter, ID>("/enter") { command, params ->
                            controller<EventController>(this).enterEvent(params.id, command)
                        }
                    }
                    authorize(setOf(Activity.EVENTS_MANAGE), {
                        val principal = principal<UserPrincipal>()
                        principal!!.id == receive<ChangeVisitStatus>().employeeId
                    }) {
                        post<Response.Either<Response.Ok, Response.Errors>, ChangeVisitStatus, ID>("/visit-status") { command, params ->
                            controller<EventController>(this).changeVisitStatus(params.id, command)
                        }
                    }
                }
            }
            authorize(setOf(Activity.EVENTS_LIST)) {
                get<Response.Listing<Event>>("") {
                    controller<EventController>(this).events(resolvePrincipal<UserPrincipal>())
                }
            }
        }
    }

    private fun Route.vacationRouting() {
        route("/vacations") {
            authorize(setOf(Activity.VACATIONS_SHOW), {
                val principal = principal<UserPrincipal>()
                principal!!.id == locations.resolve<UserId>(this).userId
            }) {
                get<Response.Data<RemainingTime>, UserId>("/{userId}") { params ->
                    controller<VacationController>(this).calculateVacation(params.userId)
                }
            }

            authorize(setOf(Activity.VACATIONS_SHOW)) {
                get<Response.Data<RemainingTimes>>("") {
                    controller<VacationController>(this).calculateVacations()
                }
            }
        }
    }

    internal data class ID(val id: UUID)
    internal data class UserId(val userId: UUID)
}
