package br.cefetmg.engcomp.fitness;

import br.cefetmg.engcomp.ga.Chromosome;

/**
 * Created by felipe on 01/06/16.
 */
public class QuadraticFitnessFunction extends FitnessFunction {

    public QuadraticFitnessFunction(int[][] G) {
        super(G);
    }

    @Override
    protected double compute(Chromosome chromosome) {
        double firstTerm = 0;
        double edgeCount = 0;
        double consecutiveEdgeCount = 0;

        for (int i = 0; i < chromosome.getSize() - 1; ++i) {

            int u = chromosome.getGene(i);
            int v = chromosome.getGene(i + 1);

            if(G[u][v] != 0) {
                edgeCount += 1;
                consecutiveEdgeCount += 1;
            } if(G[u][v] == 0) {
                firstTerm = Math.max(consecutiveEdgeCount, firstTerm);
                consecutiveEdgeCount = 0;
            }
        }

        firstTerm = Math.max(consecutiveEdgeCount, firstTerm);

        return Math.pow(firstTerm, 2) + edgeCount;
    }
}
