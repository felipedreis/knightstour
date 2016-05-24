package br.cefetmg.engcomp;

import br.cefetmg.engcomp.ga.Chromosome;
import com.google.code.tempusfugit.concurrency.IntermittentTestRunner;
import com.google.code.tempusfugit.concurrency.annotations.Intermittent;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for chromosome methods
 * @author Felipe Duarte
 */
@RunWith(IntermittentTestRunner.class)
public class ChromosomeTest {

    Chromosome a, b;

    @Before
    public void init(){
        RandomDataGenerator random = new RandomDataGenerator();


        a = new Chromosome(random.nextPermutation(100, 100));
        b = new Chromosome(random.nextPermutation(100, 100));
    }

    @Test
    @Intermittent
    public void testCrossover(){

        Chromosome [] res = a.cross(b);
        // TODO it's possible to guarantee different child?
        //assertThat(res[0], allOf(is(not(a)), is(not(b))));
        //assertThat(res[1], allOf(is(not(a)), is(not(b))));

        for(int i = 0; i < a.getSize(); ++i) {
            assertThat(res[0].getGene(i), is(not(-1)));
            assertThat(res[1].getGene(i), is(not(-1)));
        }
    }

    @Test
    @Intermittent
    public void testMutation() {
        Chromosome c = a.mutate();

        assertThat(c, is(not(a)));
    }
}
