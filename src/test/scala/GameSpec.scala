
import org.sigurdthor.chess.model.Pieces._
import org.sigurdthor.chess.model.{Board, Position}
import org.sigurdthor.chess.services.InputReader._
import org.sigurdthor.chess.services.MoveProcessorLive._
import mocks.InputReaderMock
import zio.test.Assertion._
import zio.test.{testM, _}

object GameSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("GameSpec")(

    testM("Perform pawn move") {
      implicit val board = Board.create
      val result = for {
        nextMove <- readMove
        updatedBoard <- performMove(None, nextMove)
      } yield {
        assert(updatedBoard.find(_.coord == Position(4, 4)).flatMap(_.piece))(equalTo(Some(Piece(Pawn, White))))
      }
      result.provideCustomLayer(InputReaderMock.test)
    }
    /*testM("Perform knight move") {
      implicit val board = Board.create
      val result = for {
        nextMove <- readMove
        updatedBoard <- performMove(Some(Move(Position(4,6), Position(4,4))), nextMove)
      } yield {
        assert(updatedBoard.find(_.coord == Position(5, 2)).flatMap(_.piece))(equalTo(Some(Piece(Knight, Black))))
      }
      result.provideCustomLayer(InputReaderTest.test)
    }*/
  )
}
