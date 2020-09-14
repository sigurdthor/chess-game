package org.sigurdthor.chess.services

import org.sigurdthor.chess.GameIO
import org.sigurdthor.chess.model.{Board, Cell, Move}
import org.sigurdthor.chess.model.errors.{CellWithoutPiece, FoesTurn, UnknownCell}
import zio.IO

trait MoveProcessor {
  def performMove(previousMove: Option[Move], move: Move)(implicit board: Board): GameIO[Board]
}

object MoveProcessorLive extends MoveProcessor {

  override def performMove(previousMove: Option[Move], move: Move)(implicit board: Board): GameIO[Board] = {
    val maybeSrc = Board.findCell(board, move.src)
    val maybeDst = Board.findCell(board, move.dst)
    val maybePrevPiece = previousMove.flatMap(prev => Board.findCell(board, prev.dst).flatMap(_.piece))

    (maybeSrc, maybeDst) match {
      case (Some(src), Some(dst)) =>
        src.piece match {
          case Some(piece) if maybePrevPiece.exists(_.color == piece.color) => IO.fail(FoesTurn)
          case Some(piece) => piece.validate(src, dst) *> updateBoard(board, src, dst)
          case _ => IO.fail(CellWithoutPiece(src))
        }
      case _ => IO.fail(UnknownCell)
    }
  }

  private def updateBoard(board: Board, src: Cell, dst: Cell): GameIO[Board] =
    IO.succeed(Set(Cell(src.coord, None), Cell(dst.coord, src.piece)) | board)
}
