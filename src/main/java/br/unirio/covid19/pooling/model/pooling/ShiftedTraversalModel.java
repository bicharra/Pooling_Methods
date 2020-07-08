package br.unirio.covid19.pooling.model.pooling;

import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;
import br.unirio.covid19.pooling.utils.PrimeNumberUtils;

/**
 * Class that represents a STD pooling model
 * 
 * @author Márcio Barros
 */
public class ShiftedTraversalModel extends PoolingModel
{
    /**
     * Number of individuals (n) on each layer of the pooling model
     */
    private int groupSize;

    /**
     * Number of trails in a layer (q) - must be a prime number
     */
    private int trialsInLayer;

    /**
     * Number of layers (k)
     */
    private int numberOfLayers;

    /**
     * Initializes a STD pooling model
     */
    public ShiftedTraversalModel(int groupSize, int trialsInLayer, int numberOfLayers)
    {
        this.groupSize = groupSize;
        this.trialsInLayer = trialsInLayer;
        this.numberOfLayers = numberOfLayers;
    }

    /**
     * Splits a set of individuals into groups of the selected size
     */
	@Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
		return splitIntoGroups(individuals, groupSize);
	}

    /**
     * Creates a testing set for a number of individuals
     */
	@Override
    public TestingSet createTestingSet(int individuals) 
    {
        TestingSet ts = new TestingSet(individuals, trialsInLayer * numberOfLayers);
        int theta = calculateCompressionPower(individuals, trialsInLayer);

        for (int layer = 0; layer < numberOfLayers; layer++)
            buildLayer(individuals, trialsInLayer, layer, theta, ts);

        return ts;
    }

    /**
     * Calculates the number of trials in a layer given the number of positives (t) and errors (e)
     */
    public int calculateTrialsInLayer(int individuals, int expectedPositive, int expectedErrors)
    {
        int trialsInLayer = 2;

        while (calculateLayerCount(individuals, expectedPositive, expectedErrors, trialsInLayer) > trialsInLayer + 1)
            trialsInLayer = PrimeNumberUtils.nextPrime(trialsInLayer);

        return trialsInLayer;
    }

    /**
     * Calculates the number of layers given the number of positives (t), errors (e) and trials on each layer (q)
     */
    public int calculateLayerCount(int individuals, int expectedPositive, int expectedErrors, int trialsInLayer)
    {
        int compressionPower = calculateCompressionPower(individuals, trialsInLayer);
        int k = expectedPositive * compressionPower + 2 * expectedErrors + 1;
        return k;
    }

    /**
     * Calculate the compression power for a given number of individuals (n) and trials in a layer (q)
     */
    public int calculateCompressionPower(int individuals, int trialsInLayer) 
    {
        return ((int) Math.ceil(Math.log(individuals) / Math.log(trialsInLayer))) - 1;
    }

    /**
     * Builds a layer in a testing set
     */
    private void buildLayer(int individuals, int trialsInLayer, int layer, int compressionPower, TestingSet ts) 
    {
        for (int individual = 0; individual < individuals; individual++)
        {
            int shifts = calculateShifts(individual, layer, trialsInLayer, compressionPower);
            boolean[] c00 = createReferenceVector(trialsInLayer);

            for (int shift = 0; shift < shifts; shift++)
                c00 = applyShift(c00);

            for (int trial = 0; trial < trialsInLayer; trial++)
                ts.setIndividualInTrial(individual, layer * trialsInLayer + trial, c00[trial]);
        }
    }

    /**
     * Creates the reference vector required to build a layer
     */
    private boolean[] createReferenceVector(int trialsInLayer) 
    {
        boolean[] v = new boolean[trialsInLayer];
        v[0] = true;

        for (int i = 1; i < trialsInLayer; i++)
            v[i] = false;

        return v;
    }

    /**
     * Shifts a reference vector to build a layer
     */
    private boolean[] applyShift(boolean[] c00) 
    {
        int len = c00.length;
        boolean temp = c00[len-1];

        for (int i = len-1; i > 0; i--)
            c00[i] = c00[i-1];

        c00[0] = temp;
        return c00;
    }

    /**
     * Calculates the number of shifts required to build a layer
     */
    private int calculateShifts(int individual, int layer, int trialsInLayer, int compressionPower) 
    {
        if (layer < trialsInLayer)
        {
            int result = 0;

            for (int c = 0; c <= compressionPower; c++)
                result += Math.pow(layer, c) * Math.floor(individual / Math.pow(trialsInLayer, c));

            return result;
        }

        return (int) Math.floor(individual / Math.pow(trialsInLayer, compressionPower));
    }
}