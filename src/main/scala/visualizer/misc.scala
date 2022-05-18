package visualizer

import java.awt.image.BufferedImage
import java.awt.Point
import java.awt.Robot
import java.awt.Graphics
object misc {
  private def frame = VisualizerApp.frame
  val emptyCursor = frame
    .getToolkit()
    .createCustomCursor(
      frame
        .getGraphicsConfiguration()
        .createCompatibleImage(16, 16, BufferedImage.TYPE_INT_ARGB),
      new Point(0, 0),
      "empty cursor"
    )
  val robot = new Robot()

  /** Class for measuring time Wall time
    */
  def timeNanos() = System.nanoTime()

  def timeBetween(start: Long, end: Long) = (end - start) / 1000000000.0

  def drawCrosshair(g: Graphics) = {
    val w = GfxMath.screenWidth
    val h = GfxMath.screenHeight
    g.drawLine(w / 2, h / 2 + 10, w / 2, h / 2 - 10)
    g.drawLine(w / 2 + 10, h / 2, w / 2 - 10, h / 2)
  }
}
