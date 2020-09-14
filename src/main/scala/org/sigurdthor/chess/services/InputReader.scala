package org.sigurdthor.chess.services

import com.whitehatgaming.{UserInput, UserInputFile}
import org.sigurdthor.chess.GameIO
import org.sigurdthor.chess.model.{Move, Position}
import org.sigurdthor.chess.model.errors.MovesExceeded
import zio.{Layer, ZIO, ZLayer}


object InputReader {

  trait Service {
    def readMove: GameIO[Move]
  }

  val live: Layer[Nothing, InputReader] = ZLayer.succeed(

    new Service {
      val reader: UserInput = new UserInputFile("data/sample-moves.txt")

      override def readMove: GameIO[Move] =
        ZIO.fromOption(Option(reader.nextMove()))
          .bimap(_ => MovesExceeded,
            pos => Move(Position(pos(0), pos(1)), Position(pos(2), pos(3))))
    }
  )

  def readMove: GameIO[Move] =
    ZIO.accessM[InputReader](_.get.readMove)
}