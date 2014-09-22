package org.paramas.emas.genetic

/**
 * Created by Daniel on 2014-08-12.
 */
trait GeneticOps[G <: GeneticOps[G]] extends GeneticEvaluator[G] with GeneticTransformer[G] {
  type Solution
}

trait GeneticEvaluator[G <: GeneticOps[G]] {
  type Evaluation

  def generate: G#Solution
  def evaluate(solution: G#Solution): G#Evaluation
  def ordering: Ordering[G#Evaluation]
}

trait GeneticTransformer[G <: GeneticOps[G]] {
  def transform(solution: G#Solution): G#Solution
  def transform(solution1: G#Solution, solution2: G#Solution): (G#Solution, G#Solution)
}

trait RealValuedEncoding extends GeneticOps[RealValuedEncoding] {
  type Solution = Seq[Double]
}

//trait CopyTransformer extends GeneticTransformer[RealValuedEncoding] {
//  def transform(solution: Seq[Double]) = solution
//  def transform(solution1: Seq[Double], solution2: Seq[Double]) = (solution1, solution2)
//}
//
//trait RastriginProblem extends GeneticEvaluator[RealValuedEncoding] {
//  type Evaluation = Double
//  def problemSize: Int
//
//  def generate = List.fill(problemSize)(0)
//  def evaluate(solution: Seq[Double]) = 0.0
//  def ordering = scala.math.Ordering.Double
//}


object Test {
//  def foo = {
//    val ops =  new GeneticConfig(ConfigFactory.load()) with RastriginProblem with RealValuedEncoding with CopyTransformer
//    test(ops)
//  }

  def test[G <: GeneticOps[G]](ops: G) = {
    val solution = ops.generate
    ops.transform(solution)
  }

}


