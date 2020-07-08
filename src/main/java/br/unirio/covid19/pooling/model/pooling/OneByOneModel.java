package br.unirio.covid19.pooling.model.pooling;

import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Class that represents a pooling model that evaluates each individual separately
 * 
 * @author Márcio Barros
 */
public class OneByOneModel extends PoolingModel
{
    /**
     * Split the individuals in groups
     */
    @Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
        return splitIntoGroups(individuals, 1);
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