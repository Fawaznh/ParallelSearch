
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DFSDriver 
{
    private FileWriter f;
    private Graph g;
    
    public DFSDriver()
    {
        try
        {
            process();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void process() throws Exception
    {
        //Change the outpuf file name when you change the goal node in the BalancedTree class
        File out = new File("SingleThreadMid.csv");
        File out2 = new File("MultiThreadMid.csv");
        //You might need to adjust these values for your setup
        for(int i = 1; i < 9; i++)
            for(int j = 1; j < 8; j++)
            {
                //Add your node check here
                System.out.println("Branch Factor = " + i);
                System.out.println("Depth = " + j);
                g = new BalancedTree(j, 0, i).getGraph();
                //Alternate between the single thread and multithread version
                //or performance suffers.
                //doMultiThread(out2);
                //f.close();
                doSingleThread(out);
                f.close();
            }
        System.out.println("Driver Complete");
        System.exit(0);
    }
    
    private void doMultiThread(File out) throws Exception
    {
            if(out.exists())
                f = new FileWriter(out, true);
            else
                f = new FileWriter(out);
            for(int i = 0; i < 50; i++)
            {
                System.out.println(i);
                if(i != 0)
                    f.write(", ");
                long time = ParallelSearchV2.begin(g);
                //System.out.println("Time taken: " + time);
                f.write(String.valueOf(time));
            }        
            f.write("\n");
    }
    
    private void doSingleThread(File out) throws Exception
    {
        if(out.exists())
            f = new FileWriter(out, true);
        else
            f = new FileWriter(out);
        for(int i = 0; i < 50; i++)
        {
            System.out.println(i);
            if(i != 0)
                f.write(", ");  
            long time = new PDFSManager(g).process();
            f.write(String.valueOf(time));
        }        
        f.write("\n");

    }
    public static void main(String[] args)
    {
        new DFSDriver();
    }
}
