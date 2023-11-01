package org.thuthu.seamlyhem

import kotlin.system.exitProcess

class App {
    internal val operationsManager = OperationsManager()
}

fun main(xargs: Array<String>) {

    if (xargs.contains("help")) {
        usage()
        exitProcess(0)
    }

    if (xargs.isEmpty()) {
        App().operationsManager.stage1()
        exitProcess(0)
    }

    // Intentional fail if incorrect params. Add usage message, retry later.
    val outImageName = xargs[xargs.indexOfFirst { it == "-out" } + 1]
    val inputFileName = xargs[xargs.indexOfFirst { it == "-in" } + 1]
    App().operationsManager.stage2(inputFileName, outImageName)
    App().operationsManager.stage3(inputFileName, outImageName)
}

private fun usage() {
    println("Expected usage:")
    println("Stage One - Black rectangle with red cross: no parameters")
    println("Stage two - Invert image; Stage three - Intensity image: -in inputFileName -out outputFileName")
}