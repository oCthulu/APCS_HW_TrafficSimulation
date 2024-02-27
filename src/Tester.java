import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Main tester class. Simulates traffic across a range of obstacle counts.
 * For APCSA project due 2/27/24.
 * @author Andrew Denton
 * @author Kieran Chalk
 */
public class Tester {
    public static void main(String[] args) {
        //Create the graph. The graph object will be reused.
        JsonElement json;
        TrafficGraph graph;
        try {
            String jsonText = readFromStream(Tester.class.getResourceAsStream("./graph.json"));
            json = JsonParser.parseString(jsonText);
        }
        catch (JsonSyntaxException e){
            System.out.println("Received malformed JSON file. Ensure the JSON file has no syntax errors.");
            return;
        }

        try {
            graph = TrafficGraph.loadTrafficGraphFromJson(json.getAsJsonObject());
        }
        catch (Exception e){
            System.out.println("JSON file is formatted improperly. Make sure the file has all the proper fields.");
            return;
        }

        //---------------------------------------simulation---------------------------------------
        //The amount of simulations to run per number of obstacles.
        final int SIMULATION_COUNT = 250_000;
        //Amount of simulations to run before re-randomizing obstacles.
        final int BATCH_SIZE = 5;
        //The max amount of obstacles to simulate. The program will simulate obstacle counts from 0 to this number
        final int MAX_OBSTACLES = 15;
        //The maximum amount of iterations to run per simulation
        final int MAX_PATH_LENGTH = 100;

        final Random random = new Random();

        System.out.println("Simulating traffic...");
        long simulationStartTime = System.nanoTime();

        int[][] results = new int[MAX_OBSTACLES][SIMULATION_COUNT];
        for (int obstacleCount = 0; obstacleCount < MAX_OBSTACLES; obstacleCount++) {
            for (int i = 0; i < SIMULATION_COUNT; i++) {
                if (i % BATCH_SIZE == 0) {
                    //every BATCH_SIZE simulations, reset the graph, place the obstacles, and rebake the decision logic
                    graph.restoreBaseState();
                    graph.placeObstacles(obstacleCount, random);
                    graph.bakeDecisionLogic();
                }

                //simulate and store into the graph
                results[obstacleCount][i] = graph.runTraverseSimulation(MAX_PATH_LENGTH, random);
            }
        }

        long simulationEndTime = System.nanoTime();
        System.out.println("Simulation finished! (" + (simulationEndTime - simulationStartTime) / 1_000_000_000 + "s)");

        //---------------------------------------data processing---------------------------------------
        DatapointSummary[] data = Arrays.stream(results)
                .map(row -> {
                    Arrays.sort(row);
                    double mean = Arrays.stream(row)
                            .average()
                            .orElseThrow();

                    double standardDeviation = Arrays.stream(row)
                            .mapToDouble(val -> val - mean)
                            .map(val -> val * val)
                            .sum();

                    standardDeviation /= (row.length - 1);
                    standardDeviation = Math.sqrt(standardDeviation);

                    double min = row[0];
                    double q1 = row[(row.length / 4) - 1];
                    double median = row[(row.length / 2) - 1];
                    double q3 = row[(row.length * 3 / 4) - 1];
                    double max = row[row.length - 1];

                    return new DatapointSummary(mean, standardDeviation, min, q1, median, q3, max);
                })
                .toArray(DatapointSummary[]::new);

        //---------------------------------------data saving---------------------------------------
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to save the results to a file? (y/n)");
        if(scanner.next().equalsIgnoreCase("y")){
            System.out.println("Note that the save dialog may not be focused, so you might need to minimise this " +
                    "program.");
            JFileChooser fc = new JFileChooser();
            fc.setVisible(true);
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                File file = fc.getSelectedFile();
                saveData(file, data);
            }
        }
    }

    public static void saveData(File file, DatapointSummary[] data){
        try {
            FileWriter writer = new FileWriter(file);
            saveDataRow(writer, IntStream.range(0, data.length).boxed(), "Obstacle Count", (Object::toString));
            saveDataRow(writer, Arrays.stream(data), "Mean", (DatapointSummary::mean));
            saveDataRow(writer, Arrays.stream(data), "Standard Deviation", (DatapointSummary::standardDeviation));
            saveDataRow(writer, Arrays.stream(data), "Minimum", (DatapointSummary::min));
            saveDataRow(writer, Arrays.stream(data), "First Quartile", (DatapointSummary::firstQuartile));
            saveDataRow(writer, Arrays.stream(data), "Median", (DatapointSummary::median));
            saveDataRow(writer, Arrays.stream(data), "Third Quartile", (DatapointSummary::thirdQuartile));
            saveDataRow(writer, Arrays.stream(data), "Maximum", (DatapointSummary::max));
            writer.close();
        }
        catch (IOException e){
            System.out.println("Error writing file. Make sure this process has permissions to write to the specified " +
                    "location, and that no other processes are accessing the file.");
        }
    }

    public static <T> void saveDataRow(FileWriter file, Stream<T> data, String name, Function<T, Object> mapping)
            throws IOException{
        file.write(name);
        file.write(
                data.map(mapping)
                .map(v -> "," + v.toString())
                .collect(Collectors.joining())
        );
        file.write("\n");
    }

    public static String readFromStream(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return String.join("\n", reader.lines().toList());
    }
}