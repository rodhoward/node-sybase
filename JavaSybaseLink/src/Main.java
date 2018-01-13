
/*
 * The idea is to recive json messages in containing
 * { "msgId" : 1, "sql" : "select * from blar"}   on standard in.
 *
 * Then on standard out send
 * { "msgId" : 1, "rows" : [{},{}]}  back on standard out where the msgId matches the sent message.
 */

public class Main implements SQLRequestListener {

	String host;
	Integer port;
	String dbname;
	String username;
	String password;
	SybaseDB db;
	StdInputReader input;
        boolean allownull;

    public static void main(String[] args) {

        Main m;
        String pw = "";
	 boolean allowNull = false;
        if (args.length != 6 && args.length != 5 && args.length != 4)
        {
            System.err.println("Expecting the arguments: host, port, dbname, username, password, allownull");
            System.exit(1);
        }
        if (args.length > 4) {
            pw = args[4];
          }
        if (args.length > 5) {
          allowNull = Boolean.parseBoolean(args[5]);
        }
        m = new Main(args[0], Integer.valueOf(Integer.parseInt(args[1])), args[2], args[3], pw, allowNull);
    }

    public Main(String host, Integer port, String dbname, String username, String password, boolean allowNull) {
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.username = username;
        this.password = password;
        this.allownull = allowNull;

        input = new StdInputReader(allowNull);
        input.addListener(this);

        MyProperties props = new MyProperties("sybaseConfig.properties");
        db = new SybaseDB(host, port, dbname, username, password, props.properties);
        if (!db.connect())
                System.exit(1);

        // send the connected message.
        System.out.println("connected");

        // blocking call don't do anything under here.
        input.startReadLoop();
    }

	public void sqlRequest(SQLRequest request)
	{
		db.execSQL(request);
		//System.out.println(result);
	}


}