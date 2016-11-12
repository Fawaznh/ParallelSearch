
import java.util.Random;


public class BalancedTree 
{
    
    private Graph g = new Graph();
    //private Random generator;
    int count;
    int maxDepth;
    int branchFactor;
    
    public BalancedTree(int depth, long seed, int branch)
    {
        maxDepth = depth;
        //generator = new Random(seed);
        branchFactor = branch;
        generate(0);  
        System.out.println(count);
    }
   
    private void generate(int depth)
    {
        //System.out.println(" Current Depth = " + depth);
        if(depth == maxDepth)
            return;
        depth++;
        int current = count;
        //This line will randomize the tree.
        //int out = generator.nextInt(branchFactor + 1);

        //System.out.println("Number of out edges from " + current + " = " + out);
        //You will need to change branchFactor to out if you want a randomized tree.
        for(int i = 0; i < branchFactor; i++)
        {
            count++;
            //Only relavent if tree is random
            //System.out.println("Adding edge from " + current + " to " + count);
            g.addEdge(current, count);
            generate(depth);
        }
    }
    
    public Graph getGraph()
    {
        return g;
    }
    
    public static void main(String[] args)
    {
        new BalancedTree(9, 87, 5);
    }
}
