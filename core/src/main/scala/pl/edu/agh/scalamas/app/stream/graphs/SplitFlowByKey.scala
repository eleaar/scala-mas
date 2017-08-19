/*
 * Copyright (c) 2013 Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.edu.agh.scalamas.app.stream.graphs

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition}

object SplitFlowByKey {

  /**
   * This flow will pass elements through the subflow corresponding to their key
   */
  def apply[A, K](groupKey: A => K, subFlows: Map[K, Flow[A, A, NotUsed]]): Flow[A, A, NotUsed] = {
    Flow.fromGraph(
      GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._

        val keys: Map[K, Int] = subFlows.keys.zipWithIndex.map {
          case (key, i) => key -> i
        }.toMap

        val partition = b.add(Partition[A](keys.size, groupKey andThen keys))
        val merge = b.add(Merge[A](keys.size))

        keys.foreach { case (key, i) =>
          partition.out(i) ~> subFlows(key) ~> merge.in(i)
        }

        FlowShape(partition.in, merge.out)
      })
  }

}
