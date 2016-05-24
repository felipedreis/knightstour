package br.cefetmg.engcomp.fitness;

import br.cefetmg.engcomp.ga.Chromosome;

public abstract class FitnessFunction
{
    protected int [][] G;
    
    public FitnessFunction(int [][] G) {
        this.G = G;
    }
    
    public abstract double eval(Chromosome chromosome);

    public int[][] getG() {
        return G;
    }

    public void setG(int[][] g) {
        G = g;
    }
}
