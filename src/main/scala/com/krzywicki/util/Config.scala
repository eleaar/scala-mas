package com.krzywicki.util

class Config(
  val problemSize: Int) {

  def populationSize = 100
  def initialEnergy = 10
  def reproductionThreshold = 11
  def reproductionTransfer = 5
  def fightTransfer = 10

  def mutationRate = 0.1
  def mutationRange = 0.05
  def mutationChance = 0.75
  def recombinationChance = 0.3
  def migrationProbability = 0.0001

}