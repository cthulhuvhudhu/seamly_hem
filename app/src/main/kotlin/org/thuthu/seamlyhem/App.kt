package org.thuthu.seamlyhem

import org.thuthu.seamlyhem.App.Companion.HEIGHT_FLAG
import org.thuthu.seamlyhem.App.Companion.IN_FLAG
import org.thuthu.seamlyhem.App.Companion.OUT_FLAG
import org.thuthu.seamlyhem.App.Companion.WIDTH_FLAG
import kotlin.system.exitProcess

class App {
    internal val operationsManager = OperationsManager()

    companion object {
        internal const val IN_FLAG = "-in"
        internal const val OUT_FLAG = "-out"
        internal const val WIDTH_FLAG = "-width"
        internal const val HEIGHT_FLAG = "-height"
    }
}

fun main(xargs: Array<String>) {
    checkUsage(xargs)
    val outImageName = xargs[xargs.indexOfFirst { it == OUT_FLAG } + 1]
    val inputFileName = xargs[xargs.indexOfFirst { it == IN_FLAG } + 1]
    val xWidth = xargs[xargs.indexOfFirst { it == WIDTH_FLAG } + 1].toInt()
    val xHeight = xargs[xargs.indexOfFirst { it == HEIGHT_FLAG } + 1].toInt()

    check(outImageName.isNotBlank()) { "Valid image name for output required." }
    check(inputFileName.isNotBlank()) { "Valid file name for input required." }

    App().operationsManager.process(inputFileName, outImageName, xWidth, xHeight)
    TODO("Test validation")
    TODO("Validate on learning platform")
    TODO("Performance evaluation: Evaluate stepwise reiterations")
}

private fun checkUsage(xargs: Array<String>) {
    if (xargs.contains("help") || xargs.isEmpty() || !xargs.contains(IN_FLAG) || !xargs.contains(OUT_FLAG)
        || !xargs.contains(WIDTH_FLAG) || !xargs.contains(HEIGHT_FLAG)) {
        println("Expected usage:")
        println("-in inputFileName -out outputFileName -width intPixels -height intPixels")
        exitProcess(0)
    }
}