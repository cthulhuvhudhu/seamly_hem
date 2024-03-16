package org.thuthu.seamlyhem

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage

class OperationsManager(
    private val inputFileName: String,
    private val outputFileName: String,
    private val xWidth: Int,
    private val xHeight: Int
) : KoinComponent {

    private val iGenerator: ImageGenerator by inject { parametersOf(xWidth, xHeight) }
    private val fileManager: FileManager by inject()

    fun process() {
        stage(iGenerator::generateRedCross, "-xcross")
        stage(iGenerator::generateInverted, "-inverted")
        stage(iGenerator::generateIntensity, "-energy")
        stage(iGenerator::paintYSeam, "-yseam")
        stage(iGenerator::paintXSeam, "-xseam")
        stage(iGenerator::trim, "-trim")
    }

    private fun stage(xform: () -> (RenderedImage), suffix: String) {
        val image = xform()
        fileManager.saveImage(image, "$outputFileName$suffix.png")
    }

    private fun stage(xform: (BufferedImage) -> (RenderedImage), suffix: String) {
        val inFile = fileManager.getImage(inputFileName)
        val image = xform(inFile)
        fileManager.saveImage(image, "$outputFileName$suffix.png")
    }
}
