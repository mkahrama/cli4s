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

sealed trait Token { var index: Int = 0; var value: Any = None }

case class ShortOption(name: Char) extends Token

case class LongOption(name: String) extends Token

case class Argument(name: String) extends Token

case class TokenItem(token: Token, hasValue: Boolean, expectedIndex: Option[Int] = None)

class InvalidOptionException(message: String, option: String = null) extends Exception(message)

class GetOpt(args: String, options: List[TokenItem]) {

  private val ShortOpPattern = """^(-)([\w])([\w-:_]+)?""".r

  private val LongOpPattern = """^(-{2})(\w[\w-]+)(=)?([\w-:_]+)?""".r

  private val ShortOpNoHyphenPattern = """^([\w])([\w-:_]+)?""".r

  private val TokenPattern = """([\w-=:_]+)( )?([\w-=:_ ]*)""".r

  private val OptionsMap: Map[Token, TokenItem] = options.map({case item => (item.token -> item)}).toMap

  private def checkIndex(token: Token, size: Int): Unit = {
    val itemOpt = OptionsMap.get(token)
    if (itemOpt.isEmpty)
      return

    val indexOpt = itemOpt.get.expectedIndex
    if (indexOpt.isEmpty)
      return

    val index = indexOpt.get
    if (index >=0  && index != token.index)
      throw new InvalidOptionException("wrong order")

    if (index < 0 && (size + index) != token.index)
      throw new InvalidOptionException("wrong order")

  }

  private def validateAndSet(token: Token, value: String, longOption: Boolean = false): List[Token] = {
    var list = List[Token](token)
    if (hasValue(token)) {
      if (null == value) {
        throw new InvalidOptionException("missing value")
      }
      token.asInstanceOf[Token].value = value
    } else if (value != null) {
      if (longOption)
        throw new InvalidOptionException("bad usage")

      val ShortOpNoHyphenPattern(first, rest) = value
      list ++= validateAndSet(ShortOption(first.charAt(0)), rest)
    }

    list
  }


  private def parse(args: String, ignoreOptions: Boolean = false): List[Token] = {
    val trimmedArgs = args.trim
    if (trimmedArgs.isEmpty)
      return List()

    val TokenPattern(head, _, tail) = trimmedArgs
    if (ignoreOptions)
      return List(Argument(head)) ::: parse(tail, true)

    if (isShortOptionWithValue(head)) {
      if (tail.isEmpty)
        throw new InvalidOptionException("bad usage")

      val TokenPattern(value, _, rest) = tail
      return identifyToken(head, value) ::: parse(rest)
    } else if (head == "--") {
      return parse(tail, true)
    } else
      return identifyToken(head) ::: parse(tail)

  }

  private def hasValue(token: Token): Boolean = {
    val itemOpt = OptionsMap.get(token)
    if (itemOpt.isEmpty)
      throw new InvalidOptionException("not defined")

    itemOpt.get.hasValue
  }

  private def isShortOptionWithValue(token: String): Boolean = token match {
    case ShortOpPattern(_, key, value) => hasValue(ShortOption(key.charAt(0))) && value == null
    case _ => false
  }

  private def identifyToken(token: String, suppliedValue: String = null): List[Token] = token match {
    case ShortOpPattern(_, key, value) =>
      validateAndSet(ShortOption(key.charAt(0)), if (value != null) value else suppliedValue)
    case LongOpPattern(_, key, _, value) => validateAndSet(LongOption(key), value, true)
    case _ => List(Argument(token))
  }

  def iterate(callback: (Token) => Unit) {
    val tokens = parse(args)
      .zipWithIndex.map({ case (token : Token, index) => token.index = index; token })
    tokens.foreach(token => checkIndex(token, tokens.size))
    tokens.foreach(callback)
  }

}

