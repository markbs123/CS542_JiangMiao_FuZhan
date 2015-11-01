package indexexperiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** 
 * @author Jiang Miao 
 * @date Oct 26, 2015 12:03:23 PM  
 */
public class Createindex {
	
	
	private static File file;
	private static Map<String, String> map = new LinkedHashMap<String, String>(); // Store the key and value
	
	
	public Createindex(File file) {
		this.file = file;
	}

	public Createindex() {
	}
/**
 * Read the Moives file by line
 * @param filePath
 * @return
 * @throws IOException
 */
	public static  List<String> readdbFile(String filePath) throws IOException{

		File file = new File(filePath);
		if(file.isFile() && file.exists()){
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			List<String> list = new ArrayList<String>(); 
			while((lineTxt = bufferedReader.readLine()) != null){
				list.add(lineTxt); // list contains the content of each line
			}
			read.close();
			return list;
		}else{
			System.out.println("can not find file");
		}
		return null;
		
	}
/**
 * Split the content by "," in each line, to get the value of index 	
 * @throws IOException
 */
public static void readFile() throws IOException {
		
		//String filePath = "C:\\Mark\\Eclipse workspace\\cs542pro2hw2\\Movies.db";
		String filePath = "C:\\Users\\Mr.zhan\\Desktop\\cs542pro2hw2\\Movies.db";
		List<String> List = null;
		List = readdbFile(filePath);
		String key;
	    String data_value;
		
		for(int i = 0; i<List.size(); i++){
			
			key=String.valueOf(i);  // index key is row number
			String[] temp = List.get(i).split(",");  // get the year and media from
			data_value=temp[1] + "|" + temp[2]; // combine these two attribute as the value of index
			System.out.println(key);
			System.out.println(data_value); 			
			map.put(key,data_value);  // write them to hashmap
		}
	}
	
/**
 * Adds the index entry	
 * @param key
 * @param data_value
 * @throws IOException
 */
	
	public void put(String key, String data_value) throws IOException {   
        try{
        readFile();
		if(map.get(key)==null){ // To judge whether the key is already in the hashmap 
			map.put(key, data_value); // Put the key into the hashmap
			writeFile();
			System.out.println("Insert success!");
		}
		else{
			System.out.println("Record has already existed.");
		    }
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
		
	}

/**
 * Get the specific data from index value
 * @param data_value
 * @return
 * @throws IOException
 */

	public static  String get(String data_value) throws IOException {
//		readFile();
//	    Set set = map.entrySet();
//	    ArrayList arr = new ArrayList();
//	    Iterator it = set.iterator();
//	    while(it.hasNext()) {
//	      Map.Entry entry = (Map.Entry)it.next();
//	      if(entry.getValue().equals(data_value)) {
//	        int s = Integer.parseInt(entry.getKey());
//	        arr.add(s);
//	      }
//	    }
//	    return arr;

		StringBuilder str = new StringBuilder();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Entry<String, String> entry : entrySet) { // Traverse the map
			String key = entry.getKey();
			String value = entry.getValue();
			if(value.indexOf(data_value)!=-1){ // -1 means not find substring
				str.append(key+" "); // Splicing together all the existed keys which correspond to the search value
			}
			
		}
		System.out.println(str.length()==0?"Not Found!":str);
		
		return str.toString();// Create a required keylist 
	}

/**
 * Return to the Moives file to get whole data which correspond to the search value
 * @param keyList
 * @throws Exception
 */
	public static void returnFile(String keyList) throws Exception{
		Boolean[] indexEx = new Boolean[100];
		String[] temp = keyList.split(" "); //Split the keylist in get()method
		try{
		for (int i=0;i<temp.length;i++){
			int key = Integer.parseInt(temp[i]); // Converting the String key to int and save all of them in temp[]
			indexEx[key]=true; 
		}
		}catch (NumberFormatException e){ 
			System.out.println("Wrong input!");
		}
		
		FileInputStream fis = new FileInputStream("C:\\Users\\Mr.zhan\\Desktop\\cs542pro2hw2\\Movies.db");
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String data = "";
		int i = -1; // The first row number is 0, so the loop need to begin from 0
		while((data=br.readLine())!=null){
			i++;
			if (indexEx[i]==null) // If we find the corresponding data, the bool type of this line is true
				continue; 
			else{
				System.out.println(data);
			}
		}
		
		br.close();
	}

/**
 * Remove the key in index	
 * @param key
 * @throws IOException
 */
	

	public void remove(String key) throws IOException {
	 try{
		readFile();
		if(map.get(key)==null){
			System.out.println("key " + key +" is not found");
			return;
		}
		map.remove(key); // Delete the key 
	    System.out.println("remove the key " + key + " is successful");
	
	     }catch(Exception e)
	 {
	    	 e.printStackTrace();
	 }
	}
	
/**
 * Write the index into the new file	
 * @return
 * @throws IOException
 */
	
	public static File writeFile() throws IOException {
		File file = new File("cs542.db");
		file.createNewFile();
		PrintWriter pw = new PrintWriter(file, "utf-8");
		Set<Entry<String, String>> entrySet = map.entrySet();
		// Put each pair of key and value which they are in one-to-one relationship into a set 
		for (Entry<String, String> entry : entrySet) {
			// Traverse the index to get key and value
			String key = entry.getKey();
			String data_value = entry.getValue();
			pw.println(key);
			pw.println(data_value);
		}
		pw.close();
		return file;
	}
	
}


