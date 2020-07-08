package br.unirio.covid19.pooling.model.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a group of individuals in a simulation
 */
public class IndividualGroup 
{
    private List<Individual> individuals;

    /**
     * Initializes the group of individuals
     */
    public IndividualGroup()
    {
        this.individuals = new ArrayList<Individual>();
    }

    /**
     * Counts the individuals in the group
     */
    public int countIndividuals() 
    {
		return individuals.size();
    }
    
    /**
     * Return an individual given its index
     */
    public Individual getIndividualByIndex(int index)
    {
        return individuals.get(index);
    }

    /**
     * Adds an individual in the group
     */
    public void add(Individual individual)
    {
        individuals.add(individual);
    }

    /**
     * Returns all individuals
     */
    public Iterable<Individual> getIndividuals()
    {
        return individuals;
    }
}