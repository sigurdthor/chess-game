package org.sigurdthor.chess

import zio.Has

package object services {
  type InputReader = Has[InputReader.Service]
}
