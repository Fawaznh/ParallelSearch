
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RRPDFS extends Thread
{
    private static Graph g;
    private static ConcurrentLinkedDeque<LinkedList<Integer>>[] lists;
    private static RRPDFS[] threads;
    private static long start;
    private static int rr;
    private static int ctr;
    private static int max;
    private final int  ID;
    private boolean idle;
    private LinkedList<Integer> currentList;
    private LinkedList<Integer> edges;
    private Integer currentInt;
    
    public RRPDFS(int num)
    {
        ID = num;
    }
    
    public void run()
    {
       while(true)
       {
           while(!lists[ID].isEmpty())
           {
               if(idle)
               {
                   idle = false;
                   dec();
               }
               currentList = lists[ID].pollLast();
               if(currentList != null)
                   process();              
           }
           if(!idle)
           {
                inc();
                idle = true;
           }
           requestWork(ID, new LinkedList<>());
       }
    }
    
    private void process()
    {
        currentInt = currentList.getLast();
        if(currentInt.equals(g.getGoal()))
        {
            System.out.println("t" + ID + " ==== FOUND GOAL ====");                        
            System.out.println("t" + ID + " path: " + currentList);
            System.out.println("t" + ID + " ====================");
            long time = System.currentTimeMillis() - start;
            System.out.println(time);
            System.exit(0);
        }
               
        edges = new LinkedList<>(g.outEdges(currentInt));
        ArrayList<LinkedList<Integer>> temp = new ArrayList<>();
        for(Integer edge : edges)
        {
            currentList.addLast(edge);
            temp.add(new LinkedList<>(currentList)); 
            currentList.removeLast();
        }
        lists[ID].addAll(temp);
    }
    
    private static synchronized void inc()
    {
        if(ctr < max)
            ctr++;
        if(ctr == max)
        {
            long time  = System.currentTimeMillis() - start;
            System.out.println("No path found.  Exiting");
            System.out.println("Time Taken: " + time);
            System.exit(0);
        }      
    }
    
    private static synchronized void dec()
    {
        if(ctr > 0)
            ctr--;
    }
    
    private static synchronized void requestWork(int target, LinkedList temp)
    {
        for(int i = 0; i < lists.length; i++)
            if(i != target)
            {
                temp = lists[i].poll();
                if(temp != null)
                    lists[target].addFirst(temp);
            }
    }
    
    private static void roundRobin()
    {
        rr++;
        rr = rr % max;
    }
    
    public static void begin(int count)
    {
        max = count;
        lists = new ConcurrentLinkedDeque[max];
        threads = new RRPDFS[max];
        for(int i = 0; i < max; i++)
        {
          lists[i] = new ConcurrentLinkedDeque<>();
          threads[i] = new RRPDFS(i);
        }
        lists[0].add(new LinkedList<>());
        lists[0].getFirst().add(g.getStart());
        
        start = System.currentTimeMillis();
        for(RRPDFS t : threads)
            t.start();
    }
    
    public static void main(String[] args)
    {
        /**
        g = new Graph();
        g.generateTree(100000);      

        g.setStart(0);
        g.setGoal(87655); **/
        g = new BalancedTree(11, 0, 4).getGraph();
        begin(Runtime.getRuntime().availableProcessors());
    } 
}
