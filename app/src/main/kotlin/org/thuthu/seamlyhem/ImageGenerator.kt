package org.thuthu.seamlyhem

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage

class ImageGenerator {

    fun redCross(w: Int, h: Int): RenderedImage {

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
}
