import java.io.File

fun main(args: Array<String>) {
    println("Hello World!")

    when (args[0]) {
        "0" -> addTop(args[1], args[2])
        "1" -> countComm(args[1], args[2])
        "2" -> clearIdea(args[1])
    }

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Done")
}

private fun clearIdea(dir: String) {
    File(dir).list()?.forEach {
        val f = File(dir + it)
        if (f.isDirectory) {
            if ((f.name == ".idea") || (f.name == ".gradle")) {
                f.deleteRecursively()
                println("${f.name} deleted")
            }
            f.list()?.forEach { nested ->
                val nestedFile = File(f.absolutePath + File.separator + nested)
                if (nestedFile.isDirectory && nested == "build") {
                    nestedFile.deleteRecursively()
                    println("${nestedFile.absolutePath} deleted")
                }
            }
        }
    }
}

private fun countComm(dir: String, text: String) {
    var count = 0
    File(dir).walkTopDown().forEach {
        if (it.isFile && it.extension == "kt") {
            val lines = it.readLines()
            if (lines[1].contains(text, true).not()) {
                println("file=${it.absolutePath}")
                count++
            }
        }
    }
    println("count = $count")
}

private fun addTop(dir: String, textToAdd: String) {
    val inputFile = File(textToAdd)
    val inputText = inputFile.readText()

    var count = 0
    File(dir).walkTopDown().forEach {
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
}
