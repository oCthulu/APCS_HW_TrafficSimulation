import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import graphs.Graph;
import graphs.Node;

import java.io.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        String jsonText = readFromStream(Main.class.getResourceAsStream("./graph.json"));
        JsonElement json = JsonParser.parseString(jsonText);
        TrafficGraph graph = TrafficGraph.loadTrafficGraphFromJson(json.getAsJsonObject());


        graph.runTraverseSimulation(100, new Random());
    }

    public static String readFromStream(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return String.join("\n", reader.lines().toList());
    }
}