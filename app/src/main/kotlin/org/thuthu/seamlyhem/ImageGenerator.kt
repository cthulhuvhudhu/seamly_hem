package org.thuthu.seamlyhem

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import kotlin.math.sqrt
import kotlin.properties.Delegates

class ImageGenerator {

    private var maxEnergyValue by Delegates.notNull<Double>()

    fun generateRedCross(): RenderedImage {
        println("Enter rectangle width:")
        val w = readln().toInt() // 20
        println("Enter rectangle height:")
        val h = readln().toInt() // 20

        println("Generating red cross ...")
        // Create a BufferedImage with a black square
        val image = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)

        // Create a Graphics2D object for drawing on the image
        val g2d = image.createGraphics()

        // Fill a black square
        g2d.color = Color.BLACK
        g2d.fillRect(0, 0, w, h)

        // Draw two diagonal red lines
        g2d.color = Color.RED
        g2d.drawLine(0, 0, w, h)
        g2d.drawLine(0, h, w, 0)

        // Dispose of the Graphics2D object
        g2d.dispose()
        return image
    }

    fun generateInverted(input: BufferedImage): BufferedImage {
        println("Inverting image ...")
        // To create a negative image, you should invert all color components for every pixel.
        // Inverted color for (r, g, b) is (255 - r, 255 - g, 255 - b).
        for (x in 0 until input.width) {
            for (y in 0 until input.height) {
                val rgb = input.getRGB(x, y) // HEX RGB int
                input.setRGB(x, y, MAX_RGB - rgb)
            }
        }
        return input
    }

    fun generateIntensity(input: BufferedImage): BufferedImage {

        val energies = calculateEnergies(input)

        println("Calculating intensity...")

        val image = BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB)
        maxEnergyValue = energies.flatten().max()

        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val iColor = intensity(energies[y][x])
                image.setRGB(x, y, iColor.rgb)
            }
        }
        return image
    }

    private fun generateSeam(input: BufferedImage, seamHandler: (BufferedImage, List<Pair<Int, Int>>) -> (BufferedImage), isXSeam: Boolean = false): BufferedImage  {
        var energies = calculateEnergies(input)
        if (isXSeam) {
            energies = transpose(energies)
        }
        val seamEnergies = sumSeam(energies)
        val seamPixels = mutableListOf<Pair<Int, Int>>()

        // Find min at bottom and Greedy process
        var min = seamEnergies.last().min()
        var y = seamEnergies.size-1
        var x = seamEnergies[y].indexOf( min )

        while (y > 0) {

            // Paint
            if (isXSeam) {
                seamPixels.add(y to x)
            } else {
                seamPixels.add(x to y)
            }

            // iterate through known parents; then know indices directly
            y -= 1
            var newX = x
            min = seamEnergies[y][newX]
            if (x > 0 && seamEnergies[y][x-1] < min) {
                newX -= 1
                min = seamEnergies[y][newX]
            }
            if (x < seamEnergies[0].size-1 && seamEnergies[y][x+1] < min) {
                newX = x+1
            }
            x = newX
        }
        return seamHandler(input, seamPixels)
    }

    fun iterativeRemoveSeamHandler(input: BufferedImage): BufferedImage {
        check(xWidth < input.width) { "width parameter must be less than that of input image" }
        check(xHeight < input.height) { "height parameter must be less than that of input image" }

        var image = input
        for (wRemove in 1..xWidth) {
            image = generateSeam(image, ::removeSeamHandler)
        }
        for (hRemove in 1..xHeight) {
            image = generateSeam(image, ::removeXSeamHandler, true)
        }
        return image
    }

    private fun removeSeamHandler(input: BufferedImage, seam: List<Pair<Int, Int>>): BufferedImage {
        val image = BufferedImage(input.width - 1, input.height, input.type)
        (0 until image.height).forEach{ y ->
            var newX = 0
            (0 until image.width).forEach{ x ->
                if (!seam.contains(x to y)) {
                    image.setRGB(newX, y, input.getRGB(x, y))
                    newX++
                }
            }
        }
        return image
    }

    private fun removeXSeamHandler(input: BufferedImage, seam: List<Pair<Int, Int>>): BufferedImage {
        val image = BufferedImage(input.width, input.height-1, input.type)
        (0 until image.width).forEach { x ->
            var newY = 0
            (0 until image.height).forEach { y ->
                if (!seam.contains(x to y)) {
                    image.setRGB(x, newY, input.getRGB(x, y))
                    newY++
                }
            }
        }
        return image
    }

    fun paintSeamHandler(input: BufferedImage, seam: List<Pair<Int, Int>>): BufferedImage {
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                if (seam.contains(x to y)) {
                    input.setRGB(x, y, Color(255, 0, 0).rgb)
                }
            }
        }
        return input
    }

    private fun sumSeam(energies: MutableList<MutableList<Double>>): List<List<Double>> {
        println("Dynamically calculating seam values...")
        for (y in energies.indices) {
            for (x in energies[0].indices) {
                if (y > 0) {
                    var min = energies[y-1][x]
                    if (x > 0) {
                        min = min.coerceAtMost(energies[y - 1][x - 1])
                    }
                    if (x < energies[0].size-1) {
                        min = min.coerceAtMost(energies[y - 1][x + 1])
                    }
                    energies[y][x] = energies[y][x] + min
                }
            }
        }
        return energies
    }

    private fun calculateEnergies(input: BufferedImage): MutableList<MutableList<Double>> {
        println("Calculating energy ...")
        assert(input.width > 2 && input.height > 2)

        val energies = mutableListOf<MutableList<Double>>()

        var prevX: Int
        var nextX: Int
        var prevY: Int = -1
        var nextY: Int
        var currY: Int = -1

        for (y in 0 until input.height) {

            val rowEnergy = mutableListOf<Double>()

            for (x in 0 until input.width) {
                // calc Ys

                when (y) {
                    0 -> {
                        currY = input.getRGB(x, y)
                        prevY = currY
                        nextY = input.getRGB(x, 2)
                    }
                    input.height-1 -> {
                        prevY = input.getRGB(x, input.height-3)
                        nextY = currY
                    }
                    else -> {
                        nextY = input.getRGB(x, y+1)
                    }
                }

                val dSqY = Color(prevY).dSq(Color(nextY))

                prevY = currY
                currY = nextY

                // calc Xs

                when (x) {
                    0 -> {
                        prevX = input.getRGB(0, y)
                        nextX = input.getRGB(2, y)
                    }
                    input.width-1 -> {
                        prevX = input.getRGB(input.width-3, y)
                        nextX = input.getRGB(input.width-1, y)
                    }
                    else -> {
                        prevX = input.getRGB(x-1, y)
                        nextX = input.getRGB(x+1, y)
                    }
                }

                val dSqX = Color(prevX).dSq(Color(nextX))

                rowEnergy.add(dSqY.e(dSqX))
            }
            energies.add(rowEnergy)
        }
        return energies
    }

    private fun transpose(m: List<List<Double>>): MutableList<MutableList<Double>> {
        val t = MutableList(m[0].size){
            MutableList(m.size) { 0.0 }
        }

        for (r in m.indices) {
            for (c in m[0].indices) {
                t[c][r] = m[r][c]
            }
        }
        return t
    }

    private infix fun Color.dSq(a: Color): Int {
        val rDiff = a.red-this.red
        val gDiff = a.green-this.green
        val bDiff = a.blue-this.blue
        return rDiff*rDiff + gDiff*gDiff + bDiff*bDiff
    }

    private infix fun Int.e(i: Int): Double {
        return sqrt((this+i).toDouble())
    }

    private fun intensity(e: Double): Color {
        val i = ((255.0 * e) / (maxEnergyValue)).toInt()
        return Color(i, i, i)
    }

    companion object {
        private const val MAX_RGB: Int = 0xFFFFFF
        var xWidth: Int = 0
        var xHeight: Int = 0
    }
}
