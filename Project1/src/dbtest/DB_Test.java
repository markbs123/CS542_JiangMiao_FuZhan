package dbtest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
/** 
 * @author Jiang Miao && Fu Zhan
 * @date Sep 25, 2015 10:54:58 AM 
 * Finish the validation of Concurrency, Duration and Fragmentation
 */
public class DB_Test {

	public static final byte[] data0 = new byte[2 * 1024 * 1024];
	public static final byte[] data1 = new byte[1024 * 1024];
	public static final byte[] data2 = new byte[1024 * 512];
	public static final byte[] data3 = null;

/**
 * Fragmentation test
 * @param args
 */
	public static void main(String[] args) {
		try {
		
			System.out.println("Fragmentation Test start:");
			System.out.println("------------------------------------------");
			File file = createFile();
			DBM dbm = new DBM(file);
/*
			System.out.println("test1");
			String s = dbm.get("B"); // get the value of key B

			System.out.println("test2");
			dbm.put("E", data0); // put key E which value is 2MB
*/
			System.out.println("test3");
			dbm.remove("B");
			dbm.put("E", data2); // Put key E which value is 0.5MB
			dbm.put("F", data1); // put key F which value is 1MB

			System.out.println("test4");
			dbm.remove("C");
			dbm.put("G", data1); // Put key G which value is 1MB
			
			System.out.println("test5");
			dbm.remove("E");
			dbm.put("H", data1);
			dbm.get("H"); // Get the value of key H			

		}
		 catch (IOException e) {
				e.printStackTrace();
		 }
		
	}	
/**
 * Create the initial file
 * @return
 * @throws IOException
 */

	public static File createFile() throws IOException {
		File file = new File("cs542.db");
		file.createNewFile();
		PrintWriter pw = new PrintWriter(file, "utf-8");

		String str1 = new String(data1, "utf-8");

		for (char i = 'A'; i <= 'D'; i++) {
			pw.println(i);
			pw.println(str1);
		}
		pw.close();
		return file;
	}
}
