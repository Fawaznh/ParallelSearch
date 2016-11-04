
import java.util.*;
import java.util.concurrent.*;


public class PDFSManager 
{
    private int maxThreads;
    private Object threadLock = new Object();  
    private long time;
    private final ConcurrentLinkedDeque<ArrayList<Integer>> work = new ConcurrentLinkedDeque<>();
    
    public PDFSManager(int threadCount)
    {
        Graph g = new Graph();
        maxThreads = threadCount;
        g.setStart(0);
        g.setGoal(1500);
        generateTree(g);
        PDFS.setGraph(g);
        
        threadLock = PDFS.getLock(); 
        time = System.currentTimeMillis();
        System.out.println(time);
        ArrayList<Integer> a = new ArrayList<>();
        a.add(g.getStart());
        work.add(a);
    }
    
    public static void generateTree(Graph g)
    {
       int i = 0;
       int max = 3000;
       
       while(i < max)
       {
           int parent = i;
           int left= (i*2)+1;
           int right= (i*2)+2;
           //System.out.println(parent + " " + left + " "  + right);
           
           i++;        
           
           g.addEdge(parent, left);
           g.addEdge(parent, right);
           
           //System.out.println("node: "+parent+"  "+left+"  "+right);
       }      
    }   
    
    public PDFS startSearch()
    {
        return new PDFS();
    }
    
    public long getTime()
    {
        return time;
    }
    public synchronized ArrayList<Integer> requestWork()
    {
        return work.poll();
    }
    
    public synchronized ArrayList<Integer> hasWork()
    {
        return work.peek();
    }
    
    public synchronized void addWork(ArrayList<Integer> list, Integer i)
    {
        ArrayList<Integer> temp = new ArrayList<>(list);
        temp.add(i);
        work.add(temp);
        wakeUp();
    }
    
    public synchronized void wakeUp()
    {
        if(maxThreads > 0)
        {
                maxThreads--;
                new PDFS(); 
        }
        else
            synchronized(threadLock)
            {
                threadLock.notify();
            }
    }
}
