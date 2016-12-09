
import java.util.*;


public class PDFS extends Thread
{
    private ArrayList<Integer> path = new ArrayList<>();
    private static PDFSManager manager;
    private static Graph graph;
    private static Integer goal;
    private long time;
    private long startTime;
    private boolean isFinished;
    
    public PDFS()
    {
       startTime = System.currentTimeMillis(); 
    }
    public static void setManager(PDFSManager m)
    {
        manager = m;
    }
    
    public static void setGraph(Graph g)
    {
        graph = g;
        goal = graph.getGoal();
    }  
    
    private void printGoal() throws Exception
    {
        time = System.currentTimeMillis() - startTime;
        isFinished = true;
        //System.out.println("==== FOUND GOAL ====");
        //System.out.println("path: " + path);
        //System.out.println("====================");                   
        synchronized(manager)
         {
            manager.setResult(time);
            manager.notify();
        }
    }
    public void run()
    {
        while(!isFinished)
        {
            path = manager.requestWork();

            Integer last = path.get(path.size() - 1);
            if(last.equals(goal))
            {
                try
                {
                    printGoal();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
            
            //System.out.println(last);
            ArrayList<Integer> edges = (ArrayList)graph.outEdges(last);     
            for(Integer edge : edges)
            {
                //threadLog("Adding " + path.toString() + " " + edge);
                manager.addWork(path, edge);
            }
        } 
    }
}
