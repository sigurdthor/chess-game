package org.sigurdthor.chess.model

object errors {

  sealed trait GameFailure {
    val message: String
  }

  case class InvalidMove(src: Cell, dst: Cell) extends GameFailure {
    override val message: String = s"Invalid move ${src.coord} - ${dst.coord}"
  }

  case object FoesTurn extends GameFailure {
    override val message: String = s"Opponent's turn!!!"
  }

  case object MovesExceeded extends GameFailure {
    override val message: String = "No moves to read. Game is finished"
  }

  case object UnknownCell extends GameFailure {
    override val message: String = "Unknown cell"
  }

  case class CellWithoutPiece(cell: Cell) extends GameFailure {
    override val message: String = s"Cell ${cell.coord} doesn't contain piece"
  }

}
