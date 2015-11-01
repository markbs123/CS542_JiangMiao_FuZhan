package indexexperiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/** 
 * @author Jiang Miao 
 * @date Oct 28, 2015 10:30:17 AM 
 * @parameter  
 * @since  
 * @return  
 */
public class Indextest {

	public static void main(String[] args)  {
		try{
		System.out.println("Index Test start:");
		System.out.println("------------------------------------------");
        Createindex.readFile();
        Createindex.writeFile();
        System.out.println("\n");
        
        
        System.out.println("Test one:");        
        String test1a = Createindex.get("1977|DVD");
        Createindex.returnFile(test1a);
		System.out.println("------------------------------------------");		
        String test1b = Createindex.get("1990|VHS");
        Createindex.returnFile(test1b);		
		System.out.println("------------------------------------------");
        String test1c = Createindex.get("2001|DVD");
        Createindex.returnFile(test1c);
        System.out.println("\n");
        
        System.out.println("Test two:");
        String test2a = Createindex.get("2000");
        Createindex.returnFile(test2a);
		System.out.println("------------------------------------------");		
        String test2b = Createindex.get("2005");
        Createindex.returnFile(test2b);		
		System.out.println("------------------------------------------");
        String test2c = Createindex.get("2010");
        Createindex.returnFile(test2c);
   
		}catch(Exception e){
			e.printStackTrace();
		}
	}



}
