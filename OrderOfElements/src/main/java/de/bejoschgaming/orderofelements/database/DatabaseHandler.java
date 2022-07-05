package de.bejoschgaming.orderofelements.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.filesystem.FileHandler;

public class DatabaseHandler {

	private static final String DBname = FileHandler.readOutData(FileHandler.file_Settings, "DB_Name");
	private static final String url = FileHandler.readOutData(FileHandler.file_Settings, "DB_Url")+DBname;
	private static final String user = FileHandler.readOutData(FileHandler.file_Settings, "DB_User");
	private static final String pw = FileHandler.readOutData(FileHandler.file_Settings, "DB_Password");

	public static boolean connectedToDB = false;
	private static Connection connection = null;
	private static Timer keepConnectionTimer = null;

	public static final String tabellName_profile = "Profile";
	public static final String tabellName_playerStats = "PlayerStats";
	public static final String tabellName_games = "Games";
	public static final String tabellName_decks = "Decks";
	public static final String tabellName_maps = "Maps";
	public static final String tabellName_patchnotes = "Patchnotes";
	public static final String tabellName_replays = "Replaydata";
	public static final String tabellName_friendList = "FriendList";
	public static final String tabellName_friendRequests = "FriendRequests";

	//QUELLE: https://www.youtube.com/watch?v=B928IDexsGk
	
// CONNECT / DISCONNECT ===============================================================================================================
	public static void connectToDB() {
		
		if(connection == null) {
			
			try {
				connection = DriverManager.getConnection(url, user, pw);
				keepConnectionTimer();
				connectedToDB = true;
				ConsoleHandler.printMessageInConsole("Succesfully connected to DB '"+DBname+"'!", true);
			}catch(SQLException error) {
				connectedToDB = false;
//				error.printStackTrace();
				ConsoleHandler.printMessageInConsole("Connecting to DB failed! "+error.getMessage(), true);
			}
			
		}else {
			ConsoleHandler.printMessageInConsole("Connection to DB was already established as a connect was requested!", true);
		}
		
	}

	public static void disconnectFromDB() {
		
		if(connection != null) {
			
			try {
				connection.close();
				connectedToDB = false;
				ConsoleHandler.printMessageInConsole("Succesfully disconnected from DB '"+DBname+"'!", true);
			}catch (SQLException error) {
				ConsoleHandler.printMessageInConsole("Disconnecting from DB failed with following error:", true);
				error.printStackTrace();
			}
			
		}else {
			ConsoleHandler.printMessageInConsole("No connection to DB established as a disconnect was requested!", true);
		}
		
	}
	
	private static void keepConnectionTimer() {
		//START PING TIMER TO PREVENT BROKEN PIPE TIMEOUT
		keepConnectionTimer = new Timer();
		keepConnectionTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					String query = "SELECT "+"*"+" FROM "+tabellName_profile+" LIMIT 1";
					PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = stmt.executeQuery();
					rs.close();
				} catch (SQLException error) {
					ConsoleHandler.printMessageInConsole("Pinging the DB '"+DBname+"' failed!", true);
					//error.printStackTrace();
				}
			}
		}, 0, 1000*60*5);
		ConsoleHandler.printMessageInConsole("KeepConnectionTimer started for DB '"+DBname+"'!", true);
	}
	
