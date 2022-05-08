package visualizer

import java.awt.Graphics2D
import java.awt.Color._
import java.awt.TexturePaint
import java.awt.image.BufferedImage
import GfxMath._
import java.awt.Color
import scala.collection.mutable.Buffer
import java.awt.Rectangle
import java.awt.Dimension
trait Shapes {
  def draw(g: Graphics2D): Unit
}

class Triangle(val pos1: Pos, val pos2: Pos, val pos3: Pos) extends Shapes {

  def draw(g: Graphics2D) = {
    g.drawLine(pos1.x.toInt, pos1.y.toInt, pos2.x.toInt, pos2.y.toInt)
    g.drawLine(pos2.x.toInt, pos2.y.toInt, pos3.x.toInt, pos3.y.toInt)
    g.drawLine(pos3.x.toInt, pos3.y.toInt, pos1.x.toInt, pos1.y.toInt)
  }
  def draw(g: Graphics2D, color: Color) = {
    g.setColor(color)
    g.fillPolygon(
      Array[Int](pos1.x.toInt, pos2.x.toInt, pos3.x.toInt),
      Array[Int](pos1.y.toInt, pos2.y.toInt, pos3.y.toInt),
      3
    )
  }
  def draw(g: Graphics2D, texture:TexturePaint) = {
    g.setPaint(texture)
    g.fillPolygon(
      Array[Int](pos1.x.toInt, pos2.x.toInt, pos3.x.toInt),
      Array[Int](pos1.y.toInt, pos2.y.toInt, pos3.y.toInt),
      3
    )
  }
  override def toString(): String =
    pos1.toString + pos2.toString + pos3.toString()
}

object Triangle {
  def apply(pos1: Pos, pos2: Pos, pos3: Pos) = {
    new Triangle(pos1, pos2, pos3)
  }
  def apply(tri: Triangle) = {
    new Triangle(tri.pos1, tri.pos2, tri.pos3)
  }
}

class Wall(position: Pos, rotation: Pos) extends Shapes {
  val poses = Vector[Pos](
    Pos(-300, -200, -100),
    Pos(300, -200, -100),
    Pos(300, 200, -100),
    Pos(300, 200, 100),
    Pos(-300, 200, 100),
    Pos(-300, -200, 100),
    Pos(300, -200, 100),
    Pos(-300, 200, -100)
  )
  val triangles = Vector[Triangle](
    Triangle(poses(0), poses(7), poses(2)),
    Triangle(poses(0), poses(2), poses(1)),
    Triangle(poses(1), poses(2), poses(3)),
    Triangle(poses(1), poses(3), poses(6)),
    Triangle(poses(6), poses(3), poses(4)),
    Triangle(poses(6), poses(4), poses(5)),
    Triangle(poses(5), poses(4), poses(7)),
    Triangle(poses(5), poses(7), poses(0)),
    Triangle(poses(7), poses(4), poses(3)),
    Triangle(poses(7), poses(3), poses(2)),
    Triangle(poses(6), poses(5), poses(0)),
    Triangle(poses(6), poses(0), poses(1))
  )
  def worldSpace = {
    poses.map(pos =>
      pos
        .rotate(rotation)
        .translate(position)
        .translate(-Player.pos)
    )
  }
  def isInside(pos:Pos)={
    val worldSpace=this.worldSpace
    val bottomCorner=worldSpace(0)
    val topCorner=worldSpace(3)
    def isBetween(a:Double,b:Double,c:Double)= Math.min(a,b)-1 < c && Math.max(a,b)+1>c
    isBetween(bottomCorner.x,topCorner.x,pos.x)&&
    isBetween(bottomCorner.y,topCorner.y,pos.y)&&
    isBetween(bottomCorner.z,topCorner.z,pos.z)
  }
  def draw(g: Graphics2D) = {
    val newTriangles = Buffer[Triangle]()
    triangles
      .foreach(tri => {
        val worldSpaceTri = Triangle(
          tri.pos1
            .rotate(rotation)
            .translate(position)
            .translate(-Player.pos)
            .rotate(Player.camera),
          tri.pos2
            .rotate(rotation)
            .translate(position)
            .translate(-Player.pos)
            .rotate(Player.camera),
          tri.pos3
            .rotate(rotation)
            .translate(position)
            .translate(-Player.pos)
            .rotate(Player.camera)
        )
        val clippedTriangles = calcClipping(worldSpaceTri)
        clippedTriangles.foreach(n =>
          newTriangles +=
            Triangle(
              n.pos1
                .perspective()
                .center(),
              n.pos2
                .perspective()
                .center(),
              n.pos3
                .perspective()
                .center()
            )
        )
        // Triangle(
        //   center(perspective(rotate(translatePos(translatePos(rotate(tri.pos1,rotation),position),-Player.pos),Player.camera))),
        //   center(perspective(rotate(translatePos(translatePos(rotate(tri.pos2,rotation),position),-Player.pos),Player.camera))),
        //   center(perspective(rotate(translatePos(translatePos(rotate(tri.pos3,rotation),position),-Player.pos),Player.camera)))
        // )
      })
    newTriangles.foreach(n => {
      val normal = getNormal(n)
      if (getNormal(n).z < 0) {
        n.draw(g)
      }
    })
  }
}