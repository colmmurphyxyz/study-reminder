import com.beust.klaxon.Klaxon
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.lang.Math.round
import java.lang.NullPointerException
import kotlin.math.roundToInt

class MessageListener : ListenerAdapter() {
    private val pomoBotId = "674238793431384067"
    private val genCategoryId = "750706717796466781"
    private val studyRole : Role = Bot.LCGuild.getRoleById("823202557630873630")!!
    var inStudyMode = true
    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        if (e.message.author.id == pomoBotId) {
            if (e.message.contentRaw.contains("**Break** finished!")) {
                inStudyMode = true
                println("entered study mode")
            } else if (e.message.contentRaw.contains("**Study** finished!")) {
                inStudyMode = false
                println("entered break mode")
            }
            return
        }
        try {
            if (e.member!!.roles.contains(studyRole) && e.message.category!!.id.equals(genCategoryId)
                && inStudyMode && !listOf<Char>('!', ',', '?', '-').contains(e.message.contentRaw[0])) {
                val quote : MotivationalQuote =
                    Klaxon().parse<MotivationalQuote>(
                        Bot.quoteBank[
                                (Math.random() * Bot.quoteBank.size).roundToInt()
                        ].toString()
                    )!!
                e.channel.sendMessage("> ${quote.text}\n" +
                    "- ${quote.author}\n" +
                    "${e.author.asMention} get back to studying!")
                    .queue()
                e.message.delete().queue()
                println("deleted message: ${e.message.contentRaw}: from ${e.message.author.name}")
                return
            }
        } catch (NPE: NullPointerException) { return }

        if (e.message.contentRaw.startsWith("!quote")) {
            val quote = getQuote()
            e.channel.sendMessage("> ${quote.text}\n" +
                    "- ${quote.author}\n"
            )
                .queue()
        }
    }

    private fun getQuote(): MotivationalQuote =
        Klaxon().parse<MotivationalQuote>(
            Bot.quoteBank[
                    (Math.random() * Bot.quoteBank.size).roundToInt()
            ].toString()
        )!!

    data class MotivationalQuote (val text : String, val author : String)
}