// SELECT / GET ===============================================================================================================
	public static ResultSet selectRawResultSet(String tabelle, String target, String keyName, String key) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			return rs;
		} catch (SQLException error) {
			//error.printStackTrace(); //MANCHMAL ABSICHTILICHE FEHLER ABFRAGEN ZUM TESTEN
			return null;
		}
		
	}
	public static String selectString(String tabelle, String target, String keyName, String key) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			String result = rs.getString(target);
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			//error.printStackTrace(); //MANCHMAL ABSICHTILICHE FEHLER ABFRAGEN ZUM TESTEN
			return null;
		}
		
	}
	public static int selectInt(String tabelle, String target, String keyName, String key) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			int result = rs.getInt(target);
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			//error.printStackTrace();
			return -1;
		}
		
	}
	public static double selectDouble(String tabelle, String target, String keyName, String key) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			double result = rs.getDouble(target);
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			//error.printStackTrace();
			return -1.0;
		}
		
	}
	
	public static List<Integer> getAllWhereEqual_Int(String tabelle, String target, String keyName, String key) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			List<Integer> result = new ArrayList<Integer>();
			
			try {
				do {
					result.add(rs.getInt(target));
				}while(rs.next());
			}catch(SQLException error) {
				//EMPTY RESULT SET
			}
			
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			error.printStackTrace();
			return null;
		}
		
	}
	public static List<String> getAllWhereEqual_Str(String tabelle, String target, String keyName, String key) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			List<String> result = new ArrayList<String>();
			
			try {
				do {
					result.add(rs.getString(target));
				}while(rs.next());
			}catch(SQLException error) {
				//EMPTY RESULT SET
			}
			
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			error.printStackTrace();
			return null;
		}
		
	}
	
	public static List<Integer> getAll_Int(String tabelle, String target) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+"";
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			List<Integer> result = new ArrayList<Integer>();
			
			try {
				do {
					result.add(rs.getInt(target));
				}while(rs.next());
			}catch(SQLException error) {
				//EMPTY RESULT SET
			}
			
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			error.printStackTrace();
			return null;
		}
		
	}
	public static List<String> getAll_Str(String tabelle, String target) { return getAll_Str(tabelle, target, ""); }
	public static List<String> getAll_Str(String tabelle, String target, String suffix) {
		
		try {
			String query = "SELECT "+target+" FROM "+tabelle+" "+suffix;
			PreparedStatement stmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			List<String> result = new ArrayList<String>();
			
			try {
				do {
					result.add(rs.getString(target));
				}while(rs.next());
			}catch(SQLException error) {
				//EMPTY RESULT SET
			}
			
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException error) {
			error.printStackTrace();
			return null;
		}
		
	}
	
	public static String getPatchNotesAsPacketString() {
		
		String fullPatchnotesData = "";
		
		//SELECT Datum FROM `Patchnotes` GROUP BY Datum ORDER BY Datum DESC
		List<String> getAllDates = getAll_Str(tabellName_patchnotes, "Datum", "GROUP BY Datum ORDER BY Datum DESC");
		
		for(String date : getAllDates) {
			
			fullPatchnotesData += date+";";
			
			//SELECT Typ FROM `Patchnotes` WHERE Datum='26.06.2022' ORDER BY Typ ASC
			List<String> getAllTypesOnThisDate = getAll_Str(tabellName_patchnotes, "Typ", "WHERE Datum='"+date+"' ORDER BY Typ ASC");
			
			for(String typ : getAllTypesOnThisDate) {
				
				String text = selectString(tabellName_patchnotes, "Text", "Datum,Typ", date+"','"+typ);
				
				fullPatchnotesData += typ+";"+text+";";
				
			}
			
			fullPatchnotesData = fullPatchnotesData.substring(0, fullPatchnotesData.length()-1); //REMOVES LAST ;
			fullPatchnotesData += "---";
			
		}
		
		if(fullPatchnotesData == "") {
			return null;
		}else {
			fullPatchnotesData = fullPatchnotesData.substring(0, fullPatchnotesData.length()-3); //REMOVES LAST ---
			fullPatchnotesData = fullPatchnotesData.replaceAll("\r\n", "#");
			return fullPatchnotesData;
		}
		
	}
	
	
