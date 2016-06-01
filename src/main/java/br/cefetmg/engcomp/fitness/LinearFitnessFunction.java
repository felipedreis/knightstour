package br.cefetmg.engcomp.fitness;

import br.cefetmg.engcomp.ga.Chromosome;

/**
 * Created by felipe on 23/05/16.
 */
public class LinearFitnessFunction extends FitnessFunction {

    public LinearFitnessFunction(int [][] G) {
        super(G);
    }

    @Override
    public double compute(Chromosome chromosome) {
        double res = 0;

        for (int i = 0; i < chromosome.getSize() - 1; ++i) {

            int u = chromosome.getGene(i);
            int v = chromosome.getGene(i + 1);

            if(G[u][v] != 0) {
                res += 1;
            }
        }

        return res;
    }
}
