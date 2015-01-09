package ws.chojnacki;

import java.util.Collections;
import java.util.List;

public class Lpt extends Greedy {
    Integer process(List<Integer> procs, int numCpus) {
        procs = copyProcs(procs);
        Collections.sort(procs, Collections.reverseOrder());

        return super.process(procs, numCpus);
    }
}
