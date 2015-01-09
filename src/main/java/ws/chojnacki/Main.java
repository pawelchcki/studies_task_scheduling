package ws.chojnacki;

import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

class Helper {
    Integer nCpus;
    List<Integer> processes;

    public void readFile(String fname) throws IOException {
        Stream<Integer> intStream = Files.lines(Paths.get(fname)).skip(1).map((s) -> Integer.parseInt(s.split(" ")[0]));
        intStream = intStream.peek((i -> this.nCpus = i));
        intStream = intStream.skip(1);

        this.processes = Arrays.asList(intStream.toArray(Integer[]::new));
    }

    public static void generateSamples(int nCpus, int nProc, int minTime, int maxTime) throws IOException {
        Random r = new Random();

        Stream<String> randomStream = Stream.generate(() -> r.nextInt(maxTime - minTime) + minTime).limit(nProc).map((i) -> i.toString());
        Stream<String> infoStream = Stream.of("" + nProc, "" + nCpus);

        Files.write(Paths.get("samples", nCpus + "-" + nProc), (Iterable<String>) Stream.concat(infoStream, randomStream)::iterator);
    }
}


public class Main {
    public static void main(String[] args) {
        try {
//            String[] tmp = {"ws.chojnacki.Greedy", "./lpt.txt"};
//            String[] tmp = {"ws.chojnacki.Exhaustive", "./lpt.txt"};
            String[] tmp = {"ws.chojnacki.ExhaustiveThreaded", "./lpt.txt"};
            args = tmp;
            if (args.length > 1) {
                String klassName = args[0];
                Processor processor = (Processor) ClassLoader.getSystemClassLoader().loadClass(klassName).newInstance();
                Helper x = new Helper();
                x.readFile(args[1]);


                processor.toCSV(x.processes, x.nCpus);

            } else {
                System.err.println("too few vs");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
