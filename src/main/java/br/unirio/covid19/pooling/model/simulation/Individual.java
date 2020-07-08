package br.unirio.covid19.pooling.model.simulation;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents an individual during a simulation
 */
public class Individual 
{
    private @Getter int number;

    private @Getter @Setter double positiveProbability;

    private @Getter @Setter boolean positive;

    private @Getter @Setter boolean resolved;

    private @Getter @Setter boolean testedPositive;

    private @Getter @Setter int confirmations;

    public Individual(int number)
    {
        this.number = number;
        this.positiveProbability = 0.0;
        clear();
    }

    public void clear()
    {
        this.positive = false;
        this.resolved = false;
        this.testedPositive = false;
        this.confirmations = 0;
    }

    @Override
    public String toString()
    {
        return "#" + number + " PP: " + positiveProbability;
    }
}