package org.sigurdthor.chess.model

import scala.annotation.tailrec

object utils {

  trait DirectionCheckedPieceValidator extends DirectionsChecker with PiecesScanner {

    def isValidMove(src: Position, dst: Position)(implicit board: Board): Boolean = {
      val path = Move.path(src, dst)
      isDirectionAllowed(path) && hasPieceInPath(src, dst, path)
    }
  }

  trait DirectionsChecker {

    def directions: Set[Direction]

    def isDirectionAllowed(path: Path): Boolean = {
      Move.direction(path) match {
        case Some(dir) => directions.contains(dir)
        case _ => false
      }
    }
  }

  trait PiecesScanner {

    def hasPieceInPath(src: Position, dst: Position, path: Path)(implicit board: Board): Boolean = {

      @tailrec def traverse(shift: Shift, position: Position): Boolean = {
        val currentPosition = Position(position.rank + shift._1, position.file + shift._2)
        board.find(_.coord == currentPosition) match {
          case Some(cell) if cell.piece.isEmpty && cell.coord != dst => traverse(shift, currentPosition)
          case Some(cell) if cell.coord == dst => false
          case _ => true
        }
      }

      Move.direction(path).exists(direction => traverse(direction.shift, src))
    }
  }

}
