package org.thuthu.seamlyhem

import org.koin.core.component.KoinComponent
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import kotlin.math.sqrt

class ImageGenerator(private val xWidth: Int, private val xHeight: Int) : KoinComponent {

    fun generateRedCross(): RenderedImage {
        println("Enter rectangle width:")
        val w = readln().toInt()
        println("Enter rectangle height:")
        val h = readln().toInt()

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
        // Inverted color for (r, g, b) is (255 - r, 255 - g, 255 - b).
        (0 until input.width).forEach { x ->
            (0 until input.height).forEach { y ->
                val rgb = input.getRGB(x, y) // HEX RGB int
                input.setRGB(x, y, Companion.MAX_RGB - rgb)
            }
        }
        return input
    }

    fun generateIntensity(input: BufferedImage): BufferedImage {

        assert(input.width > 2 && input.height > 2)
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

    private fun generateSeam(input: BufferedImage, isXSeam: Boolean = false): List<Pixel>  {
        println("Finding seam...")
        var energies = calculateEnergies(input)
        if (isXSeam) {
            energies = transpose(energies)
        }

        // Top to bottom dijkstra, pruning each row as islets
        var paths = energies[0].mapIndexed { idx, e -> listOf(Pixel(idx, 0, e)) }.toTypedArray()

        (1 until energies.size).forEach { y ->
            val nextRow = Array<List<Pixel>>(energies[0].size) {  emptyList() }
            (energies[0].indices).forEach { x ->
                val candidates = mutableListOf(paths[x])
                if (x - 1 in energies[0].indices) {
                    candidates.add(paths[x - 1])
                }
                if (x + 1 in energies[0].indices) {
                    candidates.add(paths[x + 1])
                }
                val winner = candidates.minByOrNull { it.last().sumE }!!.toMutableList()
                winner.add(Pixel(x, y, energies[y][x] + winner.last().sumE))
                nextRow[x] = winner
            }
            paths = nextRow
        }

        var solution = paths.minByOrNull { it.last().sumE }!!

        if (isXSeam) {
            solution = solution.map { Pixel(it.y, it.x, it.sumE) }.toList()
        }

        return solution
    }

    fun trim(input: BufferedImage): BufferedImage {
        println("Trimming image...")
        check(xWidth < input.width) { "width parameter must be less than that of input image" }
        check(xHeight < input.height) { "height parameter must be less than that of input image" }

        var image = input
        repeat(xWidth) {
            val seam = generateSeam(image, false)
            image = removeYSeam(image, seam)
        }
        repeat(xHeight) {
            val seam = generateSeam(image, true)
            image = removeXSeam(image, seam)
        }
        return image
    }

    private fun removeYSeam(input: BufferedImage, seam: List<Pixel>): BufferedImage {
        val image = BufferedImage(input.width - 1, input.height, BufferedImage.TYPE_INT_RGB)
        (0 until input.height).forEach { y ->
            var newX = 0
            (0 until input.width).forEach { x ->
                if (seam.none { it.x == x && it.y == y }) {
                    image.setRGB(newX, y, input.getRGB(x, y))
                    newX++
                }
            }
        }
        return image
    }

    private fun removeXSeam(input: BufferedImage, seam: List<Pixel>): BufferedImage {
        val image = BufferedImage(input.width, input.height - 1 , BufferedImage.TYPE_INT_RGB)
        (0 until input.width).forEach { x ->
            var newY = 0
            (0 until input.height).forEach { y ->
                if (seam.none { it.x == x && it.y == y }) {
                    image.setRGB(x, newY, input.getRGB(x, y))
                    newY++
                }
            }
        }
        return image
    }

    fun paintXSeam(input: BufferedImage): RenderedImage {
        val seam = generateSeam(input, true)
        return paintSeam(input, seam)
    }

    fun paintYSeam(input: BufferedImage): RenderedImage {
        val seam = generateSeam(input, false)
        return paintSeam(input, seam)
    }

    private fun paintSeam(input: BufferedImage, seam: List<Pixel>): BufferedImage {
        println("Painting seam...")
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

    class Pixel(val x: Int, val y: Int, val sumE: Double)

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
    }
}
