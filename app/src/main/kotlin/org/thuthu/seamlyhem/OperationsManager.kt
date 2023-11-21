package org.thuthu.seamlyhem

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import kotlin.reflect.KCallable

class OperationsManager(private val iGenerator: ImageGenerator = ImageGenerator(),
        private val fileManager: FileManager = FileManager()) {

    private lateinit var input: String
    private lateinit var output: String

    fun process(inputFileName: String, outputFileName: String) {
        input = inputFileName
        output = outputFileName
        stage(iGenerator::generateRedCross)
        stage(iGenerator::generateInverted)
        stage(iGenerator::generateIntensity)
        stage(iGenerator::generateYSeam)
        stage(iGenerator::generateXSeam)
    }

    private fun stage(xform: () -> (RenderedImage)) {
        val image = xform()
        fileManager.saveImage(image, "$output-${(xform as KCallable<*>).name}.png")
    }

    private fun stage(xform: (BufferedImage) -> (RenderedImage)) {
        val inFile = fileManager.getImage(input)
        val image = xform(inFile)
        fileManager.saveImage(image, "$output-${(xform as KCallable<*>).name}.png")
    }
}