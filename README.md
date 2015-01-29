# scala-mas

[![Build Status](https://travis-ci.org/ParaPhraseAGH/scala-mas.svg)](https://travis-ci.org/ParaPhraseAGH/scala-mas)

A Lightweight parallel Multi-Agent System library in Scala.
The goal of this project is to help creating highly-concurrent multi-agent systems targeted at massively multicore hardware.

This project allows to design a computationally-intensive multi-agent system decoupled from the runtime environment.
Then, different runtime components can be chosen to run the simulation in the most efficient settings for a given hardware.

### Motivation

Writing a concurrent application is hard for most programmers. Writing a concurrent agent simulation is even harder.
Moreover, most of the existing software tighly couples the agent programming model to the underlying execution model and parallelism type.

We want programmers to be able to design a multi-agent system at a high level which abstracts of the actual execution model.
Then, such high level multi-agent patterns could be mapped to match a specific hardware by using the most adequate execution model for that hardware.

As a result, multi-agent simulations and computations could be easily designed and tested and the same design could then be scaled out along with
additional resources to solve harder problems or run bigger simulations.

## Instalation

You can find published releases on Maven Central.

#### Project structure

The project is structures into the following modules:

- **core**- APIs for designing a MAS, as well as two execution backends: a synchronous and asynchronous one.
- **emas** - A example MAS application for evolutionary optimization.
- **genetic** - Genetic operators' APIs for evolutionary optimization.
- **examples** - An example of how to compose and run an EMAS application.

In order to grab all the modules, add the following lines to your build configuration:

#### Sbt
    resolvers += Resolver.sonatypeRepo("snapshots")

    libraryDependencies += "pl.edu.agh.scalamas" %% "emas" % "0.1-SNAPSHOT"

#### Maven
    <dependency>
        <groupId>pl.edu.agh.scalamas</groupId>
        <artifactId>emas</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

You can also choose to import only the modules you really need.

## Getting started

Create an object EmasApp, mix-in a stack, emas logic and some genetic operators. Start the application by calling the run
 method. Or copy paste the following code:

    import pl.edu.agh.scalamas.app.{ConcurrentStack, SynchronousEnvironment}
    import pl.edu.agh.scalamas.emas.EmasLogic
    import pl.edu.agh.scalamas.genetic.RastriginProblem

    import scala.concurrent.duration._

    object EmasApp extends ConcurrentStack("emas") with SynchronousEnvironment
      with EmasLogic
      with RastriginProblem {

      def main(args: Array[String]) {
        run(5 seconds)
      }

    }

Next, see how to [choose an application stack](#choosing-an-application-stack), write your [own genetic operators](#writing-custom-genetic-operators)
or [customize the multi-agent system](#custom-multi-agent-systems).

## Configuration

Scala-mas uses the typesafe config library for configuration. If you want to add some custom configuration or override scala-mas default,
simply add a **application.conf** file with your settings to the classpath.

The default configuration is documented [here](CONFIG.md)

## Usage

Scala-mas uses the Cake Pattern to statically compose a runtime application. You will need two main components: an application stack
and some agent logic. See [here](#choosing-an-application-stack) for choosing the application stack best suited for your runtime environment.
The stacks also have a run method to start the simulation, with a duration parameter to tell when it should stop.

Scala-mas provides one agent logic implementation as example and primary use-case: Evolutionary Multi-agent Systems (EMAS).
EMAS is an asynchronous evolutionary algorithm used for optimisation. The default implementation will require you to provide yet
another component representing the genetic operators to use in optimisation. To write your own operators, see [here](#writing-custom-genetic-operators).
To customize the EMAS logic, see [here](#customizing-the-emas-logic).

If you want to implement your own multi-agent simulation, see [here](#custom-multi-agent-systems).

### Choosing an application stack

In order to compose your application, you first need to choose an application stack.
THe stack will provide the other components of your apps with:

- runtime configuration
- random number generation
- statistics gathering

Be sure to use the random number generator provided by the application stack. Otherwise, your application may run into trouble,
for example math.random is actually synchronised and may quickly become a bottleneck in a concurrent runtime scenario.

The following application stack are available:

- SequentialStack - A sequential, synchronized stack for useful for development and debugging
- ConcurrentStack with SynchronousEnvironment - A concurrent stack with coarse-grained agent concurrency, suited for small numbers of agents or small numbers of cores.
- ConcurrentStack with AsynchronousEnvironment  - A concurrent stack with fine-grained agent concurrency, suited for large numbers of agents
running on large numbers of cores

### Evolutionary Multi-Agent Systems

#### Writing custom genetic operators

In order to use the EMAS app, you need to mix-in some logic responsible for carrying the genetic operations of the underlying evolutionary algorithm.
In particular, you need to define:

- The type of solutions to be optimised
- A function to generate initial solutions
- A function to evaluate existing solutions and an ordering for these evaluations (to tell which evaluation is better)
- An unary and binary function to generate new solutions out of existing ones.

This functionality is encapsulated in the GeneticOps, GeneticEvaluator and GeneticTransformer traits.
These traits all take a recursive type parameter to achieve family polimorphism, i.e. to define a set of implementations which can be used together.
You can implement all these traits in a single class, or compose partial implementations using the Cake Pattern.

This EMAS implementation **maximises** the objective function defined by the genetic operators. If you want to minimize instead, just reverse the evaluatiions' ordering.

#### Customizing the EMAS logic

The default EMAS implementation repeatedly group together agents with similar levels of energy. Low energy agents fight, high energy agents reproduce.
By default, low-energy agents fight by comparing the evaluation of their solutions, winner taking energy from losers.
High-energy agents reproduce using the genetic operators and share some energy with their children.
You can customize these strategies by mixing in custom traits instead of the default ones.

You can also fully change the EMAS logic and implement your own multi-agent algorithm by changing the agents behaviours and meetings as described [here](#custom-multi-agent-systems).

### Custom Multi-Agent Systems


## Contributing

If you have any suggestions or want to contribute, feel free to open a new ticket or create a pull request.

## License

This project is distributed under the MIT License.