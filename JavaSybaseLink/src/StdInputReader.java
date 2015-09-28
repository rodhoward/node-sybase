
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 *
 * @author rod
 */
public class StdInputReader {

	private List<SQLRequestListener> listeners = new ArrayList<SQLRequestListener>();
	private BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(System.in));

	public StdInputReader() {

	}

	public void startReadLoop()
	{
		String nextLine;
		try {
			while ((nextLine = inputBuffer.readLine()) != null) {
				nextLine = nextLine.replaceAll("\\n", "\n");
				sendEvent(nextLine);
			}
		} catch (IOException ex) {
			System.err.println("IO exception: " + ex);
		}
	}

	private void sendEvent(String sqlRequest)
	{
		long startTime = System.currentTimeMillis();
		SQLRequest request;
		try {
			JSONObject val = (JSONObject) JSONValue.parse(sqlRequest);
			request = new SQLRequest();
			request.msgId = (Integer)val.get("msgId");
			request.sql = (String)val.get("sql");
			request.javaStartTime = startTime;
		} catch (Exception e)
		{
			request = null;
		}
		if (request == null || request.sql == null)
		{
			System.err.println("Error parsing json not a valid SQLRequest object. " + sqlRequest);
			return;
		}

		for (SQLRequestListener l : listeners)
			l.sqlRequest(request);
	}

	public boolean addListener(SQLRequestListener l)
	{
		if (listeners.contains(l))
			return false;

		listeners.add(l);
		return true;
	}

	public boolean removeListener(SQLRequestListener l)
	{
		return listeners.remove(l);
	}





}
