
import java.util.*;
import java.util.concurrent.*;


public class PDFSManager 
{
    private int maxThreads;
    private final Object threadLock;  
    private final long time;
    private int count;
    private final ConcurrentLinkedDeque<ArrayList<Integer>> work = new ConcurrentLinkedDeque<>();
    
    public PDFSManager(int threadCount)
    {
        Graph g = new BalancedTree(12, 87, 3).getGraph();
        maxThreads = threadCount;
        g.setStart(0);
        g.setGoal(531441);
        PDFS.setGraph(g);
        threadLock = PDFS.getLock(); 
        time = System.currentTimeMillis();
        System.out.println(time);
        ArrayList<Integer> a = new ArrayList<>();
        a.add(g.getStart());
        work.add(a);
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

        return work.pollLast();
    }
    
    public synchronized void updateStatus()
    {
        count++;
        if(count == 4)
        {
            long done = System.currentTimeMillis();          
            System.out.println("No path found.  Exiting");
            System.out.println(done - time);
            System.exit(0);
        }
    }
    
    public synchronized void addWork(ArrayList<Integer> list, Integer i)
    {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.addAll(list);
        temp.add(i);
        work.add(temp);
        wakeUp();
    }
    
    public void wakeUp()
    {
        if(maxThreads > 0)
        {
                maxThreads--;
                new PDFS().start(); 
        }
        else
        {
            if(count > 0)
                count--;
            synchronized(threadLock)
            {
                threadLock.notify();
            }
        }
    }
}
