package br.cefetmg.engcomp.ga;

import br.cefetmg.engcomp.fitness.FitnessFunction;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.Arrays;

public class Chromosome
{
    private int [] x;

    private double value;

    public Chromosome(Chromosome a) {
        this.x = Arrays.copyOf(a.x, a.x.length);
    }

    public Chromosome(int [] x) {
        this.x = x;
    }

    public double eval(FitnessFunction f) {
        value = f.eval(this);
        return value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int[] getX() {
        return x;
    }

    public void setX(int[] x) {
        this.x = x;
    }

    /**
     * OX crossing method
     * @param other
     * @return
     */
    public Chromosome [] cross(Chromosome other){
        final int N = x.length;

        RandomDataGenerator random = new RandomDataGenerator();

        int down = random.nextInt(0, N - 2);
        int up = random.nextInt(down, N - 1);

        boolean [] mark1 = new boolean[N];
        boolean [] mark2 = new boolean[N];

        int [] y1 = new int[N];
        int [] y2 = new int[N];

        Arrays.fill(y1, -1);
        Arrays.fill(y2, -1);
        Arrays.fill(mark1, false);
        Arrays.fill(mark2, false);

        for(int i = down; i <= up; ++i) {
            y1[i] = x[i];
            mark1[y1[i]] = true;

            y2[i] = other.x[i];
            mark2[y2[i]] = true;
        }

        int k1 = 0;
        int k2 = 0;

        for (int i = 0; i < N; ++i) {

            if(!mark1[other.x[i]]) {
                while(k1 < y1.length && y1[k1] != -1)
                    k1++;

                y1[k1++] = other.x[i];
                mark1[other.x[i]] = true;
            }

            if(!mark2[x[i]]) {
                while(k2 < y2.length && y2[k2] != -1)
                    k2++;

                y2[k2++] = x[i];
                mark2[x[i]] = true;
            }

        }

        return new Chromosome[] {
                new Chromosome(y1), new Chromosome(y2)
        };
    }
    
    public Chromosome mutate() {
        RandomDataGenerator random = new RandomDataGenerator();

        int [] y = Arrays.copyOf(x, x.length);
        int i, j;

        j = i = random.nextInt(0, x.length - 1);

        while(i == j)
            j = random.nextInt(0, x.length - 1);

        int aux = y[i];
        y[i] = y[j];
        y[j] = aux;

        return new Chromosome(y);
    }

    public int getGene(int i) {
        return x[i];
    }

    public int getSize(){
        return x.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chromosome that = (Chromosome) o;

        return Arrays.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(x);
    }

    @Override
    public String toString() {
        return Arrays.toString(x);
    }
}
