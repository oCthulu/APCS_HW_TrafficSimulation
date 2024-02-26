import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import graphs.Graph;
import graphs.Node;

import java.io.*;
import java.util.Random;

public class Tester {
    public static void main(String[] args) {
        String jsonText = readFromStream(Tester.class.getResourceAsStream("./graph.json"));
        JsonElement json = JsonParser.parseString(jsonText);
        TrafficGraph graph = TrafficGraph.loadTrafficGraphFromJson(json.getAsJsonObject());


        graph.runTraverseSimulation(100, new Random());
        int meanCount = 0;
        int trialsRun = 250_000;
        int [] resultArray = new int [trialsRun];
        for (int i = 0; i<trialsRun; i++){
            if(i %10 == 0){
                graph.restoreBaseState();
                graph.placeObstacles(30,new Random());
                graph.bakeDecisionLogic();
            }
            resultArray[i] = graph.runTraverseSimulation(100,new Random());
        }
        for (int i = 0; i<resultArray.length;i++){
            meanCount += resultArray[i];
        }
        int mean = meanCount/trialsRun;
    }

    public static String readFromStream(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return String.join("\n", reader.lines().toList());
    }
}