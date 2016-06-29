/*******************************************************************************
  * Copyright 2016 Efe Kahraman
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *******************************************************************************/

package cli4s.getopt

import org.scalamock.scalatest.MockFactory
import org.scalatest._

trait GetOptBehaviours extends MockFactory { this: FlatSpec =>

  def inSequenceCaller(tokens: Array[Token], args: String): Unit = {
    assert(tokens.size == 2)
    val callback = mockFunction[Token, Unit]
    inSequence {
      callback expects tokens(0) once()
      callback expects tokens(1) once
    }

    new GetOpt(args.split(" "), tokens.map(t => TokenItem(t, false)).toList).iterate(callback)
  }

  def valueSetter(token: Token, value: Any, args: String): Unit = {
    def callback(token: Token): Unit = {
      assert(token.value == value)
      assert(token.index == 0)
    }

    new GetOpt(args.split(" "), List(TokenItem(token, true))).iterate(callback)
  }
}

class GetOptBehaviourSpec extends FlatSpec with GetOptBehaviours {

  "GetOpt (for short options)" should behave like inSequenceCaller(Array(ShortOption('s'), ShortOption('o')), "-s -o")
  "GetOpt (for short options)" should behave like inSequenceCaller(Array(ShortOption('s'), ShortOption('o')), "-so")

  "GetOpt (for short options)" should behave like valueSetter(ShortOption('s'), "value", "-svalue")
  "GetOpt (for long options)" should behave like valueSetter(LongOption("long"), "value", "--long=value")

}


