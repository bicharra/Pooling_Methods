package br.unirio.covid19.pooling.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import br.unirio.covid19.pooling.engine.Simulator;
import br.unirio.covid19.pooling.model.pooling.BorderGridModel;
import br.unirio.covid19.pooling.model.pooling.FullGridModel;
import br.unirio.covid19.pooling.model.pooling.ShiftedTraversalModel;
import br.unirio.covid19.pooling.model.pooling.TournamentAdaptativeModel2;
import br.unirio.covid19.pooling.model.pooling.TournamentModel;
import br.unirio.covid19.pooling.model.simulation.Strategy;
import br.unirio.covid19.pooling.utils.PseudoRandom;

public class AnalysisSensitivity 
{
    private static final int POPULATION_SIZE = 1000;
    private static final int SIMULATION_SCENARIOS = 100;
    private static final int SIMULATION_CYCLES = 100;

    public void analyzeBaseline() throws IOException
    {
        double[] prevalences = new double[] { 0.001, 0.01, 0.05, 0.10 };
        int[] poolSizes = new int[] { 2, 4, 6, 8, 10 };

        FileWriter writer = new FileWriter(new File("results//sensitivity//results-baseline.csv"));
        writer.write("id\tprev\terrors\ttrials\n");

        System.out.print("Running BASELINE ");

        for (int i = 0; i < prevalences.length; i++)
        {
            for (int j = 0; j < poolSizes.length; j++)
            {
                analyzeBaseline(writer, prevalences[i], poolSizes[j]);
            }

            System.out.print(".");
        }

        System.out.println();
        writer.close();
    }

    private void analyzeBaseline(FileWriter writer, double prevalence, int poolSize) throws IOException
    {
        String prefix = "\t" + (int)(prevalence * 1000);

        for (int i = 0; i < SIMULATION_SCENARIOS; i++)
        {
            Simulator simulator = new Simulator(POPULATION_SIZE, Constants.FALSE_POSITIVE_PROBABILITY, 0.0).setPositiveProbability(prevalence);
            simulator.run(SIMULATION_CYCLES, writer, "TR" + poolSize + prefix, new Strategy().add(new TournamentModel(poolSize, false)));
            simulator.run(SIMULATION_CYCLES, writer, "FG" + poolSize + prefix, new Strategy().add(new FullGridModel(poolSize)));
            simulator.run(SIMULATION_CYCLES, writer, "BG" + poolSize + prefix, new Strategy().add(new BorderGridModel(poolSize)));
            simulator.run(SIMULATION_CYCLES, writer, "ST" + poolSize + prefix, new Strategy().add(new ShiftedTraversalModel(poolSize * 2, 2, 3)));
        }
    }

    public void analyzeSensitivity() throws IOException 
    {
        double[] prevalences = new double[] { 0.001, 0.01, 0.05, 0.10 };
        int[] poolSizes = new int[] { 10, 10, 6, 4 };

        FileWriter writer = new FileWriter(new File("results//sensitivity//results-sensitivity.csv"));
        writer.write("id\tspec\tsens\tprev\terrors\ttrials\n");

        for (double specificity = 0.70; specificity <= 1.001; specificity += 0.05)
        {
            for (double sensitivity = 0.70; sensitivity <= 1.001; sensitivity += 0.05)
            {
                System.out.print("Running SP-" + (int)(specificity * 100) + " x SE-" + (int)(sensitivity * 100) + " ");

                for (int i = 0; i < prevalences.length; i++)
                {
                    analyzeSensitivity(writer, specificity, sensitivity, prevalences[i], poolSizes[i]);
                    System.out.print(".");
                }

                System.out.println();
            }
        }

        writer.close();
    }

    private void analyzeSensitivity(FileWriter writer, double specificity, double sensitivity, double prevalence, int poolSize) throws IOException 
    {
        for (int i = 0; i < SIMULATION_SCENARIOS; i++)
        {
            int positives = PseudoRandom.randBinomial(POPULATION_SIZE, prevalence);
            int[] population = generatePopulation(positives);
            int[] estimation = generateEstimation(specificity, sensitivity, positives, population);
            double[] probabilities = generateProbabilities(estimation);

            Simulator simulator = new Simulator(POPULATION_SIZE, Constants.FALSE_POSITIVE_PROBABILITY, 0.00).loadPositiveProbability(probabilities);
            
            String tournamentId = "TR" + poolSize + "\t" + (int)(specificity * 100) + "\t" + (int)(sensitivity * 100) + "\t" + (int)(prevalence * 1000);
            simulator.run(SIMULATION_CYCLES, writer, tournamentId, new Strategy().add(new TournamentModel(poolSize, true)));

            String adaptativeId = "TA" + poolSize + "\t" + (int)(specificity * 100) + "\t" + (int)(sensitivity * 100) + "\t" + (int)(prevalence * 1000);
            simulator.run(SIMULATION_CYCLES, writer, adaptativeId, new Strategy().add(new TournamentAdaptativeModel2(poolSize)));
        }
    }

    private int[] generatePopulation(int positives) 
    {
        int[] population = new int[POPULATION_SIZE];

        for (int i = 0; i < positives; i++)
            population[i] = 1;

        for (int i = positives; i < POPULATION_SIZE; i++)
            population[i] = 0;

        return population;
    }

    private int[] generateEstimation(double specificity, double sensitivity, int positives, int[] population) 
    {
        int[] estimation = new int[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++)
            estimation[i] = population[i];
        
        int falseNegatives = PseudoRandom.randBinomial(positives, 1.0 - sensitivity);

        for (int i = 0; i < falseNegatives; i++)
            estimation[i] = 0;

        int falsePositives = PseudoRandom.randBinomial(POPULATION_SIZE - positives, 1.0 - specificity);

        for (int i = 0; i < falsePositives; i++)
            estimation[positives + i] = 1;

        return estimation;
    }

    private double[] generateProbabilities(int[] estimation)
    {
        double[] probabilities = new double[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++)
            probabilities[i] = 1.0 * estimation[i];

        return probabilities;
    }

    public void analyzeRealDataset() throws IOException
    {
        FileWriter writer = new FileWriter(new File("results//sensitivity//results-real-dataset.csv"));
        writer.write("id\tpop\terrors\ttrials\n");

        for (int i = 0; i < SIMULATION_SCENARIOS; i++)
        {
            Simulator simulator1 = new Simulator(181, Constants.FALSE_POSITIVE_PROBABILITY, 0.0).setPositiveProbability(0.137);
            simulator1.run(SIMULATION_CYCLES, writer, "TR4-181-13p7\t181", new Strategy().add(new TournamentModel(4, true)));

            Simulator simulator2 = new Simulator(140, Constants.FALSE_POSITIVE_PROBABILITY, 0.0).setPositiveProbability(0.05);
            simulator2.run(SIMULATION_CYCLES, writer, "TR6-140-5\t140", new Strategy().add(new TournamentModel(6, true)));

            Simulator simulator3 = new Simulator(173, Constants.FALSE_POSITIVE_PROBABILITY, 0.0).setPositiveProbability(0.11);
            simulator3.run(SIMULATION_CYCLES, writer, "TR4-173-11\t173", new Strategy().add(new TournamentModel(4, true)));

            Simulator simulator4 = new Simulator(155, Constants.FALSE_POSITIVE_PROBABILITY, 0.0).setPositiveProbability(0.06);
            simulator4.run(SIMULATION_CYCLES, writer, "TR5-155-6\t155", new Strategy().add(new TournamentModel(5, true)));
        }

        writer.close();
    }
}