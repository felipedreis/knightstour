package br.cefetmg.engcomp.fitness;

import br.cefetmg.engcomp.ga.Chromosome;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FitnessFunction
{
    protected int [][] G;

    private static int evalCounter = 0;

    private static Map<Integer, Double> functionHistory = new ConcurrentHashMap<>();

    protected synchronized static void save(double fitnessValue) {
        evalCounter++;
        functionHistory.put(evalCounter, fitnessValue);
    }

    public static void saveResults(String filename) {
        File file = new File(filename);
        BufferedWriter writer;

        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            writer = new BufferedWriter(new FileWriter((file)));

            functionHistory.forEach((key, value) -> {
                try {
                    writer.write(String.format("%d\t%f\n", key, value));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public FitnessFunction(int [][] G) {
        this.G = G;
    }
    
    protected abstract double compute(Chromosome chromosome);

    public double eval(Chromosome chromosome) {
        double value = compute(chromosome);
        save(value);
        return value;
    }

    public int[][] getG() {
        return G;
    }

    public void setG(int[][] g) {
        G = g;
    }



}
