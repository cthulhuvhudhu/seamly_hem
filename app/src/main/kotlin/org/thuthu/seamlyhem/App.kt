package org.thuthu.seamlyhem

import org.thuthu.seamlyhem.App.Companion.IN_FLAG
import org.thuthu.seamlyhem.App.Companion.OUT_FLAG
import kotlin.system.exitProcess

class App {
    internal val operationsManager = OperationsManager()

    companion object {
        internal const val IN_FLAG = "-in"
        internal const val OUT_FLAG = "-out"
    }
}

fun main(xargs: Array<String>) {
    checkUsage(xargs)

    val outImageName = xargs[xargs.indexOfFirst { it == OUT_FLAG } + 1]
    val inputFileName = xargs[xargs.indexOfFirst { it == IN_FLAG } + 1]

    check(outImageName.isNotBlank()) { "Valid image name for output required." }
    check(inputFileName.isNotBlank()) { "Valid file name for input required." }

    App().operationsManager.process(inputFileName, outImageName)
}

private fun checkUsage(xargs: Array<String>) {
    if (xargs.contains("help") || xargs.isEmpty() || !xargs.contains(IN_FLAG) || !xargs.contains(OUT_FLAG)) {
        println("Expected usage:")
        println("-in inputFileName -out outputFileName")
        exitProcess(0)
    }
}