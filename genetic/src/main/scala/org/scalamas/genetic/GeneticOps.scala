package org.scalamas.genetic

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


