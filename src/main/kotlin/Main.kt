import java.io.File

fun main(args: Array<String>) {
    println("Hello World!")

    val inputFile = File(args.first())
    val inputText = inputFile.readText()

    File(args[1]).walkTopDown().forEach {
        if (it.isFile && it.extension == "kt") {
            val content = it.readText()
            if (!content.startsWith(inputText)) {
                it.writeText(inputText)
                it.appendText("\n")
                it.appendText(content)
            }
        }
    }
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Done.")
}