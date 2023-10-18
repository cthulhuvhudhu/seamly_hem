package org.thuthu.seamlyhem

import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

class FileManager {

    fun saveImage(f: RenderedImage, fileName: String) {

        // TODO overwrite ok; may be why it takes logner
        val outputFolderPath = System.getProperty("OUTPUT_DIR") ?: "output"
        val outputDirectory = File(outputFolderPath)
        val outputFile = File(outputDirectory, fileName)
        ImageIO.write(f, "PNG", outputFile)

        // TODO replace with logs
        println("File saved as $fileName")
    }
}
