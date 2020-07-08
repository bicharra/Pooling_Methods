package br.unirio.covid19.pooling;

import java.io.IOException;

import br.unirio.covid19.pooling.analysis.AnalysisSensitivity;
import br.unirio.covid19.pooling.utils.PseudoRandom;

public class MainProgram
{
    public static final void main(final String[] args) throws IOException
    {
        PseudoRandom.init(347634739);
        // new AnalysisSensitivity().analyzeBaseline();
        // new AnalysisSensitivity().analyzeSensitivity();
        new AnalysisSensitivity().analyzeRealDataset();
    }
}