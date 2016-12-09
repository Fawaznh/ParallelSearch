
import java.util.*;
import java.util.concurrent.*;


public class PDFSManager 
{
    private int maxThreads;
    private final long time;
    private long res;
    private boolean done;
    private final LinkedList<ArrayList<Integer>> work = new LinkedList<>();
    
    public PDFSManager(Graph graph)
    {  
        maxThreads = Runtime.getRuntime().availableProcessors();
        PDFS.setGraph(graph);
        PDFS.setManager(this);
        time = System.currentTimeMillis();
        ArrayList<Integer> a = new ArrayList<>();
        a.add(graph.getStart());
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
    
    public long process()
    {
        try
        {
            synchronized(this)
            {
                startSearch().start();
                while(!done)
                    wait();
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
   
    public synchronized void addWork(ArrayList<Integer> list, Integer i)
    {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.addAll(list);
        temp.add(i);
        work.add(temp);
    }
    
    public void setResult(long l)
    {
        res = l;
        done = true;
    }
}
