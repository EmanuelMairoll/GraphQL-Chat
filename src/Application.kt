import com.apurebase.kgraphql.GraphqlRequest
import com.apurebase.kgraphql.KtorGraphQLConfiguration
import com.apurebase.kgraphql.context
import com.emanuelmairoll.AuthService
import com.emanuelmairoll.schema
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) { header(HttpHeaders.Server, "My Demo Server") }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val authService = AuthService()

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        route("/graphql") {
            post {
                val string = call.receiveText()
                val request = Json.decodeFromString(GraphqlRequest.serializer(), string)
                request.operationName
                val ctx = context {
                    authService.userAccessForToken(call.request.headers["AccessToken"])?.let { +it }
                }
                val result = schema.execute(request.query, request.variables.toString(), ctx)
                call.respond(result)
            }
            get {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                val playgroundHtml =
                    KtorGraphQLConfiguration::class.java.classLoader.getResource("playground.html").readBytes()
                call.respondBytes(playgroundHtml, contentType = ContentType.Text.Html)
            }
        }

        /**
         * Sadly, due to the poor implementation as well as documentation of subscriptions in kgraphql, i had to abandon my attempts implementing the notification system
         */
        /*
        webSocket("/graphql") {
            val init = incoming.receive()
            val subscription = incoming.receive()

            if (init is Frame.Text && subscription is Frame.Text ) {
                val string = subscription.readText()
                val request = Json.decodeFromString(GraphqlRequest.serializer(), string)
                val ctx = context {
                    authService.userAccessForToken(call.request.headers["AccessToken"])?.let { +it }
                }

                val publisher: Publisher = schema.execute(request.query, request.variables.toString(), ctx)
                publisher.subscribe(UUID.randomUUID().toString(), object : Subscriber {
                    override fun onNext(item: String) {
                        outgoing.send(Frame.Text(item))
                    }
                })
            }
        }
         */
    }
}

