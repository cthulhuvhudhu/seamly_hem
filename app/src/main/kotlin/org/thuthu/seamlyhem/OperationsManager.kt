package org.thuthu.seamlyhem

class OperationsManager {
    private val iGenerator = ImageGenerator()
    private val fileManager = FileManager()

    fun stage1() {
        println("Enter rectangle width:")
        val width = readln().toInt() // 20
        // TODO v2:exception handling; retry
        println("Enter rectangle height:")
        val height = readln().toInt() // 20
        println("Enter output image name:")
        val outputFileName = readln() // out.png
        val image = iGenerator.redCross(width, height)
        fileManager.saveImage(image, outputFileName)
    }

    fun stage2(inputFileName: String, outputFileName: String) {
        val stream = fileManager.getImage(inputFileName)
        val image = iGenerator.invert(stream)
        fileManager.saveImage(image, outputFileName)
    }
}