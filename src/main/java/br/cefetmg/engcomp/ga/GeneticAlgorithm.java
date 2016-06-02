package br.cefetmg.engcomp.ga;

import br.cefetmg.engcomp.fitness.FitnessFunction;
import br.cefetmg.engcomp.fitness.LinearFitnessFunction;
import br.cefetmg.engcomp.fitness.QuadraticFitnessFunction;
import br.cefetmg.engcomp.util.GraphGenerator;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.random.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by felipe on 23/05/16.
 */
public class GeneticAlgorithm {

    private final RandomDataGenerator RDG = new RandomDataGenerator();

    private int boardSize;
    private int populationSize;
    private double crossRate;
    private double mutationRate;
    private int maxIterations;

    private int [][] G;

    private FitnessFunction fitness;

    private List<Chromosome> population;

    private Chromosome globalBest;

    private boolean finished;

    private String resultFile;

    /**
     *
     * @param boardSize
     * @param populationSize
     * @param crossRate
     * @param mutationRate
     * @param maxIterations
     * @param resultFile
     */
    public GeneticAlgorithm(int boardSize, int populationSize, double crossRate, double mutationRate, int maxIterations, String resultFile) {
        this.boardSize = boardSize;
        this.populationSize = populationSize;
        this.crossRate = crossRate;
        this.mutationRate = mutationRate;
        this.maxIterations = maxIterations;
        this.resultFile = resultFile;
    }

    private void init() {
        GraphGenerator graphGenerator = new GraphGenerator(boardSize);

        G = graphGenerator.generate();
        fitness = new LinearFitnessFunction(G);

        population = new ArrayList<>();
        
        for(int i = 0; i < populationSize; ++i) {
            int [] genes = RDG.nextPermutation(G.length - 1, G.length - 1);

            population.add(new Chromosome(genes));
        }

        population.forEach(x -> x.eval(fitness));
        population.sort(Comparator.comparing(Chromosome::getValue));
        globalBest = population.get(populationSize - 1);

        this.finished = false;
    }

    private List<Chromosome> crossover(List<Chromosome> population) {

        Object [] A = RDG.nextSample(population, populationSize/2);
        Object [] B = RDG.nextSample(population, populationSize/2);

        List<Chromosome> crossed = IntStream.range(0, populationSize/2)
                .mapToObj(i -> RDG.nextUniform(0, 1) > crossRate? ((Chromosome)A[i]).cross((Chromosome)B[i]) : null)
                .filter(x ->  x != null)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        //create a new population with every element of past pop
        population.forEach(a -> crossed.add(new Chromosome(a)));

        return crossed;
    }

    private List<Chromosome> mutate(List<Chromosome> crossed) {
        return crossed.stream().map(x -> {
            if(Math.random() > mutationRate) {
                return x.mutate();
            } else {
                return x;
            }
        }).collect(Collectors.toList());
    }

    /**
     * Fitness proportional-selection
     * @param mutated mutated chromosomes
     * @return the new population with populationSize elements
     */
    private List<Chromosome> newPopulation(List<Chromosome> mutated) {
        mutated.sort(Comparator.comparing(Chromosome::getValue));

        Double [] fits = mutated.stream().map(Chromosome::getValue).toArray(Double[]::new);
        Double sum = Arrays.stream(fits).reduce((x, y) -> x + y).get();
        Double [] normFits = Arrays.stream(fits).map(x -> x/sum).toArray(Double[]::new);

        for (int i = 1; i < normFits.length; ++i){
            normFits[i] += normFits[i - 1];
        }

        List<Chromosome> selected = new ArrayList<>();


        for (int i = 0; i < populationSize - 1; ++i) {
            double p = RDG.nextUniform(0, 1);

            int j = IntStream.range(0, mutated.size())
                    .filter(k-> normFits[k] >= p)
                    .findFirst().getAsInt();

            selected.add(new Chromosome(mutated.get(j)));
        }
        selected.add(new Chromosome(globalBest));

        return selected;
    }

    private void check(){
        int pathSize = boardSize*boardSize - 1;

        for (Chromosome c : population) {
            if(c.getValue() == pathSize)
                finished = true;
        }

    }

    public void run() {
        init();

        int iteration = 1;

        while(!finished && iteration < maxIterations) {

            List<Chromosome> crossed = crossover(population);
            List<Chromosome> mutated  = mutate(crossed);
            // compute pop fitness and select the best individual
            mutated.forEach(x -> x.eval(fitness));
            Chromosome best = population.stream().max(Comparator.comparing(Chromosome::getValue)).get();

            globalBest = best.getValue() > globalBest.getValue()? best : globalBest;

            this.population = newPopulation(mutated);

            check();
            iteration++;
        }

        System.out.println("Best solution: " + globalBest);
        System.out.println("Best fitness: " + new LinearFitnessFunction(G).eval(globalBest));
        FitnessFunction.saveResults(resultFile);
    }
}
