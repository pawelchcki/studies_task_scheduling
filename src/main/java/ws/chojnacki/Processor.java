package ws.chojnacki;

import com.google.common.base.Stopwatch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public abstract class Processor {
    static final int NOT_SET = -1;

    static int maxTime(int[] pCpus) {
        int max = NOT_SET;
        for (int cpu : pCpus) {
            if (cpu > max) {
                max = cpu;
            }
        }
        return max;
    }

    protected List<Integer> copyProcs(List<Integer> list) {
        List<Integer> nuProc = new LinkedList<>(list);
        Collections.copy(nuProc, list);
        return nuProc;
    }

    abstract Integer process(List<Integer> processes, int nCpus);

    void toCSV(List<Integer> processes, int nCpus) {
        int size = processes.size();
        Stopwatch timer = Stopwatch.createStarted();
        Integer result = process(processes, nCpus);
        timer.stop();
        System.out.println(String.format("%d,%d,%d,%d", nCpus, size, result, timer.elapsed(TimeUnit.MICROSECONDS)));
    }
}
