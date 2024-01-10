package org.thuthu.seamlyhem

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import kotlin.reflect.KCallable

class OperationsManager(private val iGenerator: ImageGenerator = ImageGenerator(),
        private val fileManager: FileManager = FileManager()) {

    private lateinit var input: String
    private lateinit var output: String

    fun process(inputFileName: String, outputFileName: String, xWidth: Int, xHeight: Int) {
        input = inputFileName
        output = outputFileName
        ImageGenerator.xWidth = xWidth
        ImageGenerator.xHeight = xHeight

        stage(iGenerator::generateRedCross)
        stage(iGenerator::generateInverted)
        stage(iGenerator::generateIntensity)
        stage(iGenerator::generateYSeam, iGenerator::paintSeamHandler)
        stage(iGenerator::generateXSeam, iGenerator::paintSeamHandler)
        stage(iGenerator::iterativeRemoveSeamHandler)
    }

    private fun stage(xform: () -> (RenderedImage)) {
        val image = xform()
        fileManager.saveImage(image, "$output-${(xform as KCallable<*>).name}.png")
    }

    private fun stage(xform: (BufferedImage,(BufferedImage, List<Pair<Int, Int>>) -> (BufferedImage)) -> (BufferedImage),
                      seamHandler: (BufferedImage, List<Pair<Int, Int>>) -> (BufferedImage)) {
        val inFile = fileManager.getImage(input)
        val image = xform(inFile, seamHandler)
        fileManager.saveImage(image, "$output-${(xform as KCallable<*>).name}.png")
    }

    private fun stage(xform: (BufferedImage) -> (BufferedImage)) {
        val inFile = fileManager.getImage(input)
        val image = xform(inFile)
        fileManager.saveImage(image, "$output-${(xform as KCallable<*>).name}.png")
    }
}