package br.unirio.covid19.pooling.model.pooling;

import java.util.ArrayList;
import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Class that represents a pooling model that simulates a tournament 
 * 
 * @author Márcio Barros
 */
public class TournamentAdaptativeModel2 extends PoolingModel
{
    private int poolSize;

    /**
     * Initializes the testing model indicating the size of the pool
     */
    public TournamentAdaptativeModel2(int poolSize)
    {
        this.poolSize = poolSize;
    }

    /**
     * Split the individuals in groups
     */
    @Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
        sortByPositiveProbability(individuals);

        List<Individual> potentiallyNegative = new ArrayList<Individual>();
        List<Individual> potentiallyPositive = new ArrayList<Individual>();

        for (Individual individual : individuals)
        {
            if (individual.getPositiveProbability() > 0)
                potentiallyPositive.add(individual);
            else
                potentiallyNegative.add(individual);
        }

        IndividualGroupList positiveGroups = splitIntoGroups(potentiallyPositive, 1);
        IndividualGroupList negativeGroups = splitIntoGroups(potentiallyNegative, poolSize);

        IndividualGroupList groups = new IndividualGroupList();
        groups.add(positiveGroups);
        groups.add(negativeGroups);
        return groups;
    }

    /**
     * Creates a testing set for the model
     */
    @Override
    public TestingSet createTestingSet(int individuals) 
    {
        return TestingSet.createSingleTrialForAllIndividuals(individuals);
    }
}