package ws.chojnacki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ExhaustiveThreaded extends Exhaustive {
    Integer process(List<Integer> procs, int numCpus) {
        List<Integer> rvList = Collections.synchronizedList(new ArrayList<>());
        Function<Integer, Runnable> r = (Integer numProc) -> () -> {
            List<Integer> nuProc = copyProcs(procs);
            int[] nuCpus = new int[numCpus];
            int procVal = nuProc.remove(numProc.intValue());
            int mTime = NOT_SET;
            for (int i = 0; i < numCpus; i++) {
                mTime = internalProcess(nuProc, nuCpus, i, procVal, mTime);
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

}
