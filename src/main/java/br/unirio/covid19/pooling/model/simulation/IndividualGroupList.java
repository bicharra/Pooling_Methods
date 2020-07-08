package br.unirio.covid19.pooling.model.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a list of groups of individuals
 */
public class IndividualGroupList
{
    private List<IndividualGroup> groups;

    /**
     * Initializes the list of groups
     */
    public IndividualGroupList()
    {
        this.groups = new ArrayList<IndividualGroup>();
    }

    /**
     * Counts the number of groups in the list
     */
    public int countGroups() 
    {
		return groups.size();
	}

    /**
     * Adds a group to the list
     */
    public void add(IndividualGroup group)
    {
        this.groups.add(group);
    }

    /**
     * Adds a list of groups to the list
     */
    public void add(IndividualGroupList groups)
    {
        for (IndividualGroup group : groups.getGroups())
            this.groups.add(group);
    }

    /**
     * Returns all groups in the list
     */
    public Iterable<IndividualGroup> getGroups()
    {
        return groups;
    }
}