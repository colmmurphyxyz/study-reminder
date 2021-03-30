import com.beust.klaxon.Klaxon
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import org.json.JSONObject
import java.io.File
import javax.security.auth.login.LoginException

object Bot {
    lateinit var jda : JDA
    val waiter = EventWaiter()
    lateinit var LCGuild : Guild
    val quoteBank = ArrayList<JSONObject>()
    @Throws(LoginException::class, InterruptedException::class)
    fun enable() {
        jda = JDABuilder.createLight(
            getToken(),
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES
        )
            .addEventListeners(waiter)
            .build().awaitReady()
        LCGuild = jda.getGuildById("750706717796466779")!!
        jda.addEventListener(MListener())

        val quotes = khttp.get("https://type.fit/api/quotes").jsonArray
        for (i in quotes) quoteBank.add(i as JSONObject)
    }
}

private fun getToken(): String = File("token.txt").readLines()[0]