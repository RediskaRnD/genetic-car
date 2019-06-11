package tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.testng.Assert.*;
import org.tensorflow.Graph;
import tensor.TensorflowGraph;

public class TensorflowGraphTest {

    @org.testng.annotations.Test
    public void testRunGraph() {
        Graph graph = TensorflowGraph.createGraph();
        Object result = TensorflowGraph.runGraph(graph, 3.0, 6.0);
        assertEquals(21.0, result);
        System.out.println(result);
        graph.close();
    }
}