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

import org.scalatest.FlatSpec

class GetOptErrorSpec extends FlatSpec {

  def emptyCallback(token: Token): Unit = {}

  "GetOpt" should "give an error when undefined option is passed" in {
    val items = List(TokenItem(ShortOption('s'), false))
    intercept[InvalidOptionException] {
      new GetOpt(Array("-s", "-x"), items).iterate(emptyCallback)
    }
  }

  it should "give an error when value not present" in {
    val items = List(TokenItem(ShortOption('s'), true))
    intercept[InvalidOptionException] {
      new GetOpt(Array("-s"), items).iterate(emptyCallback)
    }
  }

  it should "give an error when option is not at expected place" in {
    val items = List(TokenItem(ShortOption('s'), false), TokenItem(ShortOption('x'), false, Option(0)))
    intercept[InvalidOptionException] {
      new GetOpt(Array("-s", "-x"), items).iterate(emptyCallback)
    }
  }

}
