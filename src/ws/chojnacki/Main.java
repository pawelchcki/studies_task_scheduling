package ws.chojnacki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    int nCpu = 3;
    int[] procs;
    public static final int NOT_SET = -1;
    public static final int IGNORE = -1;

    int[] initCpus(int num) {
        int[] rv = new int[num];
        return rv;
    }

    List<Integer> initProcs(int[] procs) {
        List<Integer> rv = new ArrayList<Integer>(procs.length);
        for (int proc : procs) {
            rv.add(proc);
        }
        return rv;
    }

    int maxTime(int[] pCpus) {
        int max = NOT_SET;
        for (int cpu : pCpus) {
            if (cpu > max) {
                max = cpu;
            }
        }
        return max;
    }

    void dispCpus(int[] cpus) {
        for (int cpu : cpus) {
            System.err.print(cpu + " ");
        }
        System.err.println("");
    }

    int process(List<Integer> procs, int[] originalCpus, int iProc, int iCpu) {
        if (iProc != IGNORE && iCpu != IGNORE) {
            originalCpus[iCpu] += procs.remove(iProc);
        }
        int mTime = NOT_SET, tTime;
        if (procs.size() == 0) {
            return maxTime(originalCpus);
        } else {
            int nProc = procs.size();

            for (int cpuNumber = 0; cpuNumber < originalCpus.length; cpuNumber++) {
                for (int procNumber = 0; procNumber < nProc; procNumber++) {
                    int[] cpus = originalCpus.clone();
                    List<Integer> tProcs = new ArrayList<Integer>(procs);
                    Collections.copy(tProcs, procs);
                    tTime = process(tProcs, cpus, procNumber, cpuNumber);
                    if (tTime > 0 && (mTime == NOT_SET || tTime < mTime)) {
                        mTime = tTime;
                    }
                }
            }
            return mTime;
        }
    }

    public static void main(String[] args) {
        int[] procs = {5, 5, 4, 4, 3, 3, 3, 1};
//        |5 4 3 3
//        |5 4 3
//        |5 4 3
        Main m = new Main();
        int x = m.process(m.initProcs(procs), m.initCpus(3), IGNORE, IGNORE);
        System.err.println(x);
    }
}
