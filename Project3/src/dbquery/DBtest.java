package dbquery;

/** 
 * @author Jiang Miao 
 * @date Nov 9, 2015 10:47:02 PM 
 * @parameter  
 * @since  
 * @return  
 */
public class DBtest {
    public static void main(String args[]) {
    	
        Relation country = new Relation("country", "country.db");
        Relation city = new Relation("city", "city.db");
        try {
     		System.out.println("Query Test start:");
    		System.out.println("------------------------------------------");
            country.open();
            city.open();
            DBjoin dbjoin = new DBjoin(city,country);
            dbjoin.Condition("CountryCode","Code", "EQ", 1.0); // city.Coutrycode= coutry.code
            dbjoin.Condition("Population", "Population", "GT", 0.4);// city.population> 40%*country.population
            while (true) {
                Connection test = dbjoin.Implement();
                if (test==null) {
                	System.out.println("------------------------------------------");
                	System.out.println("Query Finished");
                    break;
                }
                test.Select(new String[]{"Name"});
   
                System.out.println(test);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
