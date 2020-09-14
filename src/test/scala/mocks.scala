import org.sigurdthor.chess.GameIO
import org.sigurdthor.chess.model.errors.MovesExceeded
import org.sigurdthor.chess.model.{Move, Position}
import org.sigurdthor.chess.services.InputReader
import org.sigurdthor.chess.services.InputReader.Service
import zio.{Layer, ZIO, ZLayer}

import scala.collection.mutable

object mocks {

  object InputReaderMock {

    val test: Layer[Nothing, InputReader] = ZLayer.succeed(
      new Service {

        val moves = mutable.Stack(Move(Position(4, 6), Position(4, 4)), Move(Position(6, 0), Position(5, 2)))

        override def readMove: GameIO[Move] = if (moves.nonEmpty) ZIO.succeed(moves.pop()) else ZIO.fail(MovesExceeded)
      }
    )
  }

}
