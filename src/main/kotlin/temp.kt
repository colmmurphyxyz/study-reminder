import org.json.JSONObject

fun main() {
    val quoteBank = ArrayList<JSONObject>()
    val quotes = khttp.get("https://type.fit/api/quotes").jsonArray
    for (i in quotes) {
        quoteBank.add(i as JSONObject)
    }
    println(quoteBank.joinToString("\n"))
}