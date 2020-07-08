package br.unirio.covid19.pooling.engine;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroup;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.Strategy;
import br.unirio.covid19.pooling.model.simulation.TestingSet;
import br.unirio.covid19.pooling.model.pooling.OneByOneModel;
import br.unirio.covid19.pooling.model.pooling.PoolingModel;
import br.unirio.covid19.pooling.utils.PseudoRandom;

public class Simulator 
{
    /**
     * Number of individuals under evaluation
     */
    private int individualCount;

    /**
     * List of individuals for the simulation
     */
    private List<Individual> individuals;

    /**
     * Probability of a false negative result in the test
     */
    private double falseNegativeProbability;
    
    /**
     * Probability of a false positive result in the test
     */
    private double falsePositiveProbability;

    /**
     * Initializes the simulator
     */
    public Simulator(int individualCount, double falseNegativeProbability, double falsePositiveProbability)
    {
        this.individualCount = individualCount;
        this.individuals = createIndividuals(individualCount);
        this.falseNegativeProbability = falseNegativeProbability;
        this.falsePositiveProbability = falsePositiveProbability;
    }

    /**
     * Create a list of individuals for the simulation
     */
    private List<Individual> createIndividuals(int individualCount)
    {
        List<Individual> sample = new ArrayList<Individual>();

        for (int i = 0; i < individualCount; i++)
        {
            Individual individual = new Individual(i+1);
            sample.add(individual);
        }

        return sample;
    }

    /**
     * Sets the positive probability for all individuals
     */
    public Simulator setPositiveProbability(double positiveProbability)
    {
        for (int i = 0; i < individualCount; i++)
        {
            Individual individual = individuals.get(i);
            individual.setPositiveProbability(positiveProbability);
        }

        return this;
    }

    /**
     * Sets the positive probability for an individual
     */
    public Simulator setPositiveProbability(int index, double positiveProbability)
    {
        Individual individual = individuals.get(index);
        individual.setPositiveProbability(positiveProbability);
        return this;
    }

    /**
     * Sets the positive probability for each individual
     */
    public Simulator loadPositiveProbability(double[] probability) 
    {
        for (int i = 0; i < individualCount; i++)
        {
            Individual individual = individuals.get(i);
            individual.setPositiveProbability(probability[i]);
        }

		return this;
	}

    /**
     * Loads a file with positive probabiilities for each individual
     */
    public Simulator loadPositiveProbability(String filename)
    {
        try
        {
            String contents = new String(Files.readAllBytes(Paths.get(filename)));
            String[] lines = contents.split("\n");

            for (int i = 0; i < lines.length; i++)
            {
                double positiveProbability = Double.parseDouble(lines[i]);
                Individual individual = individuals.get(i);
                individual.setPositiveProbability(positiveProbability);
            }
        }
        catch(Exception e)
        {
        }        

        return this;
    }

    /**
     * Returns the maximum positive probability among the individuals
     */
    public double calculateMaximumPositiveProbability()
    {
        double maxPositiveProbability = 0.0;

        for (int i = 0; i < individuals.size(); i++)
        {
            Individual individual = individuals.get(i);
            double positiveProbability = individual.getPositiveProbability();

            if (positiveProbability > maxPositiveProbability)
                maxPositiveProbability = positiveProbability;
        }

		return maxPositiveProbability;
	}

    /**
     * Runs a number of simulation rounds
     */
    public SimulationResult run(int rounds, String filename, Strategy strategy) throws IOException
    {
        SimulationResult results = run(rounds, strategy);
        results.save(filename, "");
        return results;
    }

    /**
     * Runs a number of simulation rounds
     */
    public SimulationResult run(int rounds, FileWriter writer, String roundId, Strategy strategy) throws IOException
    {
        SimulationResult results = run(rounds, strategy);
        results.save(writer, roundId);
        return results;
    }

