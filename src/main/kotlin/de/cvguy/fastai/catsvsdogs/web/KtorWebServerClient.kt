package de.cvguy.fastai.catsvsdogs.web

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.Template
import de.cvguy.fastai.catsvsdogs.game.CatsVsDogsGame
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.PartData
import org.jetbrains.ktor.request.isMultipart
import org.jetbrains.ktor.request.receiveMultipart
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import java.io.InputStream

/**
 * Web interface to the de.cvguy.fastai.catsvsdogs.game.
 */
class KtorWebServerClient(
        game: CatsVsDogsGame,
        port: Int = 8080
) {
    // make it easier to define handlebar helpers (that do not need parameter)
    fun <T> Handlebars.registerSimpleHelper(name: String, f: (context: T) -> String) {
        registerHelper(name, object : Helper<T> {
            override fun apply(context: T?, options: Options?) = context?.let { f(it) }
        })
    }

    val handlebars = Handlebars().also {
        it.registerSimpleHelper<Int>("counter", { "${it + 1}" })
        it.registerSimpleHelper<Double>("percent", { "%.2f".format(it * 100) + "%" })
    }

    val templateCache = hashMapOf<String, Template>()

    private fun _render(template: String, context: Any? = null): String {
        return templateCache.getOrPut(template, { handlebars.compile(template) }).apply(context)
    }

    val server = embeddedServer(Netty, port) {
        routing {
            get("/") {
                call.respondText(_render("index.html", game), ContentType.Text.Html)
            }

            post("/entry") {
                var error = false

                if (call.request.isMultipart()) {
                    val parts = call.receiveMultipart().parts
                    val image: InputStream? = (parts.findLast { it.partName == "image" } as? PartData.FileItem)?.streamProvider?.invoke()
                    val name: String? = (parts.findLast { it.partName == "name" } as? PartData.FormItem)?.value

                    if(image != null && image.available() > 0 && name != null) {
                        game.addEntry(name, image)
                    }
                    else {
                        error = true
                    }

                    parts.forEach { it.dispose() }
                }

                if(error) {
                    call.respondText("Invalid Input", ContentType.Text.Html, HttpStatusCode.BadRequest)
                }
                else {
                    call.respondRedirect("/")
                }
            }

            get("/reset") {
                game.scoreBoard.reset()
                call.respondRedirect("/")
            }
        }
    }

    fun serve() = server.start(wait = true)
}