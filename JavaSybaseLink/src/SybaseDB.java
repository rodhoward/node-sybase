
import java.sql.Connection;
import java.sql.DriverManager;
import com.sybase.jdbc3.jdbc.SybDriver;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 *
 * @author rod
 */
public class SybaseDB {

	public static final int TYPE_TIME_STAMP = 93;
	public static final int TYPE_DATE = 91;

	public static final int NUMBER_OF_THREADS = 5;

	String host;
	Integer port;
	String dbname;
	String username;
	String password;
	Connection conn;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public SybaseDB(String host, Integer port, String dbname, String username, String password)
	{
		this.host = host;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.password = password;
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public boolean connect()
	{
		try {

			SybDriver sybDriver = (SybDriver) Class.forName("com.sybase.jdbc3.jdbc.SybDriver").newInstance();
			conn = DriverManager.getConnection("jdbc:sybase:Tds:" + host + ":" + port + "/" + dbname, username, password);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("sp_helpdb");
			rs.next();
			stmt.close();
			return true;

		} catch (Exception ex) {
			System.err.println(ex);
			return false;
		}
	}

	public void execSQL(SQLRequest request)
	{
		Future f = executor.submit(new ExecSQLCallable(conn, df, request));
		// prints to system.out its self.
	}

}
