import com.beust.klaxon.Klaxon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.roundToInt

class MListener : ListenerAdapter() {
    private val pomoBotId = "674238793431384067"
    private val genCategoryId = "750706717796466781"
    private val studyRole : Role = Bot.LCGuild.getRoleById("823202557630873630")!!
    var inStudyMode = inStudyMode()
    override fun onGuildMessageReceived(e : GuildMessageReceivedEvent) {
        if (e.message.author.id == pomoBotId) {
            if (e.message.contentRaw.contains("Break** finished!")) {
                inStudyMode = true
                println("entered study mode")
            } else if (e.message.contentRaw.contains("Study** finished!")) {
                inStudyMode = false
                println("entered break mode")
            }
        }
        try {
            if (e.member!!.roles.contains(studyRole) && inStudyMode
                && e.message.category!!.id.equals(genCategoryId)) {
                val waiter = Bot.waiter
                var delete = true
                waiter.waitForEvent(GuildMessageReceivedEvent::class.java,
                    { ev ->
                        (ev.author.id == pomoBotId)
                    },
                    { _ ->
                        delete = false
                    },
                    5L, TimeUnit.SECONDS,
                    { -> // what to do when the EventWaiter times out
                    }
                )
                GlobalScope.launch {
                    delay(5000L)
                    if (delete && Math.random() < 0.3) {
                        val quote = getQuote()
                        e.message.delete().queue { msg ->
                            e.channel.sendMessage(
                                "> ${quote.text}\n" +
                                        "- ${quote.author}\n" +
                                        "${e.author.asMention} get back to studying!"
                            ).queue()
                        }
                        println("deleted a message from ${e.author.name}: ${e.message.contentRaw}")
                    } else if (Math.random() < 0.15) {
                            e.message.addReaction("U+1F1F8").queue()
                            e.message.addReaction("U+1F1F9").queue()
                            e.message.addReaction("U+1F1FA").queue()
                            e.message.addReaction("U+1F1E9").queue()
                            e.message.addReaction("U+1F1FE").queue()
                    } else if (Math.random() < 0.2) {
                        println("sending motivational gif")
                        e.channel.sendFile(File("src/main/resources/${(Math.random() * 9).toInt() + 1}.gif")).queue()
                    }
                }
            }
        } catch (ignored : NullPointerException) { }
        if (e.message.contentRaw.startsWith("!quote")) {
            val quote = getQuote()
            e.channel.sendMessage("> ${quote.text}\n" +
                    "- ${quote.author}\n"
            ).queue()
        } else if (e.message.contentRaw.startsWith("!gif")) {
            println("sending motivational gif!")
            println((Math.random() * 9).toInt() + 1)
            e.channel.sendFile(
                File("src/main/resources/${(Math.random() * 9).toInt() + 1}.gif")
            ).queue()
        }
    }

    private fun inStudyMode(): Boolean {
        val foo = Bot.LCGuild.getTextChannelById("823202689995112489")!!.iterableHistory
        val iterator = foo.iterator()
        findMode@while (iterator.hasNext()) {
            val nxt = iterator.next()
            if (nxt.author.id != pomoBotId) {
                continue@findMode
            } else {
                if (nxt.contentRaw.contains("Break** finished!")) {
                    return true
                } else if (nxt.contentRaw.contains("Study** finished!")) {
                    return false
                }
            }
        }
        return true
    }

    private fun getQuote(): MotivationalQuote =
        Klaxon().parse<MotivationalQuote>(
            Bot.quoteBank[
                    (Math.random() * Bot.quoteBank.size).roundToInt()
            ].toString()
        )!!

    data class MotivationalQuote (val text : String, val author : String)
}