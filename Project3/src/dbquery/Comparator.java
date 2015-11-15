package dbquery;


/** 
 * @author JiangMiao   
 * @date Nov 10, 2015 11:02:47 AM 
 *
 *  Use to comparator two different relation
 */
public class Comparator {
	
    private int lkey = -1; // The first row number is 0, so the loop need to begin from 0
    private int rkey = -1; 
    private String result = null;
    private double multiple = 1.0; // The default condition is left=right



    public Comparator(int lkey, int rkey, String result, double multiple) {
        this.lkey = lkey;
        this.rkey = rkey;
        this.result= result;
        this.multiple = multiple;
    }

/**
 * Compare the left connection and right connection to get different situations
 * @param lconn left connection of relation
 * @param rconn right connection of relation
 * @return
 */
    public boolean Compare (Connection lconn, Connection rconn) {
    	   	
        if (lconn==null || rconn==null) {
            return false;
        }

        if (result=="EQ") { // Equality
            if (lconn.getonevalue(lkey).equals(rconn.getonevalue(rkey))) {
                return true;
            }

        } else if (result=="LT") { // Less than 
            double lvalue = Double.valueOf(lconn.getonevalue(lkey));
            double rvalue = Double.valueOf(rconn.getonevalue(rkey));
            if (lvalue<rvalue*multiple) {
                return true;
            }

        } else if (result=="GT") { // Great than
            double lvalue = Double.valueOf(lconn.getonevalue(lkey));
            double rvalue = Double.valueOf(rconn.getonevalue(rkey));
            if (lvalue>rvalue*multiple) {
                return true;
            }
        }

        return false;
    }
    


}
