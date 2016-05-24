package br.cefetmg.engcomp;

import br.cefetmg.engcomp.ga.GeneticAlgorithm;
import org.apache.commons.cli.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static Options options;

    public static void main( String[] args ) throws ParseException
    {
        Parser parser = new BasicParser();
        CommandLine cl = parser.parse(options, args);
        int boardSize = Integer.parseInt(cl.getOptionValue("board"));
        int populationSize = Integer.parseInt(cl.getOptionValue("population"));
        double crossoverRate = Double.parseDouble(cl.getOptionValue("crossover"));
        double mutationRate = Double.parseDouble(cl.getOptionValue("mutation"));
        int maxIterations = Integer.parseInt(cl.getOptionValue("iterations"));

        GeneticAlgorithm ga = new GeneticAlgorithm(boardSize, populationSize, crossoverRate, mutationRate, maxIterations);

        ga.run();
    }

    static {
        Option boardSize = OptionBuilder.hasArg()
                .withArgName("size")
                .create("board");

        Option populationSize = OptionBuilder.hasArg()
                .withArgName("size")
                .create("population");

        Option crossoverRate = OptionBuilder.hasArg()
                .withArgName("rate")
                .create("crossover");

        Option mutationRate = OptionBuilder.hasArg()
                .withArgName("rate")
                .create("mutation");

        Option maxIterations = OptionBuilder.hasArg()
                .withArgName("value")
                .create("iterations");


        options = new Options();

        options.addOption(boardSize);
        options.addOption(populationSize);
        options.addOption(crossoverRate);
        options.addOption(mutationRate);
        options.addOption(maxIterations);
    }
}
