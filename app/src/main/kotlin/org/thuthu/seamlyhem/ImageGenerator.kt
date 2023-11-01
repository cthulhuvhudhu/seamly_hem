package org.thuthu.seamlyhem

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import kotlin.math.sqrt
import kotlin.properties.Delegates

class ImageGenerator {

    private var maxEnergyValue by Delegates.notNull<Double>()

    fun generateRedCross(w: Int, h: Int): RenderedImage {
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
                input.setRGB(x, y, Companion.MAX_RGB - rgb)
            }
        }
        return input
    }

    fun generateIntensity(input: BufferedImage): RenderedImage {
        println("Calculating energy ...")
        assert(input.width > 2 && input.height > 2)

        val image = BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB)

        val energies = calculateEnergies(input)
        maxEnergyValue = energies.max()

        val iter = energies.iterator()

        for (x in 0 until input.width) {
            for (y in 0 until input.height) {
                val iColor = intensity(iter.next())
                image.setRGB(x, y, iColor.rgb)
            }
        }
        return image
    }

    private fun calculateEnergies(input: BufferedImage): ArrayDeque<Double> {
        val energies = ArrayDeque<Double>(input.width * input.height)

        var prevX: Int
        var nextX: Int
        var prevY: Int = -1
        var nextY: Int
        var currY: Int = -1

        for (x in 0 until input.width) {
            for (y in 0 until input.height) {
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

                energies.add(dSqY.e(dSqX))
            }
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
