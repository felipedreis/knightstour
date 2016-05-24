package br.cefetmg.engcomp.ga;

import br.cefetmg.engcomp.fitness.FitnessFunction;
import br.cefetmg.engcomp.fitness.LinearFitnessFunction;
import br.cefetmg.engcomp.util.GraphGenerator;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by felipe on 23/05/16.
 */
public class GeneticAlgorithm {

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

    /**
     *
     * @param boardSize
     * @param populationSize
     * @param crossRate
     * @param mutationRate
     */
    public GeneticAlgorithm(int boardSize, int populationSize, double crossRate, double mutationRate, int maxIterations) {
        this.boardSize = boardSize;
        this.populationSize = populationSize;
        this.crossRate = crossRate;
        this.mutationRate = mutationRate;
        this.maxIterations = maxIterations;
    }

    private void init() {
        GraphGenerator graphGenerator = new GraphGenerator(boardSize);
        RandomDataGenerator dataGenerator = new RandomDataGenerator();

        G = graphGenerator.generate();
        fitness = new LinearFitnessFunction(G);

        population = new ArrayList<>();
        
        for(int i = 0; i < populationSize; ++i) {
            int [] genes = dataGenerator.nextPermutation(G.length, G.length);

            population.add(new Chromosome(genes));
        }

        population.forEach(x -> x.eval(fitness));
        population.sort(Comparator.comparing(Chromosome::getValue));

        this.finished = false;
    }

    private List<Chromosome> selection(){

        return population.subList(populationSize / 2, populationSize - 1);
    }

    private List<Chromosome> crossover(List<Chromosome> selected) {

        List<Chromosome> crossed = new ArrayList<>();

        RandomDataGenerator dataGenerator = new RandomDataGenerator();

        for(int i = 0; i < populationSize; ++i) {

            Object [] sample = dataGenerator.nextSample(selected, 2);
            Chromosome [] result = ((Chromosome)sample[0]).cross((Chromosome)sample[1]);
            crossed.add(result[0]);
            crossed.add(result[1]);

        }

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

    private List<Chromosome> newPopulation(List<Chromosome> mutated) {
        mutated.forEach(x -> x.eval(fitness));

        Set<Chromosome> res = new HashSet<>();
        res.addAll(population);
        res.addAll(mutated);

        List<Chromosome> uniques
                = res.stream().sorted(Comparator.comparing(Chromosome::getValue).reversed()).collect(Collectors.toList());

        return uniques.subList(0, populationSize);
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

            List<Chromosome> selected = selection();
            List<Chromosome> crossed = crossover(selected);
            List<Chromosome> mutated  = mutate(crossed);

            this.population = newPopulation(mutated);

            check();
            iteration++;

            globalBest = population.stream().max(Comparator.comparing(Chromosome::getValue)).get();

            System.out.println("Iteration #" + iteration);
            System.out.println("Global Best value: " + globalBest.getValue());
        }

        System.out.println("Best solution: " + globalBest);

    }
}
