package dbtest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/** 
 * @author Jiang Miao 
 * @date Sep 29, 2015 8:42:10 PM 
 */
public class Thread_Test {
	public static final byte[] data1 = new byte[1024 * 1024];
	public static void main(String[] args) {
		try {
	
			System.out.println("Concurrency Test start:");
			System.out.println("------------------------------------------");
			File file = createFile();
			DBM dbm = new DBM(file);
/**
* Design two threads to validate when one user do get(), other user can finish put() and replace the data
*/				
			Thread t0 = new Thread() {
				public void run() {
						try {
							Thread.sleep(5000);
							dbm.get("D");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				
			Thread t1 = new Thread() {
		
			public void run() {
					try {
						Thread.sleep(5000); // The time needed to simulate other processing, set 5s
						dbm.remove("D");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			
			t0.start();
			t1.start(); 

		} 
		catch (IOException e) {
			e.printStackTrace();
			}
	
    }
/**
 * Create the file to compare with the primary file
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