    /**
     * Runs a number of simulation rounds
     */
    public SimulationResult run(int rounds, Strategy strategy)
    {
        SimulationResult results = new SimulationResult();

        for (int round = 0; round < rounds; round++)
            runRound(results, round, strategy);

        return results;
    }

    /**
     * Runs a simulation round
     */
    private void runRound(SimulationResult results, int round, Strategy strategy)
    {
        clearIndividuals();
        randomizeIndividuals();
        int trialCount = 0;

        for (int i = 0; i < strategy.countModels(); i++)
        {
            PoolingModel ts = strategy.getTestingModelForIndex(i);
            trialCount += runTestingModel(individuals, ts);
        }

        PoolingModel ts = new OneByOneModel();
        trialCount += runTestingModel(individuals, ts);

        int errorCount = countErrors(individuals);
        results.add(trialCount, errorCount);
    }

    /**
     * Clears the list of individuals
     */
    private void clearIndividuals()
    {
        for (int i = 0; i < individualCount; i++)
            individuals.get(i).clear();
    }

    /**
     * Creates a random sample of individuals, indicating whether they are positive
     * or negative. Uses a binomial distribution random number to sample the number
     * of positives.
     */
    private void randomizeIndividuals() 
    {
        for (int i = 0; i < individualCount; i++)
        {
            Individual individual = individuals.get(i);
            boolean isPositive = (PseudoRandom.randDouble() <= individual.getPositiveProbability());
            individual.setPositive(isPositive);
        }
    }

    /**
     * Runs a testing model in a simulation round
     */
    private int runTestingModel(List<Individual> allIndividuals, PoolingModel ts)
    {
        int trialCount = 0;

        List<Individual> unresolvedIndividuals = collectUnresolvedIndividuals(allIndividuals);
        shuffle(unresolvedIndividuals);

        if (unresolvedIndividuals.size() > 0)
        {
            IndividualGroupList groups = ts.splitIndividualsIntoGroups(unresolvedIndividuals);

            for (IndividualGroup group : groups.getGroups())
            {
                TestingSet testingSet = ts.createTestingSet(group.countIndividuals());
                boolean[] trialResults = calculateTestResults(group, testingSet);    
                calculateIndividualResults(trialResults, testingSet, group);
                trialCount += testingSet.getTrials();
            }
        }

        return trialCount;
    }

    /**
     * Collects all unresolved individuals into a list
     */
    private List<Individual> collectUnresolvedIndividuals(List<Individual> allIndividuals) 
    {
        List<Individual> unresolved = new ArrayList<Individual>();

        for (Individual individual : allIndividuals)
        {
            if (!individual.isResolved())
            {
                unresolved.add(individual);
            }
        }

        return unresolved;
    }

    /**
     * Calculates the result of all trials in a testing group
     */
    private boolean[] calculateTestResults(IndividualGroup group, TestingSet testingSet) 
    {
        int trialCount = testingSet.getTrials();
        boolean[] trialResults = new boolean[trialCount];

        for (int trial = 0; trial < trialCount; trial++) 
            trialResults[trial] = calculateTrialResult(trial, group, testingSet);

        return trialResults;
    }

    /**
     * Calculates the result of a trial based on the individuals in the group and error probabilities
     */
    private boolean calculateTrialResult(int trialIndex, IndividualGroup group, TestingSet testingSet) 
    {
        boolean correctResultIsPositive = false;
        int groupSize = group.countIndividuals();

        for (int i = 0; !correctResultIsPositive && i < groupSize; i++)
        {
            if (testingSet.isIndividualInTrial(i, trialIndex)) 
            {
                if (group.getIndividualByIndex(i).isPositive())
                {
                    correctResultIsPositive = true;
                }
            }
        }

        boolean result = correctResultIsPositive;

        if (result)
        {
            if (PseudoRandom.randDouble() < falseNegativeProbability)
                result = false;
        }
        else
        {
            if (PseudoRandom.randDouble() < falsePositiveProbability)
                result = true;
        }

        return result;
    }

