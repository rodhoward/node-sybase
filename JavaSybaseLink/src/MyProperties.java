
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author rod
 */
public class MyProperties {
	
	public Properties properties;

	public MyProperties(String propertyFileName) {
		properties = new Properties();
		try {
			InputStream in =  new FileInputStream(propertyFileName);
			properties.load(in);
			in.close();
		}
		catch(Exception e) {
			//System.err.println(e);
		}
	}

}
