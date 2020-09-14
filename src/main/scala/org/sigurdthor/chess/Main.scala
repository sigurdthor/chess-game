package org.sigurdthor.chess

import org.sigurdthor.chess.model.{Board, Move, errors}
import org.sigurdthor.chess.services.InputReader
import org.sigurdthor.chess.services.InputReader._
import org.sigurdthor.chess.services.MoveProcessorLive._
import zio._
import zio.console._

object Main extends App {

  def run(args: List[String]) = gameLogic.provideCustomLayer(Console.live ++ InputReader.live).exitCode

  def gameLogic = {
    val result = for {
      board <- IO.succeed(Board.create)
      _ <- putStrLn("Game is being started")
      updatedBoard <- play()(board)
    } yield updatedBoard

    result.fold(f => println(f.message), b => println(Board.print(b)))
  }

  def play(previousMove: Option[Move] = None)(implicit board: Board): ZIO[Console with InputReader, errors.GameFailure, Board] = {
    for {
      nextMove <- readMove
      _ <- putStrLn(nextMove.toString)
      currentBoardState <- performMove(previousMove, nextMove)
      _ <- putStrLn(Board.print(currentBoardState))
      _ <- play(Some(nextMove))(currentBoardState)
    } yield currentBoardState
  }
}
