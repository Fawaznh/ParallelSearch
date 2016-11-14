

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
public class DFS extends Thread
{
    private Stack stack = new Stack();
    private Set<Integer> visited = new HashSet<>();
    public static ConcurrentMap<Integer, Integer> path = new ConcurrentHashMap<>();
    private ConcurrentLinkedDeque<DFS> workers = new ConcurrentLinkedDeque<>();

    public int maxThreads;
    

    private Graph g;
    private Integer s; // start node
    private static boolean isFound = false;
    
    // Thread related
    private boolean idle = true;

    
    private static long startTime;
    
    public DFS(Graph g,Integer s)
    {
        super.start();
        
        this.g = g; 
        this.s = s;   

        
        maxThreads = Runtime.getRuntime().availableProcessors();
       // idleCount = maxThreads;
        
        startTime = System.currentTimeMillis();
        this.activate();
        
        for(int i=0; i < maxThreads-1;i++)
        {
            DFS dfs = new DFS(g);
            workers.add(dfs);
        } 
        
        
    }
    
    
    public DFS(Graph g)
    {
        super.start();
        this.g = g;  
    }
    
    

    public void run()
    {
        while(true)
        {
            if(idle == false)
            {
                work();
            }
        }       
    }
    
    private void work()
    {
        
        // only push if its the first thread
            if(stack.isEmpty())   
                stack.push(s);
            
            while (!stack.isEmpty()) 
            {
                
                s = (Integer)stack.pop();

                if (s.equals(g.getGoal())) 
                {
                    System.out.println(startTime);
                    threadLog("==== FOUND GOAL ====");
                    g.setPath(generatePath());
                    threadLog("path: " + g.toString());
                    threadLog("====================");
                    
                    long time = System.currentTimeMillis();
                    System.out.println(time);
                    System.out.println(time - startTime);
                    
                    System.exit(0);

                }
                
                if(!workers.isEmpty() && stack.size()>2  )
                {
                    sendWork();
                }

                if (!visited.contains(s)) 
                {
                    visited.add(s);

                    for (Integer edge : g.outEdges(s)) 
                    {
                        stack.push(edge);

                        // keep track of the path from the parent node
                        if (!path.containsKey(edge)) 
                        {
                            path.put(edge, s);
                        }
                    }
                }
                
                

            }
            
            // no more work? set to idle
            setIdel();
            workers.add(this);

    }
    
    private void printStack()
    {
        String ss ="";
        for(Object edge:stack.toArray())
        {
            ss += edge +",";
        }
        //threadLog("stack: "+ss);
    }
    
    
     
    private void threadLog(String log)
    {
        System.out.println("t"+Thread.currentThread().getId()+": "+log);
        
    }
    public synchronized void terminate()
    {
        threadLog("terminating thread!");
        System.exit(0);
    }

    
    public List<Integer> generatePath()
    {
        Integer tmp1 = path.get(g.getGoal());
        List<Integer> list = new ArrayList<>();
        
        // add it to first of list since its in reverse order
        list.add(0, g.getGoal()); 
        while(tmp1 != null)
        {
            list.add(0, tmp1); 
            tmp1 = path.get(tmp1);
        }
        

       return list;
    }
    
    // split and remove
    private  synchronized Stack splitStack()
    {
        Stack tmpstack = new Stack();
        int n = stack.size()/2;
        for(int i =0;i<n;i++)
        {
            tmpstack.push(stack.pop());
        }
        return tmpstack;
    }
    

    public synchronized void sendWork()
    {
        DFS dfsWorker = workers.pollLast();
        if(dfsWorker.idle)
        {
            Stack halfStack = splitStack();
            if(!halfStack.isEmpty())
            {
                dfsWorker.setStack(halfStack);
                dfsWorker.setStart((Integer)halfStack.peek());
                dfsWorker.activate();  
            }
        }

    }
    public  void setStart(Integer s)
    {
        this.s = s;
    }
    
    public  void setStack(Stack stack)
    {
        this.stack = stack;
    }
    
    public  void setCurrentPath(ConcurrentMap<Integer, Integer> path)
    {
        this.path = path;
    }
    public boolean isFound()
    {
        return isFound;
    }
    public synchronized void activate()
    {
        idle = false;
    }
    public synchronized void setIdel()
    {
        idle = true;
    }
    
    public synchronized long getThreadId()
    {
        return this.getId();
    }


}
