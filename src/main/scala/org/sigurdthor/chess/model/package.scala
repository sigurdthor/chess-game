package org.sigurdthor.chess

import org.sigurdthor.chess.model.Direction.{Bottom, Top}
import org.sigurdthor.chess.model.Pieces._

import scala.collection.immutable.HashSet

package object model {

  type File = Int
  type Rank = Int
  type Path = (Int, Int)
  type Shift = (Int, Int)
  type Board = Set[Cell]

  case class Cell(coord: Position, piece: Option[Piece]) {

    override def equals(o: Any): Boolean = o match {
      case Cell(`coord`, _) => true
      case _ => false
    }

    override def hashCode: Int = coord.##
  }

  case class Position(file: File, rank: Rank) {

    def distance(position: Position): Option[Int] = {
      Move.path(this, position) match {
        case (x, y) if Math.abs(x) == Math.abs(y) => Option(Math.abs(x))
        case (0, y) => Option(Math.abs(y))
        case (x, 0) => Option(Math.abs(x))
        case _ => None
      }
    }
  }

  object Move {
    def path(startPos: Position, endPos: Position): Path = (endPos.file - startPos.file, endPos.rank - startPos.rank)

    def direction(path: Path): Option[Direction] = {
      path match {
        case (0, y) if y != 0 => if (y > 0) Option(Top) else Option(Bottom)

        case (x, 0) if x != 0 => if (x > 0) Option(Direction.Right) else Option(Direction.Left)

        case (x, y) if Math.abs(x) == Math.abs(y) =>
          (x / Math.abs(x), y / Math.abs(y)) match {
            case Direction.TopRight.shift => Option(Direction.TopRight)
            case Direction.BottomRight.shift => Option(Direction.BottomRight)
            case Direction.BottomLeft.shift => Option(Direction.BottomLeft)
            case Direction.TopLeft.shift => Option(Direction.TopLeft)
            case _ => None
          }

        case _ => None
      }
    }
  }

  case class Move(src: Position, dst: Position) {
    override def toString: String = s"Performing move from $src to $dst"
  }

  sealed trait Direction {
    val shift: Shift
  }

  object Direction {

    case object Top extends Direction {
      val shift = (0, 1)
    }

    case object TopRight extends Direction {
      val shift = (1, 1)
    }

    case object Right extends Direction {
      val shift = (1, 0)
    }

    case object BottomRight extends Direction {
      val shift = (1, -1)
    }

    case object Bottom extends Direction {
      val shift = (0, -1)
    }

    case object BottomLeft extends Direction {
      val shift = (-1, -1)
    }

    case object Left extends Direction {
      val shift = (-1, 0)
    }

    case object TopLeft extends Direction {
      val shift = (-1, 1)
    }

  }

  object Board {

    def create: Board =
      fillRow(0, Black) ++
        (0 to 7).map(f => Cell(Position(f, 1), Some(Piece(Pawn, Black)))).toSet ++
        (2 to 5).flatMap(r => (0 to 7).map(f => Cell(Position(f, r), None))).toSet ++
        (0 to 7).map(f => Cell(Position(f, 6), Some(Piece(Pawn, White)))).toSet ++
        fillRow(7, White)

    def findCell(board: Board, position: Position): Option[model.Cell] = board.find(_.coord == position)

    private def fillRow(index: Int, color: Color) = HashSet(
      Cell(Position(0, index), Some(Piece(Rook, color))),
      Cell(Position(1, index), Some(Piece(Knight, color))),
      Cell(Position(2, index), Some(Piece(Bishop, color))),
      Cell(Position(3, index), Some(Piece(Queen, color))),
      Cell(Position(4, index), Some(Piece(King, color))),
      Cell(Position(5, index), Some(Piece(Bishop, color))),
      Cell(Position(6, index), Some(Piece(Knight, color))),
      Cell(Position(7, index), Some(Piece(Rook, color)))
    )

    def print(board: Board): String = {
      val filesRow = """    a   b   c   d   e   f   g   h"""
      val separator = """  +---+---+---+---+---+---+---+---+"""

      filesRow + "\n" + separator + "\n" + (0 to 7).map(rank => {
        val line = "%d |".format(8 - rank) +
          (0 to 7).map(file => " %s |".format(
            board.find(_.coord == Position(file, rank)).flatMap(_.piece) match {
              case Some(piece) => piece.character
              case None => " "
            })).reduceLeft(_ + _) + " %d".format(8 - rank)

        line + "\n" + separator + "\n"
      }).mkString("\n") + filesRow
    }
  }

}
