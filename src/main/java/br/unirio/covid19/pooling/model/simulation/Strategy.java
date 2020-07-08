package br.unirio.covid19.pooling.model.simulation;

import java.util.ArrayList;
import java.util.List;

import br.unirio.covid19.pooling.model.pooling.PoolingModel;

/**
 * Class thet represents a sequence of testing models
 * 
 * @author Márcio Barros
 */
public class Strategy 
{
    private List<PoolingModel> testingModels;

    public Strategy()
    {
        this.testingModels = new ArrayList<PoolingModel>();
    }

    public int countModels()
    {
        return this.testingModels.size();
    }

    public PoolingModel getTestingModelForIndex(int index)
    {
        return this.testingModels.get(index);
    }

    public Strategy add(PoolingModel testingModel)
    {
        this.testingModels.add(testingModel);
        return this;
    }

	public IndividualGroupList splitIndividualsIntoGroups(List<Individual> unresolvedIndividuals) {
		return null;
	}
}