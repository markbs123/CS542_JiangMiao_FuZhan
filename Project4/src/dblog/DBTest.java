package dblog;

/**
 * @author Jiang Miao
 * @date Nov 27, 2015 11:28:04 PM
 * @parameter
 * @since
 * @return
 */
public class DBTest {
	public static void main(String args[]) {
		try {
			Relation rel1 = new Relation("city", "city.db");
			Relation rel2 = new Relation("country", "country.db");
			
			rel1.update("Population", 2,"city.db","upCity.db","city.log");
			rel2.update("Population", 2,"country.db","upCountry.db","country.log");
			rel1.redo("city.db", "city.log", "redocity.db");
			rel2.redo("country.db", "country.log", "redocountry.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

