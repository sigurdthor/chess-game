package org.sigurdthor

import org.sigurdthor.chess.model.errors.GameFailure
import org.sigurdthor.chess.services.InputReader
import zio.ZIO

package object chess {
  type GameIO[A] = ZIO[InputReader, GameFailure, A]
}
