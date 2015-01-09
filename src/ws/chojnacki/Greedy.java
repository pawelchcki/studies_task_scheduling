package ws.chojnacki;

import java.util.List;

public class Greedy extends Processor {
    int findLowestUsedCpu(int[] cpus) {
        int low_i = 0;
        for (int i = 0; i < cpus.length; i++) {
            if (cpus[i] <= cpus[low_i]) {
                low_i = i;
            }
        }
        return low_i;
    }

    Integer process(List<Integer> procs, int numCpus) {
        procs = copyProcs(procs);
        int[] originalCpus = new int[numCpus];

        for (Integer proc : procs) {
            int i = findLowestUsedCpu(originalCpus);
            originalCpus[i] += proc;
        }
        return maxTime(originalCpus);
    }

}
