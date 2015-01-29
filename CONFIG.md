### Default configuration

These are scala-mas configuration defaults. You can override them by placing an appropriate **application.conf** file on your classpath.


    mas {
      # The number of separate agent environments to run
      islandsNumber = 12

      # The global seed for random number generation. Will default to System.currentTimeMillis() if unspecified.
      # Note that the same global seed will not guarantee repeatable results, as actor scheduling can be non-deterministic.
      # seed = 123456789
    }

    emas {
      # The initial number of agents in every island
      populationSize = 100

      # The initial energy of agents in the initial population
      initialEnergy = 10

      # The amount of energy to start reproducing
      reproductionThreshold = 11

      # The amount of energy transmitted from parent to children during reproduction
      reproductionTransfer = 5

      # The amount of energy transmitted from loser to winner during fight
      fightTransfer = 10

      # The probability for an agent to migrate to another island
      migrationProbability = 0.0001

      # The size of fight meetings
      fightCapacity = 2

      # The size of reproduction meetings
      reproductionCapacity = 2

      # The size of migration meetings
      migrationCapacity = 1

      # The size of death meetings
      deathCapacity = 1
    }

    genetic {
      rastrigin {
        # The dimention of the optimization problem
        problemSize = 100

        # The probability of mutating a solution
        mutationChance = 0.75

        # The probability of mutating a solution's feature
        mutationRate = 0.1

        # mutationRange = 0.05

        # The probability of recombining solutions
        recombinationChance = 0.3
      }

      labs {
        # The dimention of the optimization problem
        problemSize = 201

        # The probability of mutating a solution
        mutationChance = 0.75

        # The probability of mutating a solution's feature
        mutationRate = 0.1

        mutationRange = 0.05 # unused

        # The probability of recombining solutions
        recombinationChance = 1.0
      }
    }