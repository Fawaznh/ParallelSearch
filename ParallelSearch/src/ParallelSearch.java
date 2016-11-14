
import java.util.*;

/**
 *
 * @author FN
 */
public class ParallelSearch 
{

    public static void main(String[] args)
    {

        //System.out.println(Runtime.getRuntime().availableProcessors());

        Graph g = new Graph();
        g.generateTree(100000);      

        g.setStart(0);
        g.setGoal(87655);
        
        
        new DFS(g,g.getStart());

    }
    

}
