package org.thuthu.seamlyhem

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App

val appModule = module {
    single { params -> ImageGenerator(xWidth = params.get(), xHeight = params.get()) }
    single { params -> OperationsManager(inputFileName = params.get(), outputFileName = params.get(), xWidth = params.get(), xHeight = params.get()) }
    singleOf(::FileManager)
    singleOf(::OperationsManager)
}

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }

    val parser = ArgParser("seamlyhem")
    val inputFileName by parser.option(
        ArgType.String,
        shortName = "i",
        fullName = "in",
        description = "input filename",
    ).required()
    val outImageName by parser.option(
        ArgType.String,
        shortName = "o",
        fullName = "out",
        description = "output root filename"
    ).default("out")
    val xWidth by parser.option(
        ArgType.Int,
        shortName = "xw",
        fullName = "xwidth",
        description = "width reduction amount"
    ).default(0)
    val xHeight by parser.option(
        ArgType.Int,
        shortName = "xh",
        fullName = "xheight",
        description = "height reduction amount"
    ).default(0)
    parser.parse(args)

    OperationsManager(inputFileName, outImageName, xWidth, xHeight).process()
}
