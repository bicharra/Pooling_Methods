package br.unirio.covid19.pooling.engine;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents the results of a simulation
 */
public class SimulationResult 
{
    private List<SimulationCycleResult> cycleResults;

    /**
     * Initialize the results of the simulation
     */
    public SimulationResult()
    {
        cycleResults = new ArrayList<SimulationCycleResult>();
    }

    /**
     * Returns the number of cycles in the simulation
     */
    public int count()
    {
        return cycleResults.size();
    }

    /**
     * Adds the results of a cycle
     */
    public void add(int trials, int errors)
    {
        cycleResults.add(new SimulationCycleResult(trials, errors));
    }

    /**
     * Saves the results of all cycles
     */
    public void save(String filename,  String roundId) throws IOException
    {
        FileWriter writer = new FileWriter(filename);

        if (roundId.length() > 0)
            writer.write("id\t");

        writer.write("errors\ttrials\n");
        save(writer, roundId);
        writer.close();
    }

    /**
     * Saves the results of all cycles
     */
    public void save(FileWriter writer, String roundId) throws IOException
    {
        for (int i = 0; i < cycleResults.size(); i++)
        {
            SimulationCycleResult cycle = cycleResults.get(i);

            if (roundId.length() > 0)
                writer.write(roundId + "\t");

            writer.write(cycle.getErrors() + "\t" + cycle.getTrials() + "\n");
        }
    }

    /**
     * Returns the average number of trials per cycle
     */
    public double getAverageTrials()
    {
        double sum = 0.0;

        for (int i = 0; i < cycleResults.size(); i++)
        {
            SimulationCycleResult cycle = cycleResults.get(i);
            sum += cycle.getTrials();
        }

        return sum / cycleResults.size();
    }

    /**
     * Returns the average number of errors per cycle
     */
    public double getAverageErrors()
    {
        double sum = 0.0;

        for (int i = 0; i < cycleResults.size(); i++)
        {
            SimulationCycleResult cycle = cycleResults.get(i);
            sum += cycle.getErrors();
        }

        return sum / cycleResults.size();
    }
}

/**
 * Class that represents the results of a simulation cycle
 */
class SimulationCycleResult 
{
    private @Getter int trials;

    private @Getter int errors;

    public SimulationCycleResult(int trials, int errors)
    {
        this.trials = trials;
        this.errors = errors;
    }
}