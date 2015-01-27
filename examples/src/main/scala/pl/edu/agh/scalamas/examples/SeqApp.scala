/*
 * Copyright 2013 - 2015, Daniel Krzywicki <daniel.krzywicki@agh.edu.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.edu.agh.scalamas.examples

object SeqApp

//object SeqApp extends RastriginConfig {
//
//  def main(args: Array[String]) {
//    val c = ConfigFactory.load()
//    implicit val config = new AppConfig(c)
//    val stats = Stats.simple((-10000.0, 0L)) {
//      case ((oldFitness, oldReps), (newFitness, newReps)) => (math.max(oldFitness, newFitness), oldReps + newReps)
//    }
//
//    val deadline = time fromNow
//    val startTime = System.currentTimeMillis()
//    var logDeadline = 1 second fromNow
//    def log() = {
//      val time = System.currentTimeMillis() - startTime
//      val (fitness, reproductions) = stats.getNow
//      println(s"fitness $time $fitness")
//      println(s"reproductions $time $reproductions")
//    }
//    log()
//
//    val logic = new EmasLogic[RastriginProblem](ops(c.getConfig("genetic")), stats, config)
//    import logic._
//
//    val islandsNumber = config.emas.islandsNumber
//    var islands = Array.fill(islandsNumber)(initialPopulation)
//
//    while (deadline.hasTimeLeft) {
//      val migrators = ArrayBuffer.empty[Agent]
//      def migration: MeetingFunction = {
//        case (Migration(_), agents) =>
//          migrators ++= agents
//          List.empty[Agent]
//      }
//
//      islands = islands.map(island => island.groupBy(behaviourFunction).flatMap(migration orElse meetingsFunction).toList)
//
//      migrators.foreach {
//        agent =>
//          val destination = Random.nextInt(islandsNumber)
//          islands(destination) ::= agent
//      }
//
//      if(logDeadline.isOverdue()){
//        log()
//        logDeadline = 1 second fromNow
//      }
//    }
//
//
//  }
//
//
//}