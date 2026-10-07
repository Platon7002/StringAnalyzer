
const val TARGET = "kot"   
const val ALPHABET_SIZE = 26 

class TextStats(
    val totalChars: Int,
    val digits: Int,
    val spaces: Int,
    val otherChars: Int, 
    val lower: IntArray,  
    val upper: IntArray    // счётчики заглавных A–Z

fun readInput(): String {
    print("Введите строку: ")
    return readlnOrNull() ?: ""
}
fun analyze(text: String): TextStats {
    var digits = 0
    var spaces = 0
    var others = 0
    val lower = IntArray(ALPHABET_SIZE)
    val upper = IntArray(ALPHABET_SIZE)

    for (ch in text) {
        when {
            ch in 'a'..'z' -> lower[ch - 'a']++
            ch in 'A'..'Z' -> upper[ch - 'A']++
            ch in '0'..'9' -> digits++
            ch.isWhitespace() -> spaces++
            else -> others++
        }
    }
    return TextStats(text.length, digits, spaces, others, lower, upper)
}

fun totalLetters(stats: TextStats): Int {
    var sum = 0
    for (i in 0 until ALPHABET_SIZE) {
        sum += stats.lower[i] + stats.upper[i]
    }
    return sum
}

fun countUnique(text: String): Int {
    var unique = 0
    for (i in text.indices) {
        var seenBefore = false
        for (j in 0 until i) {
            if (text[j] == text[i]) {
                seenBefore = true
                break
            }
        }
        if (!seenBefore) {
            unique++
        }
    }
    return unique
}

fun buildLettersTable(stats: TextStats): String {
    val sb = StringBuilder()
    for (i in 0 until ALPHABET_SIZE) {
        if (stats.lower[i] > 0 || stats.upper[i] > 0) {
            val small = 'a' + i
            val big = 'A' + i
            sb.append("  $small: ${stats.lower[i]}, $big: ${stats.upper[i]}\n")
        }
    }
    return if (sb.isEmpty()) "  букв нет" else sb.toString().trimEnd()
}

fun buildHistogram(stats: TextStats): String {
    val sb = StringBuilder()
    for (i in 0 until ALPHABET_SIZE) {              
        val total = stats.lower[i] + stats.upper[i]
        if (total > 0) {
            sb.append("  ").append('a' + i).append(':')
            for (star in 1..total) {              
                sb.append('*')
            }
            sb.append('\n')
        }
    }
    return if (sb.isEmpty()) "  букв нет" else sb.toString().trimEnd()
}

fun containsIgnoreCase(text: String, target: String): Boolean {
    if (text.length < target.length) {
        return false
    }
    for (start in 0..text.length - target.length) {   
        var match = true
        for (offset in target.indices) {             
            if (text[start + offset].lowercaseChar() != target[offset].lowercaseChar()) {
                match = false
                break
            }
        }
        if (match) {
            return true
        }
    }
    return false
}

fun buildReport(text: String, stats: TextStats, unique: Int, hasKot: Boolean): String {
    val shownText = if (text.isEmpty()) "(пустая строка)" else text
    val letters = totalLetters(stats)
    val lettersTable = buildLettersTable(stats)
    val histogram = buildHistogram(stats)
    val kotAnswer = if (hasKot) "да" else "нет"

    return """
        |===== Отчёт StringAnalyzer =====
        |Строка: $shownText
        |
        |Всего символов: ${stats.totalChars}
        |Цифр: ${stats.digits}
        |Пробелов: ${stats.spaces}
        |Букв (a–z, A–Z): $letters
        |Прочих символов (кириллица, знаки и т.д.): ${stats.otherChars}
        |Уникальных символов: $unique
        |
        |Буквы по типам:
        |$lettersTable
        |
        |Гистограмма:
        |$histogram
        |
        |Подстрока "$TARGET" (в любом регистре): $kotAnswer
    """.trimMargin()
}

fun main() {
    val text = readInput()
    val stats = analyze(text)
    val unique = countUnique(text)
    val hasKot = containsIgnoreCase(text, TARGET)

    println()
    println(buildReport(text, stats, unique, hasKot))
}
