package br.cefetmg.engcomp.fitness;

import br.cefetmg.engcomp.ga.Chromosome;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FitnessFunction
{
    protected int [][] G;

    private static int evalCounter = 0;
    private static double lastEval = Double.MIN_VALUE;

    private static Map<Integer, Double> functionHistory = new ConcurrentHashMap<>();

    /*
    protected synchronized static void save(double fitnessValue) {
        evalCounter++;
        lastEval = Double.max(lastEval, fitnessValue);
        functionHistory.put(evalCounter, lastEval);
    }*/



    public FitnessFunction(int [][] G) {
        this.G = G;
    }
    
    protected abstract double compute(Chromosome chromosome);

    public double eval(Chromosome chromosome) {
        double value = compute(chromosome);
        //save(value);
        return value;
    }

    public int[][] getG() {
        return G;
    }

    public void setG(int[][] g) {
        G = g;
    }
}
