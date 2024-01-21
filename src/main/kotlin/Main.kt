import com.buttersus.regex.RegexLexer
import com.buttersus.regex.toSource
import org.kiwilang.regex.*

fun main() {
    // resources/main.txt
    val `𝚂` = RegexLexer::class.java.getResource("/main.txt")
        ?.readText()?.toSource() ?: throw Exception("Could not read file")
    val `𝕃` = RegexLexer()(`𝚂`).tokenize()
    for (`𝚝` in `𝕃`) println(`𝚝`)
}
