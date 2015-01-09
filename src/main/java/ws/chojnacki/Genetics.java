package ws.chojnacki;

import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;

import java.util.List;
import java.util.function.Function;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

final class FF implements Function<Genotype<EnumGene<Integer>>, Double> {
    private final List<Integer> items;
    private final int nCpu;

    public FF(final List<Integer> items, final int nCpu) {
        this.items = items;
        this.nCpu = nCpu;
    }

    @Override
    public Double apply(final Genotype<EnumGene<Integer>> gt) {
        int cnt = 0;
        int[] results = new int[nCpu];
        int iCpu = 0;

        for (EnumGene<Integer> gene : gt.getChromosome().toSeq()) {
            if (cnt >= items.size()) {
                iCpu++;
                cnt = 0;
            }
            int i = gene.getAllele().intValue();
            if (i < items.size()) {
                results[iCpu] += items.get(i);
            }
            cnt++;

        }
        int maxTime = Genetics.maxTime(results);
        return (double) (maxTime);
    }
}

// The main class.
public class Genetics extends Processor {
    Integer process(List<Integer> procs, int nCpus) {
        int nItems = procs.size();

        final FF ff = new FF(
                procs,
                nCpus
        );

        final Engine<EnumGene<Integer>, Double> engine = Engine
                .builder(ff, PermutationChromosome.ofInteger(nItems * nCpus))
                .populationSize(100)
                .optimize(Optimize.MINIMUM)
                .survivorsSelector(new TournamentSelector<>(5))
                .offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.115),
                        new SinglePointCrossover<>(0.16))
                .build();

        final EvolutionStatistics<Double, ?>
                statistics = EvolutionStatistics.ofNumber();

        final Phenotype<EnumGene<Integer>, Double> best = engine.stream()
                .limit(bySteadyFitness(7))
                .limit(100)
                .peek(statistics)
                .collect(toBestPhenotype());

        return best.getFitness().intValue();
    }
}
