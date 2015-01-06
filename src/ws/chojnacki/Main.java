package ws.chojnacki;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final int NOT_SET = -1;

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

    int process(List<Integer> procs, int[] originalCpus, int iCpu, int procVal, int mTime) {
        int rv;
        originalCpus[iCpu] += procVal;

        if (procs.isEmpty()) {
            rv = maxTime(originalCpus);
            if (mTime != NOT_SET && rv > mTime) {
                rv = mTime;
            }
        } else {
            int nProc = procs.size();
            for (int procNumber = 0; procNumber < nProc; procNumber++) {
                int tProcVal = procs.remove(procNumber);

                for (int cpuNumber = 0; cpuNumber < originalCpus.length; cpuNumber++) {
                    mTime = process(procs, originalCpus, cpuNumber, tProcVal, mTime);
                }
                procs.add(procNumber, tProcVal);
            }
            rv = mTime;
        }
        originalCpus[iCpu] -= procVal;
        return rv;
    }

    public static void main(String[] args) {
        int[] procs = {5, 5, 4, 4, 3, 3, 3};
//        |5 4 3 3
//        |5 4 3
//        |5 4 3
        Main m = new Main();
        int x = m.process(m.initProcs(procs), m.initCpus(3), 0, 0, NOT_SET);
        System.err.println(x);
    }
}
