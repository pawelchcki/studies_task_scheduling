package ws.chojnacki;

import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.util.RandomRegistry;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

final class FF implements Function<Genotype<EnumGene<Integer>>, Double> {
    private final int[] items;
    private final int nCpu;

    public FF(final int[] items, final int nCpu) {
        this.items = items;
        this.nCpu = nCpu;
    }

    @Override
    public Double apply(final Genotype<EnumGene<Integer>> gt) {
        int cnt = 0;
        int[] results = new int[nCpu];
        int iCpu = 0;

        for (EnumGene<Integer> gene : gt.getChromosome().toSeq()) {
            if (cnt >= items.length) {
                iCpu++;
                cnt = 0;
            }
            int i = gene.getAllele().intValue();
            if (i < items.length) {
                results[iCpu] += items[i];
            }
            cnt++;

        }
        int maxTime = Main.maxTime(results);
        return (double)(maxTime);
    }
}

// The main class.
public class Genetics {

    public static void main(final String[] args) {
        final int ncpus = 3;

        int[] procs = {5, 5, 4, 4, 3, 3, 3};
        int nitems = procs.length;

        final FF ff = new FF(
                procs,
                ncpus
        );

        // Configure and build the evolution engine.
        final Engine<EnumGene<Integer>, Double> engine = Engine
                .builder(ff, PermutationChromosome.ofInteger(nitems * ncpus))
                .populationSize(100)
                .optimize(Optimize.MINIMUM)
                .survivorsSelector(new TournamentSelector<>(5))
                .offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.115),
                        new SinglePointCrossover<>(0.16))
                .build();

        // Create evolution statistics consumer.
        final EvolutionStatistics<Double, ?>
                statistics = EvolutionStatistics.ofNumber();

        final Phenotype<EnumGene<Integer>, Double> best = engine.stream()
                // Truncate the evolution stream after 7 "steady"
                // generations.
                .limit(bySteadyFitness(7))
                        // The evolution will stop after maximal 100
                        // generations.
                .limit(100)
                        // Update the evaluation statistics after
                        // each generation
                .peek(statistics)
                        // Collect (reduce) the evolution stream to
                        // its best phenotype.
                .collect(toBestPhenotype());

//        System.out.println(statistics);
//        System.out.println(best);
        System.err.println(best.getFitness().intValue());
    }
}
