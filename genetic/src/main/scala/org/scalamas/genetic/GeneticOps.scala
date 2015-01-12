package org.scalamas.genetic

/**
 * Polymorphic family of genetic operators suited for a particular solution representation.
 *
 * Implementations should specify the type of the solution and provide corresponding GeneticEvaluator
 * and GeneticTransformer operators.
 *
 * === Basic example ===
 * {{{
 *  trait MyGeneticOps extends GeneticOps[MyGeneticOps] {
 *   type Solution = MySolutionType
 *   type Evaluation = MyEvaluationType
 *
 *   def generate: MySolutionType = ...
 *   def evaluate(solution: MySolutionType): MyEvaluationType = ...
 *   def ordering: Ordering[MyEvaluationType] = ...
 *
 *   def transform(solution: MySolutionType) = ...
 *   def transform(solution1: MySolutionType, solution2: MySolutionType) = ...
 *  }
 * }}}
 *
 * === Reusing operator components ===
 *
 * You can use the cake pattern to compose different operators for a given representation, as in:
 *
 * {{{
 *  trait MyRepresentation extends GeneticOps[MyRepresentation] {
 *   type Solution = MySolutionType
 *  }
 *
 *  trait MyEvaluator extends GeneticEvaluator[MyRepresentation] {
 *   ...
 *  }
 *
 *  trait MyTransformer1 extends GeneticTransformer[MyRepresentation] {
 *   ...
 *  }
 *
 *  trait MyTransformer2 extends GeneticTransformer[MyRepresentation] {
 *   ...
 *  }
 *
 *  trait MyConcreteOps1 extends MyRepresentation with MyEvaluator with MyTransformer1
 *
 *  trait MyConcreteOps2 extends MyRepresentation with MyEvaluator with MyTransformer2
 * }}}
 *
 * @tparam G recursive parameter type representing the polymorphic family of operators
 */
trait GeneticOps[G <: GeneticOps[G]] extends GeneticEvaluator[G] with GeneticTransformer[G] {
  type Solution
}

/**
 * Evaluates solutions and generates initial ones for futher optimization.
 * This operator is usually-problem specific.
 *
 * @tparam G recursive parameter type representing the polymorphic family of operators
 */
trait GeneticEvaluator[G <: GeneticOps[G]] {
  type Evaluation

  /**
   * Generates an initial solution for further optimization.
   * @return an initial solution
   */
  def generate: G#Solution

  /**
   * Evaluates given solutions.
   *
   * @param solution the solution to be evaluated
   * @return the solution's evaluation
   */
  def evaluate(solution: G#Solution): G#Evaluation

  /**
   * Provides an for evaluations to decide which is better.
   * @return the evaluations ordering
   */
  def ordering: Ordering[G#Evaluation]
}

/**
 * Binary and unary operators to transform existing solutions into new ones, using e.g. mutation, recombination.
 *
 * @tparam G recursive parameter type representing the polymorphic family of operators
 */
trait GeneticTransformer[G <: GeneticOps[G]] {

  /**
   * An unary transformation operator. Created a new solution out of an existing one (e.g. using mutation or local search).
   *
   * @param solution the solution to be transformed
   * @return a new, transformed solution
   */
  def transform(solution: G#Solution): G#Solution

  /**
   * An binary transformation operator. Created a new solution out of two existing ones (e.g. using crossover).
   *
   * @param solution1 the first solution to be transformed
   * @param solution2 the second solution to be transformed
   * @return a pair of new, transformed solutions
   */
  def transform(solution1: G#Solution, solution2: G#Solution): (G#Solution, G#Solution)
}


