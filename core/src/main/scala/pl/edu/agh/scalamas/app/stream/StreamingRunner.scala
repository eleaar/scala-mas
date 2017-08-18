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

package pl.edu.agh.scalamas.app.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.pattern.after
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, MergePreferred, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape, Materializer}
import pl.edu.agh.scalamas.app.ConcurrentAgentRuntimeComponent
import pl.edu.agh.scalamas.stats.StatsComponent

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object LoopingGraph {
  def apply[T](source: Source[T, NotUsed], flow: Flow[T, T, NotUsed]): RunnableGraph[NotUsed] = {
    RunnableGraph.fromGraph(
      GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._

        val merge = b.add(MergePreferred[T](1).async)
        val bcast = b.add(Broadcast[T](2).async)

        source ~> merge ~> flow ~> bcast ~> Sink.ignore
        merge.preferred <~ bcast

        ClosedShape
      }
    )
  }
}

object ElapsedTimeSource {
  def apply(interval: FiniteDuration): Source[Long, NotUsed] = {
    Source.lazily(() => Source.tick(0.seconds, 1.second, System.currentTimeMillis()))
      .mapMaterializedValue(_ => NotUsed)
      .map(initialTime => System.currentTimeMillis() - initialTime)
  }
}

trait StreamingRunner[T] { this: ConcurrentAgentRuntimeComponent
  with StreamingLoopStrategy[T]
  with StatsComponent =>

  def run(duration: FiniteDuration): Unit = {
    implicit val actorSystem: ActorSystem = agentRuntime.system
    implicit val ec: ExecutionContext = actorSystem.dispatcher
    implicit val materializer: Materializer = ActorMaterializer()

    val log = Logging(actorSystem, getClass)

    LoopingGraph(initialSource, stepFlow).run()
    ElapsedTimeSource(interval = 1.second).runForeach { time =>
      log.info(s"$time ${formatter(stats.getNow)}")
    }

    for {
      _ <- after(duration, actorSystem.scheduler)(Future.successful(()))
      _ <- stats.get
      _ <- actorSystem.terminate()
    } yield ()
  }
}

