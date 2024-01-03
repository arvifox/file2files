import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

fun main(args: Array<String>) {
    println("Hello World!")

    when (args[0]) {
        "0" -> addTop(args[1], args[2])
        "1" -> countComm(args[1], args[2])
        "2" -> clearIdea(args[1])
        "3" -> readWhite(args[1], args[2])
    }

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Done")
}

private fun readWhite(url: String, dir: String) {
    runBlocking {
        val client = NetClient()
        val response = client.createJsonRequest<JsonArray>(url)
        println("response count = ${response.size}")
        val mapped = response.mapNotNull {
            it as JsonObject
            it["address"]?.jsonPrimitive?.content
        }
        println("response mapped count = ${mapped.size}")
        val file = File(dir, "qweqwe.txt")
        file.writeText(Json.encodeToString(mapped))
        println("written file")
    }
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
