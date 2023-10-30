package org.thuthu.seamlyhem

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage

class ImageGenerator {

    fun redCross(w: Int, h: Int): RenderedImage {
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

    fun invert(input: BufferedImage): RenderedImage {
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

    companion object {
        private const val MAX_RGB: Int = 0xFFFFFF
    }
}
