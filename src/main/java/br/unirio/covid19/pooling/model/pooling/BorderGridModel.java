package br.unirio.covid19.pooling.model.pooling;

import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Class that represents a pooling model that uses a bidimensional border grid
 * 
 * @author Márcio Barros
 */
public class BorderGridModel extends PoolingModel
{
    private int gridSize;

    /**
     * Initializes the testing model
     */
    public BorderGridModel(int gridSize)
    {
        this.gridSize = gridSize;
    }

    /**
     * Split the individuals in groups
     */
    @Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
        int sampleSize = calculateBorderGridSampleSize(gridSize);
        return splitIntoGroups(individuals, sampleSize);
    }

    /**
     * Creates a testing set for the model
     */
    @Override
    public TestingSet createTestingSet(int individuals) 
    {
        int sampleSize = calculateBorderGridSampleSize(gridSize);

        if (individuals == sampleSize)
            return createBorderGridTrial(individuals, gridSize);
        else
            return TestingSet.createSingleTrialForAllIndividuals(individuals);
    }

    /**
     * Creates a bidimentional grid distribution
     */
    public TestingSet createBorderGridTrial(int individuals, int gridSize)
    {
        int sampleSize = calculateBorderGridSampleSize(gridSize);
        TestingSet set = new TestingSet(individuals, 4);

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < gridSize-1; j++)
            {
                int index = (i * (gridSize-1) + j) % sampleSize;
                set.setIndividualInTrial(index, i, true);
            }

            int index = ((i+1) * (gridSize-1)) % sampleSize;
            set.setIndividualInTrial(index, i, true);
        }

        return set;
    }

    /**
     * Calculates the number of individuals in a 2D grid of a given size
     */
    public int calculateBorderGridSampleSize(int gridSize) 
    {
        return gridSize * 2 + (gridSize - 2) * 2;
    }
}