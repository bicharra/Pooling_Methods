package br.unirio.covid19.pooling.model.pooling;

import java.util.Comparator;
import java.util.List;

import br.unirio.covid19.pooling.model.simulation.Individual;
import br.unirio.covid19.pooling.model.simulation.IndividualGroup;
import br.unirio.covid19.pooling.model.simulation.IndividualGroupList;
import br.unirio.covid19.pooling.model.simulation.TestingSet;

/**
 * Abstract class for a pooling strategy
 */
public abstract class PoolingModel 
{
    /**
     * This function is called to split a set of individuals into testing groups
     */
    public abstract IndividualGroupList splitIndividualsIntoGroups(List<Individual> individuals);

    /**
     * This function is called to create a testing set for a group of individuals
     */
    public abstract TestingSet createTestingSet(int individuals);

    /**
     * Split a set of individuals into groups of a given size
     */
    protected IndividualGroupList splitIntoGroups(List<Individual> individuals, int maxGroupSize) 
    {
        int individualCount = individuals.size();

        int groupCount = individualCount / maxGroupSize;
        
        if (individualCount % maxGroupSize != 0)
            groupCount++;

        IndividualGroupList groups = new IndividualGroupList();
   
        for (int i = 0; i < groupCount; i++)
        {
            IndividualGroup group = new IndividualGroup();
            
            for (int j = 0; j < maxGroupSize; j++)
            {
                int index = i * maxGroupSize + j;
   
                if (index < individualCount)
                    group.add(individuals.get(index));
            }

            groups.add(group);
        }
   
        return groups;
    }

    /**
     * Sorts individuals by their positive probability
     */
    public void sortByPositiveProbability(List<Individual> individuals)
    {
        individuals.sort(new IndividualComparatorByPositiveProbability());
    }
}

/**
 * Class that compares individuals by their positive probability
 */
class IndividualComparatorByPositiveProbability implements Comparator<Individual>
{
    @Override
    public int compare(Individual individual1, Individual individual2) {
        if (individual1.getPositiveProbability() < individual2.getPositiveProbability())
            return -1;

        if (individual1.getPositiveProbability() > individual2.getPositiveProbability())
            return +1;

        return 0;
    }
}