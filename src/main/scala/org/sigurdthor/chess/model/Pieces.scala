package org.sigurdthor.chess.model

import org.sigurdthor.chess.GameIO
import org.sigurdthor.chess.model.Direction._
import org.sigurdthor.chess.model.errors.InvalidMove
import org.sigurdthor.chess.model.utils.DirectionCheckedPieceValidator
import zio.IO

object Pieces {

  sealed trait Color

  case object Black extends Color

  case object White extends Color

  sealed trait PieceType {

    val character: Char

    def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean
  }

  sealed trait DirectionCheckedPiece extends PieceType with DirectionCheckedPieceValidator

  case object King extends PieceType {

    override val character: Char = 'K'

    val kingMaxDistance = 1

    override def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean =
      src.coord.distance(dst.coord).contains(kingMaxDistance)
  }

  case object Rook extends DirectionCheckedPiece {

    override val character: Char = 'R'

    val directions: Set[Direction] = Set(Top, Bottom, Direction.Left, Direction.Right)

    override def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean =
      isValidMove(src.coord, dst.coord)
  }

  case object Queen extends DirectionCheckedPiece {

    override val character: Char = 'Q'

    val directions: Set[Direction] = Set(Direction.Left, Top, Direction.Right, Bottom, TopLeft, BottomLeft, TopRight, BottomRight)

    override def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean = isValidMove(src.coord, dst.coord)
  }

  case object Knight extends PieceType {

    override val character: Char = 'N'

    override def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean = {
      val path = Move.path(src.coord, dst.coord)

      (Math.abs(path._1), Math.abs(path._2)) match {
        case (2, 1) => true
        case (1, 2) => true
        case _ => false
      }
    }
  }

  case object Bishop extends DirectionCheckedPiece {

    override val character: Char = 'B'

    val directions: Set[Direction] = Set(TopLeft, BottomLeft, TopRight, BottomRight)

    override def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean = isValidMove(src.coord, dst.coord)
  }

  case object Pawn extends PieceType {

    override val character: Char = 'P'

    override def validate(src: Cell, dst: Cell)(implicit board: Board): Boolean = {

      def pawnStartRank(color: Option[Color]) = color match {
        case Some(Black) => 1
        case Some(White) => 6
      }

      val offset = src.piece.map(_.color) match {
        case Some(White) => 1
        case Some(Black) => -1
      }

      Move.path(dst.coord, src.coord) match {
        case (0, y) if src.coord.rank == pawnStartRank(src.piece.map(_.color)) => y == 2 * offset
        case (0, _) => dst.piece.isEmpty
        case (x, y) if dst.piece.isDefined => Math.abs(x) == 1 && y == offset
        case _ => false
      }


    }
  }

  case class Piece(pieceType: PieceType, color: Color) {

    val character: Char = color match {
      case Black => pieceType.character.toLower
      case White => pieceType.character.toUpper
    }

    def validate(src: Cell, dst: Cell)(implicit board: Board): GameIO[Board] = {
      (src.piece, dst.piece) match {
        case (Some(srcPiece), Some(dstPiece)) if srcPiece.color == dstPiece.color => IO.fail(InvalidMove(src, dst))
        case (Some(srcPiece), _) => if (srcPiece.pieceType.validate(src, dst)) IO.succeed(board) else IO.fail(InvalidMove(src, dst))
      }
    }
  }

}
