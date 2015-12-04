package dblog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




/**
 * @author Jiang Miao
 * @date Nov 22, 2015 10:28:43 AM
 * @parameter
 * @since
 * @return
 */
public class Relation {

	private String[] attrivalue = null;
	private String dbname = null;
	private String dbpath = "C:\\Mark\\Eclipse workspace\\cs542pro4";
	private FileInputStream filein = null;
	private int attrino = 0;
	private StringBuffer inbuffer = new StringBuffer();
	private List<Connection> attrirecord = new ArrayList<Connection>();

	public Relation(String dbname, String dbpath) throws IOException {
		this.dbname = dbname;
		this.dbpath = dbpath;
	}

/**
 * Located the specific attribute value of target column
 * @param goallocation
 * @param attr
 * @return
 */
	public int location(String goallocation,String[] attr) {
		for (int i = 0; i < attr.length; i++) {
			if (goallocation.equals(attr[i].trim())) {
				return i;
			}
		}
		return -1;
	}
/**
 *  Get the location column number i of the given target
 * @param goallocation
 * @return
 */
	public int location(String goallocation) {
		for (int i = 0; i < attrivalue.length; i++) {
			if (goallocation.equals(attrivalue[i])) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Definition a buffer block to operate, get the contents of db file line by
	 * line, the first line of each file is the key attribute
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {

		try {

			byte[] operation = new byte[1024];
			if (filein == null) {
				filein = new FileInputStream(this.dbpath);
			}
			if (filein.read(operation) != 0) // Through calculating operation
												// number, we can repeat to
												// retrieve the file

			{
				inbuffer.append(new String(operation));
				String its = inbuffer.toString();
				inbuffer.delete(0, inbuffer.lastIndexOf("\n") + 1);
				String[] row = its.split("\n");
				int rowno = row.length + (its.endsWith("\n") ? 0 : -1);
				for (int i = 0; i < rowno; i += 1) {
					if (attrivalue == null) { // First line: key attribute
						attrivalue = row[0].split(",");
						attrino = attrivalue.length;
						for (int j = 0; j < attrino; j += 1) {
							attrivalue[j] = attrivalue[j].trim();
						}
					} else {
					attrirecord.add(new Connection(attrivalue, row[i].split(",")));

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the next useful attribute record in the db file
	 * 
	 * @return
	 * @throws IOException
	 */
	public Connection getNext() throws IOException {

		open();
		if (attrirecord.size() == 0) {
			return null;
		} else { // get a new connection from the buffer, definition a new
					// buffer block to get next record
			Connection conn = attrirecord.get(0);
			attrirecord.remove(0);
			return conn;
		}
	}

	/**
	 * Shut down all the connection from the buffer which meant the original
	 * record will not be changed any more
	 * 
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
	/**
	 * Reading the current file, then according to the requirements to update all the data in the required column
	 * @param column the operation target
	 * @param coefficent  the operation number   
	 * @param readFile  the original db file
	 * @param upFile  the current db file
	 * @param logFile  create the log file 
	 * @throws IOException
	 */

    public void update(String column, int coefficent ,String readFile, String upFile,String logFile) throws IOException {


        PrintWriter pw = new PrintWriter(upFile);
        PrintWriter pwlog = new PrintWriter(logFile);
        		
		FileInputStream fis = new FileInputStream(readFile);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		
		int ind = 1;        
        String line= br.readLine();
        pw.println(line);
        pwlog.println("start");       
        String[] attri1= line.split(",");  // split the first line to get the attribute name of the target column 
        
//        for(int i=0;i<attri1.length;i++){
//        	System.out.println(attri1[i]);
//        }
//        
        int j = location(column, attri1); // get the specific location of this attribute in db file
        
      //  line = br.readLine();
		while ((line = br.readLine())!=null) { // operation the data from the second line
        	System.out.println(line);
                String[] atts = line.split(",");
                System.out.println(atts[j]);
                
                // update the records
                int origin = Integer.parseInt(atts[j]);
                int current = (int)(origin*(1+coefficent/100.0));
                atts[j] = String.valueOf(current);

                // write the new data
                
                String str1 = "";
                for(int i=0;i<atts.length-1;i++){
                	str1 = str1 + atts[i]+",";
                }
                str1 = str1 + atts[atts.length-1];
                
                String str2 = column + ","+ ind + "," + origin + "," +current; // log file format
                
                ind++;
                pw.println(str1);
                pwlog.println(str2);
        }
       
        pwlog.println("commit"); // finish writing log 
        pw.flush(); // release all the allocated resources
        pwlog.flush();
        br.close();
        pw.close();
        pwlog.close();
    }

/**
 * Redo the current db file with the existing log file, make sure the data value is corresponding to the new
 * value in log file
 * @param readFile   the current db file
 * @param logFile    the log file
 * @param redoFile   the new generated db file
 * @throws IOException
 */
    public void redo(String readFile,String logFile,String redoFile) throws IOException {
    	
    	String column;  // make sure which column's attribute need to redo    	
        PrintWriter pwredo = new PrintWriter(redoFile);
        
    	FileInputStream fis1 = new FileInputStream(readFile);
		InputStreamReader isr1 = new InputStreamReader(fis1);
		BufferedReader br1 = new BufferedReader(isr1);
		
		FileInputStream fis2 = new FileInputStream(logFile);
		InputStreamReader isr2 = new InputStreamReader(fis2);
		BufferedReader br2 = new BufferedReader(isr2);
        
	    String line= br1.readLine();
        pwredo.println(line);
        
	    br2.readLine();

        
        String temp = br2.readLine();
        String[] tempdata = temp.split(",");
        column = tempdata[0]; // the location of target column's attribute in log file 

        String[] attri1= line.split(",");               
        int j = location(column, attri1); //  get the specific location of this attribute in db file
       // br1.readLine();       
        while ((line = br1.readLine())!=null) {

                String[] atts = line.split(",");
                
                if("commit".equals(temp)){ // "commit" represents log file has already been finished
                	break;
                }else{	
                	tempdata = temp.split(",");
                    atts[j]=tempdata[3]; // modify the data
                 
                    String str1 = "";
                    for(int i=0;i<atts.length-1;i++){
                    	str1 = str1 + atts[i]+",";
                    }
                    str1 = str1 + atts[atts.length-1];
                    pwredo.println(str1);
                    temp = br2.readLine();
                }         
        }
        pwredo.flush();
        br1.close();
        br2.close();
        pwredo.close();
        
    }
        
        

        

     
      

}
