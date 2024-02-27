import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

object LocalPropertiesReader {

    fun loadUpdateServer(file: File): String {
        if (!file.isFile) {
            error("Given file (`$file`) does not exist!")
        }

        val properties = Properties()
        InputStreamReader(FileInputStream(file), Charsets.UTF_8). use { reader ->
            properties.load(reader)
        }


        return '"' + properties.getProperty("update.server") + '"'
    }
}