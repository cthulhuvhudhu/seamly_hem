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
//        println("Enter output image name:")
//        val outputFileName = readln() // out.png

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

    fun generateInverted(input: BufferedImage): RenderedImage {
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

    fun generateIntensity(input: BufferedImage): RenderedImage {

        val energies = calculateEnergies(input)

        // TODO refactor below into a receiving function

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

    fun generateSeam(input: BufferedImage): RenderedImage  {

        val energies = calculateEnergies(input) // ArrayDeque<Double>

        // TODO refactor below into a receiving function

        // calc ALL seams

        println("Dynamically calculating seam values...")
        // Populate
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                if (y > 0) {
                    var min = energies[y-1][x]
                    if (x > 0) {
                        min = min.coerceAtMost(energies[y - 1][x - 1])
                    }
                    if (x < input.width-1) {
                        min = min.coerceAtMost(energies[y - 1][x + 1])
                    }
                    energies[y][x] = energies[y][x] + min
                }
            }
        }

        // What do I do with multiple seams? Will do greedy first

        // Find min at bottom and Greedy process
        var min = energies[input.height-1].min()
        var y = input.height-1
        var x = energies[y].indexOf( min )

        while (y > 0) {

            // Paint
//            println("Painting $x $y: ${energies[y][x]}")
            input.setRGB(x, y, Color(255, 0, 0).rgb)

            // iterate through known parents; then know indices directly
            y -= 1
            var newX = x
            min = energies[y][newX]
            if (x > 0 && energies[y][x-1] < min) {
                newX -= 1
                min = energies[y][newX]
            }
            if (x < input.width-1 && energies[y][x+1] < min) {
                newX = x+1
            }
            x = newX
        }
        return input
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
    }
}