    /**
     * Calculates the results of a group of individuals from the results of a set of trials
     */
    private void calculateIndividualResults(boolean[] trialResults, TestingSet testingSet, IndividualGroup group) 
    {
        // all the variables present in at least one negative pool are tagged negative
        resolveIndividualsInNegativeTrials(trialResults, testingSet, group);

        // any variable present in at least one positive pool where all other variables have been tagged negative, is tagged positive
        resolveSinglePositiveInPositiveTrial(trialResults, testingSet, group);
    }

    /**
     * Mark all individuals in negative trials as resolved
     */
    private void resolveIndividualsInNegativeTrials(boolean[] trialResults, TestingSet testingSet, IndividualGroup group) 
    {
        int individuals = testingSet.getIndividuals();
        int trials = testingSet.getTrials();

        /*for (int i = 0; i < trials; i++)
        {
            if (!trialResults[i])
            {
                for (int j = 0; j < individuals; j++)
                {
                    if (testingSet.isIndividualInTrial(j, i))
                    {
                        Individual individual = group.getIndividualByIndex(j);
                        individual.setResolved(true);
                        individual.setTestedPositive(false);
                        individual.setConfirmationLevel(individual.getConfirmationLevel() + 1);
                    }
                }
            }
        }*/

        for (int i = 0; i < individuals; i++)
        {
            int countNegativeTrials = 0;
            int countTrials = 0;

            for (int j = 0; j < trials; j++)
            {
                if (testingSet.isIndividualInTrial(i, j))
                {
                    countTrials++;

                    if (!trialResults[j])
                        countNegativeTrials++;
                }
            }

            if (countTrials == countNegativeTrials)
            {
                Individual individual = group.getIndividualByIndex(i);
                individual.setResolved(true);
                individual.setTestedPositive(false);
                individual.setConfirmations(individual.getConfirmations() + 1);
            }
        }
    }

    /**
     * Mark unresolved individuals as positive all others are negative in a positive trial
     */
    private void resolveSinglePositiveInPositiveTrial(boolean[] trialResults, TestingSet testingSet, IndividualGroup group) 
    {
        int individuals = testingSet.getIndividuals();
        int trials = testingSet.getTrials();

        for (int i = 0; i < trials; i++)
        {
            if (trialResults[i])
            {
                int individualCount = 0;
                int negativeCount = 0;
                int positiveCount = 0;
                Individual positiveIndividual = null;

                for (int j = 0; j < individuals; j++)
                {
                    if (testingSet.isIndividualInTrial(j, i))
                    {
                        individualCount++;
                        Individual individual = group.getIndividualByIndex(j);

                        if (!individual.isResolved())
                        {
                            positiveCount++;
                            positiveIndividual = individual;
                        }
                        else if (!individual.isTestedPositive())
                        {
                            negativeCount++;
                        }
                    }
                }

                if (positiveCount == 1 && negativeCount == individualCount-1)
                {
                    positiveIndividual.setResolved(true);
                    positiveIndividual.setTestedPositive(true);
                    positiveIndividual.setConfirmations(positiveIndividual.getConfirmations() + 1);
                }
            }
        }
    }

    /**
     * Count the number of wrongly assessed individuals
     */
    private int countErrors(List<Individual> allIndividuals) 
    {
        int count = 0;

        for (Individual individual : allIndividuals)
        {
            if (!individual.isResolved() || individual.isPositive() != individual.isTestedPositive())
            {
                count++;
            }
        }

        return count;
    }

	/**
	 * Shuffles a list of objects
	 */
	private void shuffle(List<Individual> individuals)
	{
		int len = individuals.size();

		for (int i = 0; i < len; i++) 
		{
            int indexToSwap = PseudoRandom.randInt(0, len-1);
            Individual temp = individuals.get(indexToSwap);
			individuals.set(indexToSwap, individuals.get(i));
			individuals.set(i, temp);
		}
	}
}