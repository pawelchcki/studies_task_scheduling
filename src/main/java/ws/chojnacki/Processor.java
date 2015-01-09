package ws.chojnacki;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


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
}
