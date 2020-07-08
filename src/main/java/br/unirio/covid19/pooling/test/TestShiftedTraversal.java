package br.unirio.covid19.pooling.test;

import br.unirio.covid19.pooling.model.pooling.ShiftedTraversalModel;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Shifted Traversal Design pooling model
 * 
 * @author Márcio Barros
 */
public class TestShiftedTraversal 
{
    /**
     * Test the composition of the pool for a given individual
     */
    public void testTrialsForFirstIndividual()
    {
        ShiftedTraversalModel std = new ShiftedTraversalModel(9, 3, 4);
        TestingSet ts = std.createTestingSet(9);

        int[][] trialsWithZero = getTrialsForIndividual(0, ts);
        assertEquals(trialsWithZero.length, 4);
        
        assertEquals(trialsWithZero[0].length, 3);
        assertEquals(trialsWithZero[0][0], 0);
        assertEquals(trialsWithZero[0][1], 3);
        assertEquals(trialsWithZero[0][2], 6);
        
        assertEquals(trialsWithZero[1].length, 3);
        assertEquals(trialsWithZero[1][0], 0);
        assertEquals(trialsWithZero[1][1], 5);
        assertEquals(trialsWithZero[1][2], 7);
        
        assertEquals(trialsWithZero[2].length, 3);
        assertEquals(trialsWithZero[2][0], 0);
        assertEquals(trialsWithZero[2][1], 4);
        assertEquals(trialsWithZero[2][2], 8);
        
        assertEquals(trialsWithZero[3].length, 3);
        assertEquals(trialsWithZero[3][0], 0);
        assertEquals(trialsWithZero[3][1], 1);
        assertEquals(trialsWithZero[3][2], 2);
    }

    /**
     * Test: Every pair of individuals pertains to a trial
     */
    public void testEveryPairInSomeTrial(int individuals, int trialsInLayers, int numberOfLayers)
    {
        ShiftedTraversalModel std = new ShiftedTraversalModel(individuals, trialsInLayers, numberOfLayers);
        TestingSet ts = std.createTestingSet(individuals);
        // System.out.println(ts);

        for (int i = 0; i < individuals; i++)
        {
            for (int j = 0; j < individuals; j++)
            {
                if (i != j)
                {
                    // if (!checkPairInSomeTrial(i, j, ts))
                    //     System.out.println(i + " " + j + " not in any given trial.");

                   assertTrue(checkPairInSomeTrial(i, j, ts));
                }
            }
        }
    }
    
    /**
     * Checks if a pair of individuals pertain to a trial
     */
    private boolean checkPairInSomeTrial(int firstIndividual, int secondIndividual, TestingSet ts) 
    {
        for (int i = 0; i < ts.getTrials(); i++)
        {
            if (ts.isIndividualInTrial(firstIndividual, i) && ts.isIndividualInTrial(secondIndividual, i))
                return true;
        }

        return false;
    }

    /**
     * Get the trials on which an individual participates
     */
    private int[][] getTrialsForIndividual(int individual, TestingSet set) 
    {
        int poolCount = set.countTrialsWithIndividual(individual);
        int[][] result = new int[poolCount][];

        int poolWalker = 0;

        for (int trial = 0; trial < set.getTrials(); trial++)
        {
            if (set.isIndividualInTrial(individual, trial))
            {
                result[poolWalker++] = set.getIndividualsInTrial(trial);
            }
        }

        return result;
    }

    /**
     * Main program for testing
     */
    public static final void main(String[] args)
    {
        TestShiftedTraversal test = new TestShiftedTraversal();

        System.out.println("Test trials for the first individual ...");
        // test.testTrialsForFirstIndividual();

        System.out.println("Test if every pair is in some trial for STD(9, 3, 4) ...");
        test.testEveryPairInSomeTrial(9, 3, 4);

        System.out.println("Test if every pair is in some trial for STD(16, 2, 4) ...");
        System.out.println(new ShiftedTraversalModel(0, 0, 0).calculateCompressionPower(4, 2));
        // test.testEveryPairInSomeTrial(4, 2, 3);


        // number of trials per layer (q) should be prime, larger than one and smaller than n

        // number of layers (k) must be q+1 for complete redundancy: every pair of individuals meets in one trial and detect 1 positive

        // Let t be the number of positive cases in a set.
        // Let t · Γ(q,n) ≤ q.
        // Let k = t · Γ + 1.

        // Let Γ(q,n) = 1 for complete redundancy. Then k = t + 1.
    }
}