package org.thuthu.seamlyhem

class App {
    internal val iGenerator = ImageGenerator()
    internal val fileManager = FileManager()
}
fun main() {
    println("Enter rectangle width:")
    val width: Int = readln().toInt() //20
    // TODO exception handling; retry
    // TODO replace printlns with logs
    println("Enter rectangle height:")
    val height = readln().toInt() // 20
    println("Enter output image name:")
    var imageName = readln() // out.png
    if (!imageName.endsWith(".png", false)) {
        imageName = "$imageName.png"
    }

    // Generate red cross
    val image = App().iGenerator.redCross(width, height)
    // Save
    App().fileManager.saveImage(image, imageName)
}