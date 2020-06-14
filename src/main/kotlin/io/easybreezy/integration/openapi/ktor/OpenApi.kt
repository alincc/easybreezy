package io.easybreezy.integration.openapi.ktor

import io.easybreezy.integration.openapi.ClassTypeDescription
import io.easybreezy.integration.openapi.OpenAPI
import io.easybreezy.integration.openapi.OpenApiKType
import io.easybreezy.integration.openapi.Type
import io.easybreezy.integration.openapi.getOpenApiKType
import io.easybreezy.integration.openapi.spec.Root
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

class OpenApi(configuration: Configuration) {
    private val typeBuilder: (OpenApiKType) -> Type.Object = configuration.typeBuilder
    private val responseBuilder: (OpenApiKType) -> Map<Int, Type> = configuration.responseBuilder
    private val openApi: OpenAPI = configuration.openApi
    private val path: String = configuration.path
    private val descriptions: MutableMap<String, ClassTypeDescription> = mutableMapOf()

    class Configuration {
        var typeBuilder: (OpenApiKType) -> Type.Object = { type -> type.objectType("response") }
        var responseBuilder: (OpenApiKType) -> Map<Int, Type> = { type -> mapOf(200 to type.type()) }
        var path = "/openapi.json"
        var configure: (OpenApi) -> Unit = {}
        var openApi: OpenAPI = OpenAPI("localhost")
    }

    fun addClassTypeDescription(clazz: KClass<*>, description: ClassTypeDescription) {
        descriptions[clazz.qualifiedName!!] = description
    }

    fun addPath(
        path: String,
        method: HttpMethod,
        response: KType,
        body: KType? = null,
        pathParams: KType? = null
    ) {
        openApi.addPath(
            path,
            OpenAPI.Method.valueOf(method.value),
            responseBuilder(response.getOpenApiKType(this.descriptions)),
            body?.getOpenApiKType(this.descriptions)?.let(typeBuilder),
            pathParams?.getOpenApiKType(this.descriptions)?.let(typeBuilder)
        )
    }

    suspend fun intercept(
        context: PipelineContext<Unit, ApplicationCall>
    ) {
        if (context.call.request.path() == path) {
            val response = Json(
                JsonConfiguration.Default.copy(
                    encodeDefaults = false
                )
            ).stringify(
                Root::class.serializer(), openApi.root
            )
            context.call.response.status(HttpStatusCode.OK)

            context.call.respondText(response, contentType = ContentType.Application.Json)
            context.finish()
        }
    }

    /**
     * Installable feature for [OpenApi].
     */
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, OpenApi> {
        override val key = AttributeKey<OpenApi>("OpenApi")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): OpenApi {
            val configuration = Configuration().apply(configure)
            val feature = OpenApi(configuration)
            configuration.configure(feature)
            pipeline.intercept(ApplicationCallPipeline.Fallback) {
                feature.intercept(this)
            }
            return feature
        }
    }
}
