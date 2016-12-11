import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FN
 * 
 * dynamic load balancing
 * http://parallelcomp.uw.hu/ch11lev1sec4.html
 * 
 */
public class DFSV3 extends Thread
{
    private static Object lock;
    private static long startTime;
    private static long time;
    //private static boolean isFound = false;
    private static ConcurrentLinkedDeque<DFSV3> workers;
    public static int maxThreads;
    private static Graph g;
    
    private ArrayList<Integer> edges;
    private Integer currentInt;
    private ArrayList<Integer> s = new ArrayList<>(); // start node
    private LinkedList<ArrayList<Integer>> stack = new LinkedList<>();
    // Thread related
    private boolean idle = true;     
    /**
    public DFS(Graph graph,Integer start, ParallelSearch l)
    {
        //super.start();
     
        g = graph;
        parent = l;
        s = new ArrayList<>();
        s.add(start);
        stack.add(s); 
        idle = false;

        
        maxThreads = Runtime.getRuntime().availableProcessors();
        for(int i=0; i < maxThreads-1;i++)
        {
            DFS dfs = new DFS(g);
            workers.add(dfs);
        }   
        
        startTime = System.currentTimeMillis();
    }
    
    
    public DFS(Graph g)
    {
        //super.start();
        this.g = g;  
    }
    **/
    public DFSV3()
    {
        idle = true;
    }
    public void addVals()
    {
        s.clear();
        s.add(g.getStart());
        stack.add(s); 
        setIdle(false);
    }
    
    public static void begin(Graph graph, ConcurrentLinkedDeque<DFSV3> w, int threads, Object l)
    {
        workers = w;
        g = graph;
        maxThreads = threads;
        lock = l;
        
        DFSV3 starter = workers.pollFirst();
        starter.addVals();        
        startTime = System.currentTimeMillis();
    }
    public void run() 
    {
        while(true)
        {
            try
            {
                if(!idle)
                    work();  
                sleep(1);
            }
            catch(Exception e)
            {
               e.printStackTrace();
            }
        }

    }
    
    public void cleanUp()
    {
        
        stack.clear();
        currentInt = 0;
        s.clear();
    }
    
    private static synchronized void doCheck()
    {
        if(workers.size() == maxThreads)
        {       
            synchronized(lock)
            {
                reset();
                lock.notify();
            }
        }
    }
    
    private synchronized static void threadLog(String log)
    {
        System.out.println("t"+Thread.currentThread().getId()+": "+log);
        
    }
    
    private void work() throws Exception
    { 
            while (!stack.isEmpty()) 
            {
                //threadLog("Stack: " + stack.toString());
                s = stack.pollLast();
                //threadLog("Current List: " + s.toString());
                currentInt = s.get(s.size() - 1);
                if (currentInt.equals(g.getGoal())) 
                    printGoal();              
                
                edges = new ArrayList<>(g.outEdges(currentInt));
                //threadLog("Edges: " + edges.toString());
                for(Integer edge : edges)
                {
                    ArrayList<Integer> temp = new ArrayList<>(s);
                    temp.add(edge);
                    stack.add(new ArrayList<>(temp)); 
                }  
                if(hasIdle() && stack.size() > 1)
                    sendWork(stack);
            }
            // no more work? set to idle
            setIdle(true);
            cleanUp();
            workers.add(this);
            doCheck();

    }         
    
    public static synchronized void sendWork(LinkedList<ArrayList<Integer>> stack)
    {
        DFSV3 dfsWorker = workers.pollLast();
        if(dfsWorker != null)
        {
            dfsWorker.steal(stack.pollFirst());
            dfsWorker.setIdle(false);
        }
    }
    
    private void printGoal() throws Exception
    {
        time = System.currentTimeMillis() - startTime;
        ParallelSearchV3.setResult(time);
        //isFound = true;
        //threadLog("==== FOUND GOAL ====");
        //threadLog("path: " + s);
        //threadLog("====================");                   

        /**
        synchronized(parent)
         {
            parent.setResult(time);
            reset();
            parent.notify();     
        }
        synchronized(lock)
        {
            ParallelSearch.setResult(time);
            reset();
            lock.notify();
        } **/
    }
    
    public void steal(ArrayList<Integer> addition)
    {
        stack.add(addition);
    }
    
    public  void setStart(Integer s)
    {
        this.s = new ArrayList<>(s);
    }

    public void setIdle(boolean status)
    {
        idle = status;
    }
    
    private static boolean hasIdle()
    {
        return !workers.isEmpty();
    }
    
    public synchronized long getThreadId()
    {
        return this.getId();
    }
    
    private static void reset()
    {
        //isFound = false;
        startTime = 0;
        time = 0;
    }
}
