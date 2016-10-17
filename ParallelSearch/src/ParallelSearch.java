
import java.util.*;

/**
 *
 * @author FN
 */
public class ParallelSearch 
{
    
    
    public static List<DFS> dfsThreads; //shared
    

    public static void main(String[] args)
    {

        dfsThreads = new ArrayList<>();
        Graph g = new Graph();
        g.setStart(5);
        g.setGoal(767);
        
        generateTree(g);
        
        /*g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(2, 5);
        g.addEdge(2, 6);
        g.addEdge(3, 7);
        g.addEdge(3, 8);

        g.addEdge(4, 5);
        */
        dfsThreads.add(new DFS(g,g.getStart()));
        dfsThreads.add(new DFS(g,g.getStart()));
        dfsThreads.add(new DFS(g,g.getStart()));
        dfsThreads.add(new DFS(g,g.getStart()));
        
        // only activate one thread, others will get work from it
        dfsThreads.get(0).activate(); 
      
        

    }
    
    
    public static void generateTree(Graph g)
    {
       int i = 0;
       int max = 1000;
       
       while(i < max)
       {
           int parent = i;
           int left= (i*2)+1;
           int right= (i*2)+2;
           
           i++;
           
           
           g.addEdge(parent, left);
           g.addEdge(parent, right);
           
           //System.out.println("node: "+parent+"  "+left+"  "+right);
       }
       
    }
    
    
}
