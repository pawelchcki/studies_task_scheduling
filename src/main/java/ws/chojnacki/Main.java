package ws.chojnacki;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

class Helper {
    Integer nCpus;
    List<Integer> processes;

    public void readFile(String fname) {
        readFile(Paths.get(fname));
    }

    public void readFile(Path path) {
        Stream<Integer> intStream = null;
        try {
            intStream = Files.lines(path).skip(1).map((s) -> Integer.parseInt(s.split(" ")[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.processes = new LinkedList<>(Arrays.asList(intStream.toArray(Integer[]::new)));
        this.nCpus = this.processes.get(0);
        this.processes.remove(0);

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
        Map<Class, Integer> all = new HashMap<>();
        all.put(Exhaustive.class, 7);
        all.put(ExhaustiveThreaded.class, 8);
        all.put(Genetics.class, 99999);
        all.put(Greedy.class, 99999);
        all.put(Lpt.class, 99999);
//
        try {
            if (args.length > 1) {
                String klassName = args[0];
                Processor processor = (Processor) ClassLoader.getSystemClassLoader().loadClass(klassName).newInstance();
                Helper x = new Helper();
                x.readFile(args[1]);

                processor.toCSV(x.processes, x.nCpus);
            } else {
                Files.list(Paths.get("samples")).forEach((path) -> {
                    Helper helper = new Helper();
                    helper.readFile(path);
                    for (Class klass: all.keySet()){
                        if (helper.processes.size() < all.get(klass)){
                            try {
                                Processor processor = (Processor)klass.newInstance();
                                processor.toCSV(helper.processes, helper.nCpus);
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
//                    System.exit(1);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
