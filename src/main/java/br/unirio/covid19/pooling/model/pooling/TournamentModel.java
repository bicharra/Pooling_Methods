package br.unirio.covid19.pooling.model.pooling;

import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Class that represents a pooling model that simulates a tournament 
 * 
 * @author Márcio Barros
 */
public class TournamentModel extends PoolingModel
{
    private int poolSize;

    private boolean orderByProbability;

    /**
     * Initializes the testing model indicating the size of the pool
     */
    public TournamentModel(int poolSize, boolean orderByProbability)
    {
        this.poolSize = poolSize;
        this.orderByProbability = orderByProbability;
    }

    /**
     * Split the individuals in groups
     */
    @Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
        if (orderByProbability)
            sortByPositiveProbability(individuals);

        return splitIntoGroups(individuals, poolSize);
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