package org.thuthu.seamlyhem

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import kotlin.math.sqrt

class ImageGenerator {

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
        g2d.drawLine(0, 0, w - 1, h - 1)
        g2d.drawLine(0, h - 1, w - 1, 0)

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
        val image = BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB)

        println("Calculating intensity...")

        val maxEnergyValue = energies.flatten().max()

        (0 until input.height).forEach { y ->
            (0 until input.width).forEach { x ->
                val iColor = intensity(energies[y][x], maxEnergyValue)
                image.setRGB(x, y, iColor.rgb)
            }
        }
        return image
    }

    class Pixel(val x: Int, val y: Int, val sumE: Double)

    fun generateYSeam(input: BufferedImage, seamHandler: (BufferedImage, List<Pixel>) -> (BufferedImage)): BufferedImage  {
        return generateSeam(input, seamHandler, false)
    }

    fun generateXSeam(input: BufferedImage, seamHandler: (BufferedImage, List<Pixel>) -> (BufferedImage)): BufferedImage  {
        return generateSeam(input, seamHandler, true)
    }

    private fun generateSeam(input: BufferedImage, seamHandler: (BufferedImage, List<Pixel>) -> (BufferedImage), isXSeam: Boolean = false): BufferedImage  {
        var energies = calculateEnergies(input)
        if (isXSeam) {
            energies = transpose(energies)
        }

        // Top to bottom dijkstra, pruning each row as islets
        var path = energies[0].mapIndexed { idx, e -> listOf(Pixel(idx, 0, e)) }.toTypedArray()

        (1 until energies.size - 1).forEach { y ->
            val nextRow = Array<List<Pixel>>(energies[0].size) {  emptyList() }
            (energies[0].indices).forEach { x ->
                val candidates = mutableListOf(path[x])
                if (x - 1 in energies[0].indices) {
                    candidates.add(path[x - 1])
                }
                if (x + 1 in energies[0].indices) {
                    candidates.add(path[x + 1])
                }
                val winner = candidates.minByOrNull { it.last().sumE }!!.toMutableList()
                winner.add(Pixel(x, y, energies[y][x] + winner.last().sumE))
                nextRow[x] = winner
            }
            path = nextRow
        }

        val seamPixels = if (isXSeam) {
            path.minByOrNull { it.last().sumE }!!.map { Pixel(it.y, it.x, it.sumE) }.toList()
        } else {
            path.minByOrNull { it.last().sumE }!!
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

    private fun removeSeamHandler(input: BufferedImage, seam: List<Pixel>): BufferedImage {
        val image = BufferedImage(input.width - 1, input.height, input.type)
        (0 until image.height).forEach{ y ->
            var newX = 0
            (0 until image.width).forEach{ x ->
                if (seam.none { it.x == x && it.y == y }) {
                    image.setRGB(newX, y, input.getRGB(x, y))
                    newX++
                }
            }
        }
        return image
    }

    private fun removeXSeamHandler(input: BufferedImage, seam: List<Pixel>): BufferedImage {
        val image = BufferedImage(input.width, input.height-1, input.type)
        (0 until image.width).forEach { x ->
            var newY = 0
            (0 until image.height).forEach { y ->
                if (seam.none { it.x == x && it.y == y }) {
                    image.setRGB(x, newY, input.getRGB(x, y))
                    newY++
                }
            }
        }
        return image
    }

    fun paintSeamHandler(input: BufferedImage, seam: List<Pixel>): BufferedImage {
        seam.forEach { input.setRGB(it.x, it.y, Color(255, 0, 0).rgb) }
        return input
    }

    private fun calculateEnergies(input: BufferedImage): MutableList<MutableList<Double>> {
        println("Calculating energy ...")
        assert(input.width > 2 && input.height > 2)

        val energies = mutableListOf<MutableList<Double>>()

        (0 until input.height).forEach { y ->
            val rowEnergy = mutableListOf<Double>()

            (0 until input.width).forEach { x ->
                val centerY = when(y) {
                    0 -> 1
                    input.height - 1 -> y - 1
                    else -> y
                }
                val above = input.getRGB(x, centerY - 1)
                val below = input.getRGB(x, centerY + 1)
                val dSqY = Color(above).dSq(Color(below))

                val centerX = when(x) {
                    0 -> 1
                    input.width - 1 -> x - 1
                    else -> x
                }
                val left = input.getRGB(centerX - 1, y)
                val right = input.getRGB(centerX + 1, y)
                val dSqX = Color(left).dSq(Color(right))

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

    private fun intensity(e: Double, maxEnergyValue: Double): Color {
        val i = ((255.0 * e) / (maxEnergyValue)).toInt()
        return Color(i, i, i)
    }

    companion object {
        private const val MAX_RGB: Int = 0xFFFFFF
        var xWidth: Int = 0
        var xHeight: Int = 0
    }
}
