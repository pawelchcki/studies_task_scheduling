package ws.chojnacki;

import java.util.LinkedList;
import java.util.List;

public class Exhaustive extends Processor {

    Integer process(List<Integer> processes, int nCpus) {
        return internalProcess(processes, new int[nCpus], 0, 0, NOT_SET);
    }

    protected int internalProcess(List<Integer> procs, int[] cpus, int iCpu, int procVal, int mTime) {
        int rv;
        procs = new LinkedList<>(procs);
        cpus[iCpu] += procVal;

        if (procs.isEmpty()) {
            rv = maxTime(cpus);
            if (mTime != NOT_SET && rv > mTime) {
                rv = mTime;
            }
        } else {
            int nProc = procs.size();
            for (int procNumber = 0; procNumber < nProc; procNumber++) {
                int tProcVal = procs.remove(procNumber);

                for (int cpuNumber = 0; cpuNumber < cpus.length; cpuNumber++) {
                    mTime = internalProcess(procs, cpus, cpuNumber, tProcVal, mTime);
                }
                procs.add(procNumber, tProcVal);
            }
            rv = mTime;
        }
        cpus[iCpu] -= procVal;
        return rv;
    }
}