// CREATE / INSERT ===============================================================================================================
	
	public static boolean insertNewPlayer(String name, String password) {
		
		try {
			//ID VIA AUTOINCREMENT IN 
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europa/Berlin"));
			String date = doubleWriteNumber(cal.get(Calendar.DAY_OF_MONTH))+"_"+doubleWriteNumber(cal.get(Calendar.MONTH)+1)+"_"+doubleWriteNumber(cal.get(Calendar.YEAR));
			//HASH PW:
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(password.getBytes());
			String passwordHash = new String(messageDigest.digest());
			DatabaseHandler.insertData(DatabaseHandler.tabellName_profile, "Name,Datum,Password", name+"','"+date+"','"+passwordHash);
			int id = DatabaseHandler.selectInt(DatabaseHandler.tabellName_profile, "ID", "Name", name);
			DatabaseHandler.insertData(DatabaseHandler.tabellName_playerStats, "ID", ""+id);
			return true;
		} catch (SQLException error) {
//			error.printStackTrace(); //SOMETIMES SHOULD BE THROWN AS CHECK FOR DUPLICATE ENTRY (REGISTER NAME AS EXAMPLE)
			return false;
		} catch (NoSuchAlgorithmException error) {
			error.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean registerNewFriendship(int id1, int id2) {
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europa/Berlin"));
		String date = doubleWriteNumber(cal.get(Calendar.DAY_OF_MONTH))+"_"+doubleWriteNumber(cal.get(Calendar.MONTH)+1)+"_"+doubleWriteNumber(cal.get(Calendar.YEAR));
		
		try {
			DatabaseHandler.insertData(DatabaseHandler.tabellName_friendList, "ID1,ID2,Datum", id1+","+id2+","+date);
			DatabaseHandler.insertData(DatabaseHandler.tabellName_friendList, "ID1,ID2,Datum", id2+","+id1+","+date);
		} catch (SQLException error) {
			return false;
		}
		
		return true;
	}
	public static boolean unregisterFriendship(int id1, int id2) {
		
		DatabaseHandler.deleteData(DatabaseHandler.tabellName_friendList, "ID1,ID2", id1+"','"+id2);
		DatabaseHandler.deleteData(DatabaseHandler.tabellName_friendList, "ID1,ID2", id2+"','"+id1);
		
		return true;
	}
	
	/**
	 * Insert data
	 * @param tabelle - Table name
	 * @param vars - The variables WITHOUT BRACKETS! - Like this: Name,ID,Datum,Password
	 * @param values - The values WITHOUT BRACKETS! - Like this: 'BEJOSCH','1234','01_02_2000','abcba'
	 * @throws SQLException 
	 */
	public static void insertData(String tabelle, String vars, String values) throws SQLException {
		
		String query = "INSERT INTO "+tabelle+" ("+vars+") VALUES ('"+values+"')";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.executeUpdate();
		stmt.close();
		
	}
	
// UPDATE ===============================================================================================================
	
	public static void updateString(String tabelle, String target, String value, String keyName, String key) {
		
		try {
			String query = "UPDATE "+tabelle+" SET "+target+"="+value+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException error) {
			error.printStackTrace();
		}
		
	}
	public static void updateInt(String tabelle, String target, int value, String keyName, String key) {
		
		try {
			String query = "UPDATE "+tabelle+" SET "+target+"="+value+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException error) {
			error.printStackTrace();
		}
		
	}
	public static void updateDouble(String tabelle, String target, double value, String keyName, String key) {
	
		try {
			String query = "UPDATE "+tabelle+" SET "+target+"="+value+" where ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException error) {
			error.printStackTrace();
		}
		
	}
	
// DROP / REMOVE ===============================================================================================================
	
	public static void deleteData(String tabelle, String keyName, String key) {
		
		try {
			String query = "DELETE FROM "+tabelle+" WHERE ("+keyName+")=('"+key+"')";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException error) {
			error.printStackTrace();
		}
		
	}

	public static String doubleWriteNumber(int number) {
		
		if(number < 10) {
			return "0"+number;
		}else {
			return ""+number;
		}
		
	}

	public static Connection getConnection() {
		return connection;
	}
	
}
