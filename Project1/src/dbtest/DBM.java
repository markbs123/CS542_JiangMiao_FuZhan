package dbtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Set;
/** 
 * @author Jiang Miao && Fu Zhan
 * @date Sep 25, 2015 10:54:58 AM 
 *   
 */
public class DBM {
	private static final int DATA_LENGTH = 4 * 1024 * 1024;
	private static final int METADATA_LENGTH = 1024 * 1024;
	private static final int VALUE_LENGTH = 1024 * 1024;
	private static File file;
	private static Map<String, byte[]> map = new HashMap<String, byte[]>(); // Store the key and value
	private ReadWriteLock rwlock = new ReentrantReadWriteLock(); 
	public DBM(File file) {
		this.file = file;
	}

	public DBM() {
	}
/**
 * I need to validate the size of data before saving the data, so I use sum[0] to save all the key length
 * in metadata, and sum[1] to save all the value length. Then use them to compare with the default metadata
 * length and datalength. If they meet the size of requirements, then we can finish the put() process.
 * @param Key  
 * @param data
 * @return 
*/
	public synchronized void put(String Key, byte[] data) throws IOException {  
        rwlock.writeLock().lock();  
        try{
		int[] sum = readFile();
		sum[0] += Key.length(); // Calculate the metadata size in the file 
		if(map.get(Key)==null){ // To judge whether the key is already in the hashmap 
			sum[1] += data.length; // If the key is not in hashmap, the data length of whole file need to 
			                       // plus the new key data length.
		}else{
			sum[1] = sum[1]-map.get(Key).length+data.length;
	// If the key is in the hashmap, I need to remove the original data length of this key at first, 
	// then plus the new data length of this key to get data length of whole file
		}
		if (data.length > VALUE_LENGTH) { // The max size of value length is 1MB
			System.out.println("Your value length is larger than 1MB");
			return;
		}
		else if (sum[0] > METADATA_LENGTH || sum[1] > DATA_LENGTH) {
			// The key length and value length of whole file cannot larger than the default value
			System.out.println("the data size/meta data size is too large");
			System.out.println("Fail to put the Key " + Key);
			return;
		}else{
			map.put(Key, data); // Put the key into the hashmap
			writeFile();
			System.out.println(
					"putting the Key " + Key + " with new size " + (data.length) / (1024.0 * 1024) + "MB is successful");
		     }
        }
        finally{
        rwlock.writeLock().unlock(); }
		
	}
/**
 * If I want to get the each exact size of value in the file, at first I need to get their key through 
 * traversing the hashmap. In the other word, find the key means to find its value.
 * @param Key
 * @return
 */

	public synchronized String get(String Key) throws IOException {
		try{
		rwlock.readLock().lock();  
		readFile();
		if(map.get(Key)==null){ //Key is not in the hashmap

			System.out.println("Key " + Key +" is not found");
			return null;			
		}
		else{
		String value = new String(map.get(Key), "utf-8");// Get the key
		System.out.println("the Key " + Key + " value size is " + (map.get(Key).length) / (1024.0 * 1024) + "MB");
		
		return value; 
		 }	
		}
		finally {
			rwlock.readLock().unlock();  
		}
		
	}
/**
 * Same principle of the get(), do operations for key.
 * @param Key
 * @throws IOException It will appear in the test process
 */

	public synchronized void remove(String Key) throws IOException {
	     rwlock.writeLock().lock();  
	        try{
		readFile();
		if(map.get(Key)==null){
			System.out.println("Key " + Key +" is not found");
			return;
		}
		map.remove(Key); // Delete the key 
		if (writeFile()) {
			System.out.println("remove the Key " + Key + " is successful");
		}
	          }
	        finally{
	        	 rwlock.writeLock().unlock();	        	
	        }
	}
/**
 * Use bufferreader to read the input key and value, then save them into the hashmap
 * @return
 * @throws IOException
 */

	public static int[] readFile() throws IOException {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "utf-8");
		BufferedReader br = new BufferedReader(isr); // Read the data from the file by inputstream
		String line = "";
		String Key = "";
		int[] sum = new int[2];
		while ((line = br.readLine()) != null) {
			Key = line;
			sum[0]+= Key.length(); // Sum of all the key length
			line = br.readLine();
			byte[] temp = line.getBytes(); 
			sum[1]+=temp.length; // Sum of all the value length
			map.put(Key, temp); // Save key,value into the hashmap
			
		}

		br.close();
		return sum;
	}
	
	
/**
 * Traversing each pair of key and value to write them to the  file
 * @return
 * @throws FileNotFoundException
 * @throws UnsupportedEncodingException
 */
	public static boolean writeFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter pw = new PrintWriter(file, "utf-8");
		Set<Entry<String, byte[]>> entrySet = map.entrySet();
		// Put each pair of key and value which they are in one-to-one relationship into a set 
		for (Entry<String, byte[]> entry : entrySet) {
			// Traverse the set to get key and value
			String key = entry.getKey();
			byte[] value = entry.getValue();
			pw.println(key);
			pw.println(new String(value, "utf-8"));
		}
		pw.close();
		return true;
	}

}
