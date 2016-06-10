package br.cefetmg.engcomp.ga;

import br.cefetmg.engcomp.fitness.FitnessFunction;
import br.cefetmg.engcomp.fitness.LinearFitnessFunction;
import br.cefetmg.engcomp.fitness.QuadraticFitnessFunction;
import br.cefetmg.engcomp.util.GraphGenerator;
import br.cefetmg.engcomp.util.Pair;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.random.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private BufferedWriter writer;

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

        File file = new File(resultFile);

        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            writer = new BufferedWriter(new FileWriter((file)));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Compute the fitness of population and return pairs of elements to cross
     * @param population
     * @return
     */
    public List<Pair<Chromosome, Chromosome>> select(List<Chromosome> population) {

        Double [] fits = population
                            //.parallelStream()
                            .stream()
                            .map(Chromosome::getValue).toArray(Double[]::new);
        
        Double sum = Arrays
                        .stream(fits)
                        //.parallel()
                        .reduce((x, y) -> x + y).get();
        
        Double [] normFits = Arrays
                                .stream(fits)
                                //.parallel()
                                .map(x -> x/sum).toArray(Double[]::new);

        for (int i = 1; i < normFits.length; ++i){
            normFits[i] += normFits[i - 1];
        }

        //System.out.println(Arrays.toString(normFits));

        List<Pair<Chromosome, Chromosome>> selected = new ArrayList<>();


        for (int i = 0; i < populationSize - 1; ++i) {
            final double p1 = RDG.nextUniform(0, 1);
            final double p2 = RDG.nextUniform(0, 1);

            //System.out.println("P1 = " + p1 + " P2 = " + p2);


            int indexA = IntStream.range(0, population.size())
                    //.parallel()                    
                    .filter(k -> normFits[k] >= p1)
                    .findFirst().getAsInt();

            int indexB = IntStream.range(0, population.size())
                    //.parallel()
                    .filter(k -> normFits[k] >= p2)
                    .findFirst().getAsInt();

            Pair<Chromosome, Chromosome> pair = new Pair<>(new Chromosome(population.get(indexA)),
                    new Chromosome(population.get(indexB)));

            selected.add(pair);
        }

        return selected;
    }

    private List<Chromosome> crossover(List<Pair<Chromosome, Chromosome>> population) {

        List<Chromosome> crossed = population.stream()
                .map(pair -> RDG.nextUniform(0, 1) <= crossRate? pair.first.cross(pair.second) : null)
                .filter(x ->  x != null)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        //create a new population with every element of past pop
        population.forEach(a -> {
            crossed.add(new Chromosome(a.first));
            crossed.add(new Chromosome(a.second));
        });

        return crossed;
    }

    private List<Chromosome> mutate(List<Chromosome> crossed) {
        return crossed
                    .stream()
                    //.parallelStream()
                    .map(x -> x.mutate(mutationRate, RDG))
                    .collect(Collectors.toList());
    }

    /**
     * Fitness proportional-selection
     * @param mutated mutated chromosomes
     * @return the new population with populationSize elements
     */
    private List<Chromosome> newPopulation(List<Chromosome> mutated) {

        List<Chromosome> pop = mutated.subList(0, populationSize - 1);
        pop.add(globalBest);

        return pop;
    }

    private void check(){
        int pathSize = boardSize*boardSize - 1;

        for (Chromosome c : population) {
            if(c.getValue() == pathSize)
                finished = true;
        }

    }

    private void save(int it, double min, double avg, double max) {

        try{
            writer.write(String.format("%d\t%f\t%f\t%f\n", it, min, avg, max));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }


    public void run() {
        init();

        int iteration = 1;

        while(!finished && iteration < maxIterations) {

            population.sort(Comparator.comparing(Chromosome::getValue));
            Chromosome best = population.get(populationSize - 1);
            globalBest = best.getValue() > globalBest.getValue()? best : globalBest;

            List<Pair<Chromosome, Chromosome>> pairs = select(population);
            List<Chromosome> crossed = crossover(pairs);
            List<Chromosome> mutated  = mutate(crossed);

            // compute pop fitness and select the best individual
            mutated.forEach(x -> x.eval(fitness));
            mutated.add(new Chromosome(globalBest));
            mutated.sort(Comparator.comparing(Chromosome::getValue).reversed());


            population = newPopulation(mutated);

            DoubleSummaryStatistics statistics = population.stream().mapToDouble(Chromosome::getValue).summaryStatistics();

            save(iteration, statistics.getMin(), statistics.getAverage(), statistics.getMax());
            check();
            iteration++;

            System.gc();
        }

        save(iteration, 0, 0, globalBest.getValue());

        System.out.println("Best solution: " + globalBest);
        System.out.println("Best fitness: " + globalBest.getValue());


        try{
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
