
import java.util.concurrent.ConcurrentLinkedDeque;



public class ParallelSearchV3 
{
    private static Graph g;
    private static long res;
    private static boolean done;
    private static int threadCount; 
    private static ConcurrentLinkedDeque<DFSV3> workers;
    private static final Object LOCK = new Object();
    
    public ParallelSearchV3() throws Exception
    {

    }
    
    public static long begin(Graph graph)
    {
        done = false;
        res = 0;
        g = graph; 
        threadCount = Runtime.getRuntime().availableProcessors();
        //System.out.println("Goal: " + g.getGoal());
        
        if(workers == null)
        {
            createWorkers();
        }
        else
            for(DFSV3 worker : workers)
                worker.cleanUp();
        return process();
    }
    
    public static void createWorkers()
    {
        workers = new ConcurrentLinkedDeque<>();
        for(int i = 0; i < threadCount; i++)
        {
            DFSV3 d = new DFSV3();
            workers.add(d);
            d.start();
        } 
    }
    
    public static void main(String[] args)
    {

        //System.out.println(Runtime.getRuntime().availableProcessors());

        Graph g = new BalancedTree(13, 0, 3).getGraph();            
        long test = ParallelSearchV3.begin(g);
        System.out.println("Goal found in " + test + "ms");
        System.exit(0);
    }
    
    public static boolean isDone()
    {
        return done;
    }
    
    public static long process()
    {
        try
        {
            synchronized(LOCK)
            {
                DFSV3.begin(g, workers, threadCount, LOCK);
                while(workers.size() != threadCount)
                    LOCK.wait();
            }            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    
    public static void setResult(long l)
    {
        res = l;
        done = true;
    }
}
