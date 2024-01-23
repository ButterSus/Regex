import com.buttersus.regex.RegexLexer
import com.buttersus.regex.toSource
import com.buttersus.regex.*

fun main() {
    // resources/main.txt
    val `𝚂` = RegexLexer::class.java.getResource("/main.txt")
        ?.readText()?.toSource() ?: throw Exception("Could not read file")
    val `𝕃` = RegexLexer()(`𝚂`).tokenize()
    for (token in `𝕃`) {
        println(token)
    }
    val `ℙ` = RegexParser()(RegexLexer()(`𝚂`).tokenize())
    println(`ℙ`.parse())
}
