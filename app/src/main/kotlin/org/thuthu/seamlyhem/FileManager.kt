package org.thuthu.seamlyhem

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

class FileManager {

    fun saveImage(f: RenderedImage, fileName: String) {
        val file = connectFile(sanitize(fileName), System.getProperty("OUTPUT_DIR") ?: "output")
        ImageIO.write(f, "png", file)

        println("File saved as $fileName")
    }

    fun getImage(fileName: String): BufferedImage {
        val file = connectFile(sanitize(fileName), System.getProperty("INPUT_DIR") ?: "sample_images")
        return ImageIO.read(file)
    }

    private fun connectFile(fileName:String, dirName: String): File {
        val dir = File(dirName)
        return File(dir, sanitize(fileName))
    }

    private fun sanitize(filename: String): String {
        if (!filename.endsWith(".png", false)) {
            return "$filename.png"
        }
        return filename
    }
}
