import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlin.js.Promise

val fs = js("require('fs')")
val exec = js("require('child_process').execSync")

fun main(args: Array<String>) {
    readDir("/Users/andrewreitz/shadow-creator").then {
        val files = it.filter { file -> file.endsWith(".mp3") }
            .map { file ->
                val spacedFile = "${file.removeSuffix(".mp3")}-spaced.mp3"
                file to spacedFile
            }
            .onEach { files ->
                sox("${files.first} -c 2 -r 48k ${files.second} pad 0 2 repeat 6")
            }

        val fileString = files.joinToString(" ") { f -> f.second }

        sox("$fileString /Users/andrewreitz/shadow-creator/shadowloop.mp3")

        files.forEach { p -> unlink(p.second).then { "Done!" } }
    }
}

fun sox(command: String) {
    exec("sox $command")
}

fun readDir(path: String): Promise<Array<String>> = fs.promises.readdir(path) as Promise<Array<String>>
fun unlink(path: String): Promise<Unit> = fs.promises.unlink(path) as Promise<Unit>

// maybe someday I can pass command line options to this program
class ShadowLoop: CliktCommand() {

    val inputDir by argument()
    val outputFile by argument()

    // todo options to choose how many loops and possibly what file types to support

    override fun run() {
        readDir(inputDir).then {
            val files = it.filter { file -> file.endsWith(".mp3") }
                .map { file ->
                    val spacedFile = "${file.removeSuffix(".mp3")}-spaced.mp3"
                    file to spacedFile
                }
                .onEach { files ->
                    sox("${files.first} -c 2 -r 48k ${files.second} pad 0 2 repeat 6")
                }

            val fileString = files.joinToString(" ") { f -> f.second }

            // check if output has .mp3
            // and check if it's a path if not
            // output to execution dir

            sox("$fileString shadowloop.mp3")

            files.forEach { p -> unlink(p.second).then { echo("Done!") } }
        }
    }
}
