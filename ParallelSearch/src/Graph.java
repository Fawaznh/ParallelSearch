
import java.util.*;

/**
 *
 * @author FN
 */
public class Graph 
{
    private Integer start;
    private Integer goal;
    
    private HashMap<Integer, ArrayList<Integer>> adj = new HashMap<>();
    
    private List<Integer> pathFound;
    
    public Graph()
    {

    }
    
    public void addEdge(int i, int j) 
    {
        if(!adj.containsKey(i))
            adj.put(i, new ArrayList<>());
        if(!adj.containsKey(j))
            adj.put(j, new ArrayList<>());
        
         adj.get(i).add(j);
    }
    
    public List<Integer> outEdges(int i) 
    {
         return adj.get(i);
    }
    
    public Integer getStart()
    {
        return start;
    }
    public Integer getGoal()
    {
        return goal;
    }
    public void setStart(Integer start) 
    {
        this.start = start;
    }     
    public void setGoal(Integer goal) 
    {
        this.goal = goal;
    } 
    
    public void setPath(List<Integer> path)
    {
        pathFound = path;
    }
    public void generateTree(int max)
    {
        for(int i= 0;i < max;i++)
        {
           addEdge(i, (i*2)+1);
           addEdge(i, (i*2)+2);
        }
    }
    
    public String toString()
    {
        if(pathFound == null) return "Not found!";
        
        Iterator<Integer> iter = pathFound.iterator();
        String path = iter.next().toString();
        while (iter.hasNext())
            path += "-->" + iter.next().toString();
        
        return path;
    }


    
}
