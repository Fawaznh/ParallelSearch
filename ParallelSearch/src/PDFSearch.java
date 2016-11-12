
public class PDFSearch 
{
    public static void main(String args[])
    {
        PDFSManager manager = new PDFSManager(Runtime.getRuntime().availableProcessors());
        PDFS.setManager(manager);        
        manager.startSearch().start();
    }
}
