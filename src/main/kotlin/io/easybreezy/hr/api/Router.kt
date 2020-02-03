package io.easybreezy.hr.api

import com.google.inject.Inject
import io.easybreezy.hr.api.controller.AbsenceController
import io.easybreezy.hr.api.controller.LocationController
import io.easybreezy.hr.api.controller.ProfileController
import io.easybreezy.infrastructure.ktor.GenericPipeline
import io.easybreezy.infrastructure.ktor.Router
import io.easybreezy.infrastructure.ktor.auth.Auth
import io.easybreezy.infrastructure.ktor.auth.UserPrincipal
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.request.receive
import java.util.UUID
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

class Router @Inject constructor(
    application: Application,
    genericPipeline: GenericPipeline
) : Router(application, genericPipeline) {

    init {
        application.routing {
            route("/api/hr") {
                authenticate(*Auth.user) {
                    profileRouting(this)
                    absencesRouting(this)
                    locationsRouting(this)
                    hrRouting(this)
                }
            }
        }
    }

    private fun profileRouting(route: Route) {
        route.route("/profile") {
            get("") {
                controller<ProfileController>(this).show(
                    resolveUserId<UserPrincipal>()
                )
            }
            post("/personal-data") {
                controller<ProfileController>(this).updatePersonalData(
                    resolveUserId<UserPrincipal>(),
                    call.receive()
                )
            }
            post("/add-messengers") {
                controller<ProfileController>(this).updateMessengers(
                    resolveUserId<UserPrincipal>(),
                    call.receive()
                )
            }
            post("/contact-details") {
                controller<ProfileController>(this).updateContactDetails(
                    resolveUserId<UserPrincipal>(),
                    call.receive()
                )
            }
        }
    }

    private fun hrRouting(route: Route) {

    }


    private fun absencesRouting(route: Route) {
        route.route("/absences") {
            @Location("/{id}")
            data class Absence(val id: UUID)

            @Location("/{id}")
            data class WorkingHour(val id: UUID)

            post("") { controller<AbsenceController>(this).createAbsence(call.receive()) }
            post<Absence> { controller<AbsenceController>(this).updateAbsence(it.id, call.receive()) }
            delete<Absence> { controller<AbsenceController>(this).removeAbsence(it.id) }
            get<Absence> { controller<AbsenceController>(this).showAbsence(it.id) }
            get("") { controller<AbsenceController>(this).absences(resolveUserId<UserPrincipal>()) }

            route("/working-hours") {
                post("") { controller<AbsenceController>(this).noteWorkingHours(call.receive()) }
                put("") { controller<AbsenceController>(this).editWorkingHours(call.receive()) }
                delete("") { controller<AbsenceController>(this).removeWorkingHours(call.receive()) }
                get<WorkingHour> { controller<AbsenceController>(this).showWorkingHour(it.id) }
                get("") { controller<AbsenceController>(this).workingHours(resolveUserId<UserPrincipal>()) }
            }
        }
    }

    private fun locationsRouting(route: Route) {
        route.route("/locations") {
            @Location("/{id}")
            data class Locations(val id: UUID)

            @Location("/{id}")
            data class UserLocation(val id: UUID)

            post("") { controller<LocationController>(this).createLocation(call.receive()) }
            delete<Locations> { controller<LocationController>(this).removeLocation(it.id) }
            get("") { controller<LocationController>(this).locations() }

            route("/user") {
                post("") { controller<LocationController>(this).assignLocation(resolveUserId<UserPrincipal>(), call.receive()) }
                post<UserLocation> { controller<LocationController>(this).editUserLocation(it.id, call.receive()) }
                delete<UserLocation> { controller<LocationController>(this).removeUserLocation(it.id) }
                get<UserLocation> { controller<LocationController>(this).showUserLocation(it.id) }
                get("") { controller<LocationController>(this).userLocations(resolveUserId<UserPrincipal>()) }
            }
        }
    }
}
