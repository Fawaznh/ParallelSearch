

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
    public static ConcurrentMap<Integer, Integer> path = new ConcurrentHashMap<>(); //shared

    //HashMap<Integer, Integer> path = new HashMap<>();
    
    
    private Graph g;
    private Integer s; // start node
    private boolean isFound = false;
    
    // Thread related
    private boolean idle = true;
    private boolean terminate = false;
    private boolean workRequested = false;
    private boolean workAccepted = false;
    private DFS recipient;

    
    public DFS(Graph g,Integer s)
    {
        this.g = g; 
        this.s = s;  
        super.start();
    }
    
    public DFS(Graph g)
    {
        this.g = g; 
        super.start();
    }
    
    

    public void run()
    {
        while(terminate == false)
        {
            // start in idle state then look for work
            if(idle)
            {
              
                int idleCount = 0;   
                List<DFS> synList = Collections.synchronizedList(new ArrayList<DFS>(ParallelSearch.dfsThreads));
                int listSize = synList.size();
                for(DFS t: synList)
                {
                    if(!t.isIdle())
                    {
                        if(t.requestWork(this) && !workAccepted)
                        {
                            threadLog("requesting work from t"+t.getThreadId());
                            workAccepted = true;
                            break;
                        }      
                    }
                    else
                    {
                        idleCount++;
                    }

                }
                // terminate if all threads are idle( or ran out of work)
                if(idleCount == listSize) 
                {
                    this.terminate();
                }
                        
                
                continue; // skip in idle state
            }
            
     
            
            // only push if its the first thread
            if(stack.isEmpty())   
                stack.push(s);
            
            
            while (!stack.isEmpty() && !isFound) 
            {
                threadLog("DFS");
                printStack();

                

                s = (Integer)stack.pop();

                threadLog("pop() " + s);
                if (s.equals(g.getGoal())) 
                {
                    threadLog("==== FOUND GOAL ====");
                    g.setPath(generatePath());
                    threadLog("path: " + g.toString());
                    threadLog("====================");
                    
                    isFound = true;
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
                
                
                if(workRequested)
                {
                    workRequested = false;

                        threadLog("work assigned to t"+recipient.getId());
                        Stack halfStack = splitStack();
                        
                        if(!halfStack.isEmpty())
                        {
                            recipient.setStack(halfStack);
                            recipient.setStart((Integer)halfStack.peek());
                            recipient.activate();  
                        }
                }
                
                
                try {
                    sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            // no more work? set to idle
            idle = true;
            workAccepted = false;
            
            threadLog("thread set to idle!");
            
        }
    }
    
    private void printStack()
    {
        String ss ="";
        for(Object edge:stack.toArray())
        {
            ss += edge +",";
            
        }
        threadLog("stack: "+ss);
    }
    
    private void threadLog(String log)
    {
        System.out.println("t"+Thread.currentThread().getId()+": "+log);
        
    }
    public void terminate()
    {
        threadLog("terminating thread!");
        terminate = true;
    }
    
    public List<Integer> generatePath()
    {
        Integer tmp1 = path.get(g.getGoal());
        List<Integer> list = new ArrayList<>();
        
        // add it to first of list since its in reverse order
        while(tmp1 != null)
        {
            list.add(0, tmp1); 
            tmp1 = path.get(tmp1);
        }

       return list;
    }
    
    // split and remove
    private  Stack splitStack()
    {
        Stack tmpstack = new Stack();

        
        int n = stack.size() / 2;
        List<Integer> list = new ArrayList<>(stack.subList(n, stack.size()));
        
        tmpstack.addAll(list);
        // remove the half we give to other thread
        stack.removeAll(list); 
        
        return tmpstack;
    }
    public synchronized boolean requestWork(DFS dfs)
    {
        //reject if there is only one node in the stack
        if(stack.size() <=1 || this.idle) return false;
        
        workRequested = true;
        recipient = dfs;
        
        return true;
    }
    public void setStart(Integer s)
    {
        this.s = s;
    }
    
    public void setStack(Stack stack)
    {
        this.stack = stack;
    }
    
    public void setStack2(Stack stack)
    {
        
        //split the stack then add it
        int size = stack.size();
        int n = size / 2;
       
        List<Integer> list = new ArrayList<>(stack.subList(n, size));

        this.stack.addAll(list);
        
       
        setStart((Integer)this.stack.peek());

    }
    
    public void setCurrentPath(ConcurrentMap<Integer, Integer> path)
    {
        this.path = path;
    }
    public boolean isFound()
    {
        return isFound;
    }
    public void activate()
    {
        idle = false;
    }
    
    public long getThreadId()
    {
        return this.getId();
    }
    
    public boolean isIdle()
    {
        return this.idle;
    }
    public DFS getRecipient()
    {
        return recipient;
    }

}
