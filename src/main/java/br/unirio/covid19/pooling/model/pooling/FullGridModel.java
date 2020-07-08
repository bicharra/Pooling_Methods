package br.unirio.covid19.pooling.model.pooling;

import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Class that represents a pooling model that uses a bidimensional full grid
 * 
 * @author Márcio Barros
 */
public class FullGridModel extends PoolingModel
{
    private int gridSize;

    /**
     * Initializes the testing model
     */
    public FullGridModel(int gridSize)
    {
        this.gridSize = gridSize;
    }

    /**
     * Split the individuals in groups
     */
    @Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
        int sampleSize = calculateFullGridSampleSize(gridSize);
        return splitIntoGroups(individuals, sampleSize);
    }

    /**
     * Creates a testing set for the model
     */
    @Override
    public TestingSet createTestingSet(int individuals) 
    {
        int sampleSize = calculateFullGridSampleSize(gridSize);

        if (individuals == sampleSize)
            return createFullGridTestingSet(individuals, gridSize);
        else
            return TestingSet.createSingleTrialForAllIndividuals(individuals);
    }

    /**
     * Creates a bidimentional full grid distribution
     */
    public TestingSet createFullGridTestingSet(int individuals, int gridSize)
    {
        TestingSet set = new TestingSet(individuals, gridSize * 2);

        for (int i = 0; i < gridSize; i++)
        {
            for (int j = 0; j < gridSize; j++)
            {
                int index = i * gridSize + j;
                set.setIndividualInTrial(index, i, true);
            }

            for (int j = 0; j < gridSize; j++)
            {
                int index = i + j * gridSize;
                set.setIndividualInTrial(index, i + gridSize, true);
            }
        }

        return set;
    }

    /**
     * Calculates the number of individuals in a 2D grid of a given size
     */
    public int calculateFullGridSampleSize(int gridSize) 
    {
        return gridSize * gridSize;
    }
}