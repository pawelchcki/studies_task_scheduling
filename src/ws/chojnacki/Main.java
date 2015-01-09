package ws.chojnacki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;


public class Main {
    public static final int NOT_SET = -1;

    int[] initCpus(int num) {
        int[] rv = new int[num];
        return rv;
    }

    List<Integer> initProcs(int[] procs) {
        List<Integer> rv = new LinkedList<Integer>();
        for (int proc : procs) {
            rv.add(proc);
        }
        return rv;
    }

    static int maxTime(int[] pCpus) {
        int max = NOT_SET;
        for (int cpu : pCpus) {
            if (cpu > max) {
                max = cpu;
            }
        }
        return max;
    }

    static void dispCpus(int[] cpus) {
        for (int cpu : cpus) {
            System.err.print(cpu + " ");
        }
        System.err.println("");
    }

    List<Integer> copyProcs(List<Integer> list){
        List<Integer> nuProc = new LinkedList<>(list);
        Collections.copy(nuProc, list);
        return nuProc;
    }

    int[] copyCpus(int[] cpus) {
        return cpus.clone();
    }

    int multiProcess(List<Integer> procs, int[] originalCpus) {
        List<Integer> rvList = Collections.synchronizedList(new ArrayList<>());
        Function<Integer, Runnable> r = (Integer numProc) -> () -> {
            List<Integer> nuProc = copyProcs(procs);
            int[] nuCpus = copyCpus(originalCpus);
            int procVal = nuProc.remove(numProc.intValue());
            int mTime = NOT_SET;
            for (int i = 0; i < originalCpus.length; i++) {
                mTime = process(nuProc, nuCpus, i, procVal, mTime);
            }
            rvList.add(mTime);
        };
        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < procs.size(); i++) {
            Thread th = new Thread(r.apply(i));
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return Collections.min(rvList);
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

    int findLowestUsedCpu(int[] cpus) {
        int low_i = 0;
        for (int i = 0; i < cpus.length; i++) {
            if (cpus[i] <= cpus[low_i]) {
                low_i = i;
            }
        }
        return low_i;
    }

    int greedyProcess(List<Integer> procs, int[] originalCpus) {
        procs = copyProcs(procs);
        originalCpus = copyCpus(originalCpus);

        for (Integer proc : procs) {
            int i = findLowestUsedCpu(originalCpus);
            originalCpus[i] += proc;
        }
        return maxTime(originalCpus);
    }

    int lptProcess(List<Integer> procs, int[] originalCpus) {
        procs = copyProcs(procs);
        originalCpus = copyCpus(originalCpus);
        Collections.sort(procs, Collections.reverseOrder());

        return greedyProcess(procs, originalCpus);
    }

    public static void main(String[] args) {
        int[] procs = {5, 5, 4, 4, 3, 3, 6, 2};
        Main m = new Main();
        List<Integer> lprocs = m.initProcs(procs);
        int [] cpus = m.initCpus(3);
        int x = m.process(m.initProcs(procs), m.initCpus(3), 0, 0, NOT_SET);
//        System.err.println(m.multiProcess(lprocs, cpus));
        System.err.println(m.greedyProcess(lprocs, cpus));
        System.err.println(m.lptProcess(lprocs, cpus));

//        System.err.println(x);
    }
}
