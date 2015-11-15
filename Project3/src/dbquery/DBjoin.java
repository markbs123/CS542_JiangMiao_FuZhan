package dbquery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/** 
 * @author JiangMiao  FuZhan 
 * @date Nov 4, 2015 10:57:33 AM 
 *
 * Definition Join method
 */
public class DBjoin {
    private Relation ltable = null; 
    private Relation rtable = null;

    private List<Comparator> goalvalue = null;

    private Connection lconn= null;
    private Connection rconn = null;

    public DBjoin(Relation lt, Relation rt) throws IOException {
    	
        goalvalue = new ArrayList<Comparator>(); // target operation data has already processed by Comparator
        
        this.ltable = lt;
        ltable.open();
        lconn = ltable.getNext();
        
        this.rtable = rt;
        rtable.open();
        rconn = rtable.getNext();
    }


/**
 * The join condition method, which can act on more than one relationship
 * @param lt left table 
 * @param rt right table
 * @param result  Comparator result
 * @param multiple  
 */
    public void Condition(String lt, String rt, String result, double multiple) {
        if (ltable!=null && rtable!=null && ltable.location(lt)!=-1 && rtable.location(rt)!=-1) {
        	Comparator com = new Comparator(ltable.location(lt), rtable.location(rt), result, multiple);
        	goalvalue.add(com);
        }
    }

/**
 * Use the left relation as the standard, find all the required value in the right relation which are satisfied
 * with the join conditions   
 * @return
 * @throws IOException
 */
    public Connection Implement() throws IOException {
        if (lconn==null) {
            return null;
        }

        Connection result = null;

        while (true) {
            if (rconn==null) {
                lconn = ltable.getNext();
                if (lconn==null) {
                    break;
                }
                rtable.close();
                rtable.open();
                rconn = rtable.getNext();
            }
            boolean equal= true;
            for (int i=0; i<goalvalue.size(); i+=1) {
                Comparator com = goalvalue.get(i);
             if (!com.Compare(lconn, rconn)) {
                	equal = false;
                    break;
                }
            }
            if (equal==true) {
                result = new Connection(rconn);
                result.Union(lconn); // right connection union with the left connection
                rconn = rtable.getNext();
                break;
            } 
            else {
                rconn = rtable.getNext();
            }
        }

        return result;
    }
    



}
