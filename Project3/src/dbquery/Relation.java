package dbquery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;




/** 
 * @author JiangMiao  FuZhan
 * @date Nov 9, 2015 6:23:40 PM 

 */
public class Relation {
	   
	    private static final int Buffersize = 256;
	    private String[] attrivalue = null;
	    private String dbname = null;
	    private String dbpath ="C:\\Mark\\Eclipse workspace\\cs542pro3";
	    private FileInputStream filein = null;	    
	    private int attrino = 0;
	    private StringBuffer inbuffer = new StringBuffer();
	    private List<Connection> attrirecord = new ArrayList<Connection>();
	    

	    public Relation(String dbname, String dbpath) {
	        this.dbname = dbname;
	        this.dbpath = dbpath;
	    }
/**
 * 	Get the location column number i of the given target   
 * @param goallocation
 * @return
 */
	    public int location(String goallocation) {
	        for (int i=0; i<attrivalue.length; i+=1) {
	            if (goallocation.equals(attrivalue[i])) {
	                return i;
	            }
	        }
	        return -1;
	    }

/**
 * Definition a buffer block to operate, get the contents of db file line by line, the first line of each 
 * file is the key attribute      
 * @throws IOException
 */
	    public void open() throws IOException {
	    	
	    try{  
	    	
	       byte[] operation = new byte[Buffersize];
	       if (filein==null) {
	           filein = new FileInputStream(this.dbpath);
	         }	       	        
           if(filein.read(operation)!=0) // Through calculating operation number, we can repeat to retrieve the file
        	                             
            {       
	            inbuffer.append(new String(operation));
	            String its = inbuffer.toString();
	            inbuffer.delete(0, inbuffer.lastIndexOf("\n")+1);
	            String[] row = its.split("\n");
	            int rowno = row.length + (its.endsWith("\n")?0:-1);
				for (int i=0; i<rowno; i+=1) {
                    if (attrivalue==null) {  // First line: key attribute
	                    attrivalue = row[0].split(",");
	                    attrino = attrivalue.length;                  
	                    for (int j=0; j<attrino; j+=1) {
	                        attrivalue[j] = attrivalue[j].trim();
	                    }
	                } 
	                else {
		                 attrirecord.add(new Connection(attrivalue, row[i].split(",")));
           
	                }
	             }
               } 
	        }catch(IOException e){
	        	e.printStackTrace();
	        }
	            
              
    }

/**
 * Get the next useful attribute record in the db file
 * @return
 * @throws IOException
 */
	    public  Connection getNext() throws IOException {
	    	
	    	  open();	    	
	    	  if(attrirecord.size()==0){
	    		 return null;  	          
		        }
	    	  else{ // get a new connection from the buffer, definition a new buffer block to get next record             
	    		Connection conn = attrirecord.get(0);
	 	        attrirecord.remove(0);
	 	        return conn;
	    	   }
		 }
		  
/**
 * Shut down all the connection from the buffer which meant the original record will not be changed any more
 * @throws IOException
 */

	    public void close() throws IOException {
	        try {
	            this.attrirecord.clear();
	            this.filein.close();
	            this.filein = null;
	            this.attrivalue = null;
	            this.inbuffer = new StringBuffer();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}