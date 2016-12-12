
import java.util.*;

//Single Thread DFS
public class STDFS 
{
    private final LinkedList<ArrayList<Integer>> work = new LinkedList<>();
    private ArrayList<Integer> path = new ArrayList<>();
    private Graph graph;
    private long startTime;
    private boolean isFinished;
    
    public STDFS(Graph graph)
    {  
        this.graph = graph;
        ArrayList<Integer> a = new ArrayList<>();
        a.add(graph.getStart());
        work.add(a);
    }
    
    public ArrayList<Integer> requestWork()
    {

        return work.pollLast();
    }
    
    public long process()
    {
        //System.out.println(graph.getGoal());
        startTime = System.currentTimeMillis(); 
        while(!isFinished)
        {
            path = requestWork();
            //System.out.println(path);
            Integer last = path.get(path.size() - 1);
            //System.out.println(last);
            if(last.equals(graph.getGoal()))
            {  
                //System.out.println("==== FOUND GOAL ====");
                //System.out.println("path: " + s);
                //System.out.println("====================");  
                return System.currentTimeMillis() - startTime;
            }
            
            ArrayList<Integer> edges = (ArrayList)graph.outEdges(last);     
            for(Integer edge : edges)
                addWork(path, edge);
        } 
        return -1;
    }
   
    public void addWork(ArrayList<Integer> list, Integer i)
    {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.addAll(list);
        temp.add(i);
        work.add(temp);
    }
}
