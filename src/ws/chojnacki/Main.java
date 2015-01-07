package ws.chojnacki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
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

    int multiProcess(List<Integer> procs, int[] originalCpus){
        List<Integer> rvList = Collections.synchronizedList(new ArrayList<>());
        Function<Integer, Runnable> r = (Integer numProc) -> () -> {
            List<Integer> nuProc = new LinkedList<>(procs);
            Collections.copy(nuProc, procs);
            int[] nuCpus = originalCpus.clone();
            int procVal = nuProc.remove(numProc.intValue());
            int mTime = NOT_SET;
            for (int i=0; i<originalCpus.length; i++){
                mTime = process(nuProc, nuCpus, i, procVal, mTime);
            }
            rvList.add(mTime);
        };
        List<Thread> threads = new LinkedList<>();
        for(int i=0; i< procs.size(); i++){
            Thread th = new Thread(r.apply(i));
            threads.add(th);
            th.start();
        }
        for (Thread th: threads) {
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

    public static void main(String[] args) {
        int[] procs = {5, 5, 4, 4, 3, 3, 3, 1, 2};
        Main m = new Main();
//        int x = m.process(m.initProcs(procs), m.initCpus(3), 0, 0, NOT_SET);
        int x = m.multiProcess(m.initProcs(procs), m.initCpus(3));

        System.err.println(x);
    }
}
