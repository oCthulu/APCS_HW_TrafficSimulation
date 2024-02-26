import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Main tester class. Simulates traffic across a range of obstacle counts.
 * For APCSA project due 2/27/24.
 * @author Andrew Denton
 * @author Kieran Chalk
 */
public class Tester {
    public static void main(String[] args) {
        //Create the graph. The graph object will be reused.
        String jsonText = readFromStream(Tester.class.getResourceAsStream("./graph.json"));
        JsonElement json = JsonParser.parseString(jsonText);
        TrafficGraph graph = TrafficGraph.loadTrafficGraphFromJson(json.getAsJsonObject());

        //---------------------------------------simulation---------------------------------------
        final int SIMULATION_COUNT = 250_000;
        final int BATCH_SIZE = 5;
        final int MAX_OBSTACLES = 15;
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
                    graph.placeObstacles(obstacleCount, new Random());
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
        System.out.println("Would you like to save the results to a file (does not work yet)? (y/n)");
        if(scanner.next().equalsIgnoreCase("y")){
            JFileChooser fc = new JFileChooser();
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                //TODO: file saving
                File file = fc.getSelectedFile();
            }
        }
    }

    public static void saveData(File file, DatapointSummary[] data){
        try {
            FileWriter writer = new FileWriter(file);
            //TODO: save the file
            writer.close();
        }
        catch (IOException e){
            System.out.println("Error writing file. Make sure this process has permissions to write to the specified " +
                    "location, and that no other processes are accessing the file.");
        }
    }

    public static String readFromStream(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return String.join("\n", reader.lines().toList());
    }
}