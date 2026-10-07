
const val TARGET = "kot"      // искомая подстрока
const val ALPHABET_SIZE = 26  // букв в латинском алфавите

/** Результаты подсчёта символов. */
class TextStats(
    val totalChars: Int,
    val digits: Int,
    val spaces: Int,
    val otherChars: Int,   // кириллица, знаки препинания и всё остальное
    val lower: IntArray,   // счётчики строчных a–z
    val upper: IntArray    // счётчики заглавных A–Z
)

/** Читает строку. Если ввода нет совсем (null) — считаем строку пустой. */
fun readInput(): String {
    print("Введите строку: ")
    return readlnOrNull() ?: ""
}

/** Один проход по строке: классифицируем каждый символ. */
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

/** Общее количество букв a–z и A–Z. */
fun totalLetters(stats: TextStats): Int {
    var sum = 0
    for (i in 0 until ALPHABET_SIZE) {
        sum += stats.lower[i] + stats.upper[i]
    }
    return sum
}

/** Количество уникальных символов (вложенные циклы). */
fun countUnique(text: String): Int {
    var unique = 0
    for (i in text.indices) {
        var seenBefore = false
        // смотрим, не встречался ли этот символ раньше
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

/** Таблица: сколько раз встретилась каждая буква (строчная / заглавная). */
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

/** Гистограмма (вложенные циклы). Заглавные и строчные объединены. */
fun buildHistogram(stats: TextStats): String {
    val sb = StringBuilder()
    for (i in 0 until ALPHABET_SIZE) {                // внешний цикл — по буквам
        val total = stats.lower[i] + stats.upper[i]
        if (total > 0) {
            sb.append("  ").append('a' + i).append(':')
            for (star in 1..total) {                  // внутренний цикл — по звёздочкам
                sb.append('*')
            }
            sb.append('\n')
        }
    }
    return if (sb.isEmpty()) "  букв нет" else sb.toString().trimEnd()
}

/** Поиск подстроки без indexOf: два вложенных цикла, регистр не важен. */
fun containsIgnoreCase(text: String, target: String): Boolean {
    if (text.length < target.length) {
        return false
    }
    for (start in 0..text.length - target.length) {   // где начинается совпадение
        var match = true
        for (offset in target.indices) {              // сравниваем символ за символом
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

/** Многострочный отчёт. */
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