package Bplustreeexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class test1 {
	
	public static  List<String> readTxtFile(String filePath) throws IOException{
		String encoding = "GBK";
		File file = new File(filePath);
		if(file.isFile() && file.exists()){
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			List<String> list = new ArrayList<String>();
			while((lineTxt = bufferedReader.readLine()) != null){
				list.add(lineTxt);
			}
			read.close();
			return list;
		}else{
			System.out.println("can not find file");
		}
		return null;
		
	}


	public static void main(String[] args) throws IOException {
		
		String filePath = "D:\\MyEclipe\\MyWorkSpace\\BPlustTree_CS542\\data.db";
		List<String> List = null;
		List = readTxtFile(filePath);
//		System.out.println(List.get(1));
		String[] alphabetStrings = new String[List.size()];
		String[] alphabet = new String[List.size()];
		
		for(int i = 0; i<List.size(); i++){
			String[] temp = List.get(i).split(","); 
			alphabet[i] =	temp[1] + "|" + temp[2];	
			//System.out.println(alphabet[i]); // 
		}
		
		  for (int j = 0; j<alphabet.length; j++) {
			  alphabetStrings[j] = alphabet[j].substring(0,4) ;
			  alphabetStrings[j] = String.valueOf(j);
		  }
			BPlusTree<String, String> tree = new BPlusTree<String, String>();
			Utils.bulkInsert(tree, alphabetStrings, alphabet);// key alphabetString, value alphabet

			//String test = Utils.outputTree(tree);
			tree.remove(String.valueOf(0));
			tree.remove(String.valueOf(1));
			String test = Utils.outputTree(tree);
			System.out.println(test);
			}

}
