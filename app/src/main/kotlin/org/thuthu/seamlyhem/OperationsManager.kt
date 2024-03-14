package org.thuthu.seamlyhem

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage

class OperationsManager(private val iGenerator: ImageGenerator = ImageGenerator(),
        private val fileManager: FileManager = FileManager()) {

    private lateinit var input: String
    private lateinit var output: String

    fun process(inputFileName: String, outputFileName: String, xWidth: Int, xHeight: Int) {
        input = inputFileName
        output = outputFileName
        ImageGenerator.xWidth = xWidth
        ImageGenerator.xHeight = xHeight

        stage(iGenerator::generateRedCross, "-xcross")
        stage(iGenerator::generateInverted, "-inverted")
        stage(iGenerator::generateIntensity, "-energy")
        stage(iGenerator::generateYSeam, iGenerator::paintSeamHandler, "-yseam")
        stage(iGenerator::generateXSeam, iGenerator::paintSeamHandler, "-xseam")
        stage(iGenerator::iterativeRemoveSeamHandler, "-trim")
    }

    private fun stage(xform: () -> (RenderedImage), suffix: String) {
        val image = xform()
        fileManager.saveImage(image, "$output$suffix.png")
    }

    private fun stage(xform: (BufferedImage, (BufferedImage, List<ImageGenerator.Pixel>) -> (BufferedImage)) -> (BufferedImage),
                      seamHandler: (BufferedImage, List<ImageGenerator.Pixel>) -> (BufferedImage),
                      suffix: String) {
        val inFile = fileManager.getImage(input)
        val image = xform(inFile, seamHandler)
        fileManager.saveImage(image, "$output$suffix.png")
    }

    private fun stage(xform: (BufferedImage) -> (BufferedImage), suffix: String) {
        val inFile = fileManager.getImage(input)
        val image = xform(inFile)
        fileManager.saveImage(image, "$output$suffix.png")
    }
}