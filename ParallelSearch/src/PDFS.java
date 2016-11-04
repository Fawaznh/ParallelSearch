
import java.util.*;


public class PDFS extends Thread
{
    private ArrayList<Integer> path = new ArrayList<>();
    private static PDFSManager manager;
    private static Graph graph;
    private static final Object THREADLOCK = new Object();
    private static Integer goal;
    
    public PDFS()
    {
        super.start();
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
    
    public static Object getLock()
    {
        return THREADLOCK;
    }
    
    private String printPath()
    {
        String ret = "";
        for(int i = 0; i < path.size(); i++)
            ret += path.get(i) + " ";
        
        return ret;           
    }
    
    private void threadLog(String log)
    {
        System.out.println("t"+Thread.currentThread().getId()+": "+log);      
    }
    
    private void giveUp()
    {
        try
        {
            synchronized(THREADLOCK)
            {
                THREADLOCK.wait();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void run()
    {
        while(true)
        {
            path = manager.requestWork();
            Integer last = path.get(path.size() -1);
            if(last.equals(goal))
            {
                threadLog("==== FOUND GOAL ====");                        
                threadLog("path: " + printPath());
                threadLog("====================");
                long time = System.currentTimeMillis();
                System.out.println(time);
                System.out.println(time - manager.getTime());
                System.exit(0);
            }
            
            ArrayList<Integer> edges = (ArrayList)graph.outEdges(last);     
            for(Integer edge : edges)
            {
                //threadLog("Adding " + path.toString() + " " + edge);
                manager.addWork(path, edge);
            }
            if(manager.hasWork() == null)
                giveUp();
        } 
    }
}
