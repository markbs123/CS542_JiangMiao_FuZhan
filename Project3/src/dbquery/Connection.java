package dbquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/** 
 * @author JiangMiao  FuZhan
 * @date Nov 9, 2015 7:18:41 PM 
 *
 *Define the connection of each relation, return the record of each relation
 */
public class Connection {

    private String[] key = null;
    private String[] value = null;
    
    public Connection(String[] key, String[] value) {
        this.key = new String[key.length];
        this.value = new String[key.length];
        for (int i=0; i<key.length; i+=1) {

            this.key[i] = key[i].trim();
            this.value[i] = value[i].trim();
        }
    }
    
/**
 * Distribution of the opdata which contains key and value into two array lists as the operation target  
 * @param opdata operation data
 */
    public Connection (Connection opdata) {
        this.key = Arrays.copyOf(opdata.getallkeys(), opdata.getallkeys().length);
        this.value = Arrays.copyOf(opdata.getallvalues(), opdata.getallvalues().length);
    }
/**
 *  Define the Select method to get required data from the relation, remove other useless data  
 * @param names required attributes
 */
    public void Select(String[] names) {
        HashSet<String> set = new HashSet<String>(Arrays.asList(names));
        List<String> selectkey = new ArrayList<String>();
        List<String> selectvalue = new ArrayList<String>();
        for (int i=0; i<key.length; i+=1) {
            if (set.contains(key[i])) {
            	selectkey.add(key[i]);
            	selectvalue.add(value[i]);
            }
        }
        key = selectkey.toArray(new String[]{});
        value = selectvalue.toArray(new String[]{});
        
    }
    
/**
 * Union all the required relations, keylist and valuelist are independent
 * @param opdata operation data
 */
    public void Union(Connection opdata) {


        String[] aimkey = opdata.getallkeys();
        String[] aimvalue = opdata.getallvalues();
        List<String> keylist = new ArrayList<String>(Arrays.asList(key));
        List<String> valuelist = new ArrayList<String>(Arrays.asList(value));

        for (int i=0; i<aimkey.length; i+=1) {
            keylist.add(aimkey[i]);
            valuelist.add(aimvalue[i]);
        }
        String[] tmp = new String[1];
        key = keylist.toArray(tmp);
        value = valuelist.toArray(tmp);
    }
    
 /**
  * Get the specific key form the relation by using its index number       
  * @param index
  * @return
  */
    public String getonekey(int index){
    	
        if (key==null || key.length<index) {
            return null;
        }
        return key[index];    	   	
    }
/**
 * Get the specific value form the relation by using its index number     
 * @param index
 * @return
 */
    public String getonevalue(int index){
    	 if (value==null || value.length<index) {
             return null;
         }
         return value[index];
    
    }
    
   String[] getallkeys() {
		return this.key;

	}
		
	public  String[] getallvalues() {
		return this.value;

	}
	
/**
 * Translate all the connection to the String value, ensure the final results are in data form
 */

    public String toString() {
    	
        StringBuilder str = new StringBuilder();

        
        if (value==null) {
            return null;
        }
        for (int i=0; i<value.length; i+=1) {
        	str.append(value[i]);
        	str.append(" ");
        }
        str.append("\n");
        return str.toString();
    }
}
