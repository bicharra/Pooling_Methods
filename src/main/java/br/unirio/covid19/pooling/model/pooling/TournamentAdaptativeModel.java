package br.unirio.covid19.pooling.model.pooling;

import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroup;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Class that represents a pooling model that simulates a tournament 
 * 
 * @author Márcio Barros
 */
public class TournamentAdaptativeModel extends PoolingModel
{
    private double eightIndividualsGroupMaxProbability;

    private double fourIndividualsGroupMaxProbability;

    private double twoIndividualsGroupMaxProbability;

    /**
     * Initializes the testing model indicating the size of the pool
     */
    public TournamentAdaptativeModel()
    {
        this(0.10, 0.15, 0.20);
    }

    /**
     * Initializes the testing model indicating the size of the pool
     */
    public TournamentAdaptativeModel(double prob8, double prob4, double prob2)
    {
        eightIndividualsGroupMaxProbability = prob8;
        fourIndividualsGroupMaxProbability = prob4;
        twoIndividualsGroupMaxProbability = prob2;
    }

    /**
     * Split the individuals in groups
     */
    @Override
    public IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals) 
    {
        sortByPositiveProbability(individuals);
        return adaptativeSplitIntoGroups(individuals);
    }

    /**
     * Split a set of individuals into groups of a given size
     */
    private IndividualGroupList adaptativeSplitIntoGroups(List<Individual> individuals) 
    {
        int individualCount = individuals.size();

        IndividualGroupList groups = new IndividualGroupList();
        IndividualGroup group = new IndividualGroup();
        int currentGroupSize = 8;
   
        for (int i = 0; i < individualCount; i++)
        {
            Individual individual = individuals.get(i);

            int individualGroupSize = getGroupSize(individual.getPositiveProbability());

            if (individualGroupSize != currentGroupSize || group.countIndividuals() == currentGroupSize)
            {
                groups.add(group);
                group = new IndividualGroup();
                currentGroupSize = individualGroupSize;
            }

            group.add(individual);
        }

        if (group.countIndividuals() > 0)
            groups.add(group);
   
        return groups;
    }

    /**
     * Gets the size of the group for a given probability of testing positive
     */
    private int getGroupSize(double positiveProbability)
    {
        if (positiveProbability < eightIndividualsGroupMaxProbability)
            return 8;

        if (positiveProbability < fourIndividualsGroupMaxProbability)
            return 4;

        if (positiveProbability < twoIndividualsGroupMaxProbability)
            return 2;

        return 1;
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