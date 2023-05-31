import java.io.File

fun main(args: Array<String>) {
    println("Hello World! 681")

    val inputFile = File(args[1])
    val inputText = inputFile.readText()

    var count = 0
    File(args[0]).walkTopDown().forEach {
        if (it.isFile && it.extension == "kt") {
            val lines = it.readLines()
            if (lines[1].contains("pekrin", true)) {
                val without = lines.subList(4, lines.lastIndex + 1).joinToString("\n")
                it.writeText(inputText)
                it.appendText(without)
                it.appendText("\n")
                count++
            }
        }
    }
    println("count = $count")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Done")
}