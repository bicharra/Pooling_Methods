package br.unirio.covid19.pooling.model.simulation;

import lombok.Getter;

/**
 * Class that represents a testing set comprised by a set of trials,
 * each evaluating a pool of individuals.
 * 
 * @author Márcio Barros
 */
public class TestingSet
{
    private @Getter int individuals;
    private @Getter int trials;
    private boolean[][] matrix;

    /**
     * Initializes a testing set for a given number of individuals and trials
     */
    public TestingSet(int individuals, int trials)
    {
        this.individuals = individuals;
        this.trials = trials;
        matrix = new boolean[individuals][trials];
    }

    /**
     * Indicates whether an individual participates in a trial
     */
    public void setIndividualInTrial(int individual, int trial, boolean flag)
    {
        matrix[individual][trial] = flag;
    }

    /**
     * Determines whether an individual participates in a trial
     */
    public boolean isIndividualInTrial(int individual, int trial) 
    {
		return matrix[individual][trial];
	}

    /**
     * Counts the number of individuals in a given trial
     */
    public int countIndividualsInTrial(int trial) 
    {
        int individualsInPool = 0;

        for (int j = 0; j < individuals; j++)
            if (isIndividualInTrial(j, trial))
                individualsInPool++;

        return individualsInPool;
    }

    /**
     * Count the number of trails on which an individual participates
     */
    public int countTrialsWithIndividual(int individual) 
    {
        int poolCount = 0;

        for (int trial = 0; trial < trials; trial++)
            if (isIndividualInTrial(individual, trial))
                poolCount++;

        return poolCount;
    }

    /**
     * Returns the individuals participating in a given trial
     */
    public int[] getIndividualsInTrial(int trial) 
    {
        int individualsInPool = countIndividualsInTrial(trial);
        int[] result = new int[individualsInPool];
        int individualWalker = 0;

        for (int individual = 0; individual < getIndividuals(); individual++)
        {
            if (isIndividualInTrial(individual, trial))
            {
                result[individualWalker] = individual;
                individualWalker++;
            }
        }

        return result;
    }

    /**
     * Returns the testing set as a string
     */
    @Override
    public String toString()
    {
        String result = "";

        for (int trial = 0; trial < trials; trial++)
        {
            for (int individual = 0; individual < individuals; individual++)
            {
                if (matrix[individual][trial])
                    result += "*";
                else
                    result += ".";
            }

            result += "\n";
        }    

        return result;
    }

    /**
     * Creates a testing set with a single trial for all individuals
     */
    public static TestingSet createSingleTrialForAllIndividuals(int individuals) 
    {
        TestingSet ts = new TestingSet(individuals, 1);

        for (int individual = 0; individual < individuals; individual++)
            ts.setIndividualInTrial(individual, 0, true);
   
        return ts;
    }
}