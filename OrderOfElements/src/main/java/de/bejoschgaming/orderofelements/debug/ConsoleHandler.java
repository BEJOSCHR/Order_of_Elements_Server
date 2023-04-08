package de.bejoschgaming.orderofelements.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import de.bejoschgaming.orderofelements.gamesystem.Game;
import de.bejoschgaming.orderofelements.gamesystem.GameHandler;
import de.bejoschgaming.orderofelements.main.OOE_Main_Server;
import de.bejoschgaming.orderofelements.mapsystem.MapHandler;
import de.bejoschgaming.orderofelements.patchnotessystem.PatchnotesHandler;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;
import de.bejoschgaming.orderofelements.sessionsystem.SessionHandler;
import de.bejoschgaming.orderofelements.unitsystem.Unit;
import de.bejoschgaming.orderofelements.unitsystem.UnitCategory;
import de.bejoschgaming.orderofelements.unitsystem.UnitHandler;
import de.bejoschgaming.orderofelements.unitsystem.UnitTargetPattern;

public class ConsoleHandler {

	private static String prefix = "[OOE_S] ";
	public static int focusDebugID = -1;
	public static Timer consoleInputScanner = new Timer();
	
//==========================================================================================================
	public static void printMessageInConsole(String text, boolean prefix) { printMessageInConsole(-1, text, prefix); }
	/**
	 * Print simple Message in the console
	 * @param gameID - int - (-1) its non specific debug - (0) means its a package debug - else a gameID related debug
	 * @param text - String - The message to print
	 * @param prefix - boolean - Enable/Disable Prefix
	 */
	public static void printMessageInConsole(int gameID, String text, boolean prefix) {
		
		if(gameID == focusDebugID) {
			//RIGHT MODE
			if(prefix == true) {
				System.out.println(ConsoleHandler.prefix+text);
			}else {
				System.out.println(text);
			}
		}
		
	}
//==========================================================================================================
	/**
	 * Print an empty line
	 */
	public static void printBlankLineInConsole() { printBlankLineInConsole(-1); }
	public static void printBlankLineInConsole(int gameID) {
		if(gameID == focusDebugID) {
			System.out.println("");
		}
	}
	
//==========================================================================================================
	/**
	 * Start Scanner which checks for new user console input
	 * @see consoleInputScanner
	 */
	public static void startUserInputScanner() {
		
		consoleInputScanner.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				
				try {
					@SuppressWarnings("resource") //DARF NICHT GESCHLOSSEN WERDEN!
					Scanner consoleInput = new Scanner(System.in);
					
					if(consoleInput.hasNextLine()) {
						
						if(focusDebugID != -1) {
							
							//GAME SESSION
							int oldID = focusDebugID;
							focusDebugID = -1; //LEAVE SESSION
							printBlankLineInConsole();
							if(oldID == 0) {
								//PACKETS
								printMessageInConsole("Terminated packets session", true);
							}else {
								//GAME
								printMessageInConsole("Terminated game session for id ["+oldID+"]", true);
							}
							printBlankLineInConsole();
							
						}else {
							
							String input = consoleInput.nextLine();
							List<String> inputs = ArrayFromPattern(input.split(" "));
							String keyWord = inputs.get(0);
							
							switch(keyWord) { 
							case "/help":
								sendCommand_help(inputs);
								break;
							case "/packets":
								sendCommand_packets(inputs);
								break;
							case "/overview":
								sendCommand_overview(inputs);
								break;
							case "/session":
								sendCommand_session(inputs);
								break;
							case "/sessions":
								sendCommand_sessions(inputs);
								break;
							case "/units":
								sendCommand_units(inputs);
								break;
							case "/game":
								sendCommand_game(inputs);
								break;
							case "/games":
								sendCommand_games(inputs);
								break;
							case "/update":
								sendCommand_update(inputs);
								break;
							case "/stop":
								sendCommand_stop(inputs);
								break;
							default:
								printMessageInConsole("Unknown input! Use '/help' for details...", true);
								break;
							}
							
						}
						
					}
				}catch(Exception error) {
					error.printStackTrace();
					printMessageInConsole("Can't handle this input! [Error]", true);
				}
				
			}
		}, 0, 60);
		
		printMessageInConsole("ConsoleInputScanner started!", true);
		
	}

	/**
	 * Convert an String Pattern into an String ArrayList
	 * @param Input - String[] - The String Pattern shold be converted
	 * @return Output - ArrayList(String) - The converted ArrayList
	 */
	public static List<String> ArrayFromPattern(String[] Input) {
		List<String> Output = new ArrayList<String>();
		for(String Inhalt : Input) {
			Output.add(Inhalt);
		}
		return Output;
	}
	
//==========================================================================================================
	/**
	 * Stop Scanner which checks for new user console input
	 * @see consoleInputScanner
	 */
	public static void stopUserInputScanner() {
		
		consoleInputScanner.cancel();
		consoleInputScanner.purge();
		
		printMessageInConsole("ConsoleInputScanner stopped!", true);
		
	}
	
	private static void sendCommand_help(List<String> inputs) {
		
		printMessageInConsole("Choose one of these commands:", true);
		printMessageInConsole("'/packets ' - Join the packet session so you see the traffic of packets", true);
		printMessageInConsole("'/overview ' - Gives a general overview about everything interessting", true);
		printMessageInConsole("'/session [id|name] ' - Gives info about the session", true);
		printMessageInConsole("'/sessions ([start] [end]) ' - Shows the list of connected sessions", true);
		printMessageInConsole("'/units [category|pattern] ' - Infos about units, unitCategories and unitTargetPattern", true);
		printMessageInConsole("'/game [id] ' - Join the game session so you see the log of the game", true);
		printMessageInConsole("'/games ([start] [end]) ' - Shows the list of running games", true);
		printMessageInConsole("'/update [units|patchnotes|maps] ' - Reloads the targeted data from the DB", true);
		printMessageInConsole("'/stop ' - Stoppes the whole server", true);
		
	}
	
	private static void sendCommand_overview(List<String> inputs) {
		
		printMessageInConsole("Running OrderOfElements-Server since "+(System.currentTimeMillis()-OOE_Main_Server.startMillis)/1000/60+" min", true);
		printMessageInConsole("Running games: "+GameHandler.getRunningGames().size(), true);
		printMessageInConsole("Connected sessions: "+SessionHandler.numberOfConnectedSessions(), true);
		int sendPackets = 0;
		for(ClientSession session : SessionHandler.getConnectedSessions()) {
			sendPackets += session.getConnection().getSendPackets().size();
		}
		printMessageInConsole("Send packets: "+sendPackets, true);
		
	}
	
	private static void sendCommand_packets(List<String> inputs) {
		
		printBlankLineInConsole();
		printMessageInConsole("Joined packets session", true);
		printBlankLineInConsole();
		focusDebugID = 0;
		
	}

	private static void sendCommand_session(List<String> inputs) {
		
		if(inputs.size() >= 2) {
			try {
				//ID
				int id = Integer.parseInt(inputs.get(1));
				ClientSession session = SessionHandler.getSession(id);
				if(session != null) {
					printMessageInConsole(session.getShortInfo(), true);
				}else {
					printMessageInConsole("There is no session with ID '"+id+"' online!", true);
				}
			}catch(NumberFormatException error) {
				//NAME
				String name = inputs.get(1);
				ClientSession session = SessionHandler.getSession(name);
				if(session != null) {
					printMessageInConsole(session.getShortInfo(), true);
				}else {
					printMessageInConsole("There is no session with name '"+name+"' online!", true);
				}
			}
		}else {
			printMessageInConsole("/session [ID|Name]", true);
		}
		
	}
	
	private static void sendCommand_sessions(List<String> inputs) {
		
		if(SessionHandler.getConnectedSessions().isEmpty()) {
			//EMPTY
			printMessageInConsole("There are no connected sessions at the moment!", true);
			return;
		}
		
		if(inputs.size() >= 3) {
			//HAS NUMBER
			try {
				int start = Integer.parseInt(inputs.get(1));
				int end = Integer.parseInt(inputs.get(2));
				start = ( start > 0 ? start : 1 );
				start = ( SessionHandler.numberOfConnectedSessions() >= start ? start : SessionHandler.numberOfConnectedSessions() );
				end = ( end > 0 ? end : 1 );
				end = ( SessionHandler.numberOfConnectedSessions() >= end ? end : SessionHandler.numberOfConnectedSessions() );
				if(start > end) { ConsoleHandler.printMessageInConsole("Invalid argument! ["+start+">"+end+"]", true); return; }
				printMessageInConsole("Showing "+start+" to "+end+" from max. "+SessionHandler.numberOfConnectedSessions()+" connected sessions:", true);
				for(int i = start ; i <= end ; i++) {
					ClientSession session = SessionHandler.getConnectedSessions().get(i-1);
					printMessageInConsole(i+". "+session.getShortInfo(), true);
				}
			}catch(NumberFormatException error) {
				printMessageInConsole("/sessions or /sessions [start] [end]", true);
			}
		}else { 
			//NO NUMBER
			int start = 1;
			int end = 10;
			end = ( SessionHandler.numberOfConnectedSessions() >= end ? end : SessionHandler.numberOfConnectedSessions() );
			printMessageInConsole("Showing "+start+" to "+end+" from max. "+SessionHandler.numberOfConnectedSessions()+" connected sessions:", true);
			for(int i = start ; i <= end ; i++) {
				ClientSession session = SessionHandler.getConnectedSessions().get(i-1);
				printMessageInConsole(i+". "+session.getShortInfo(), true);
			}
		}
		
	}

	private static void sendCommand_units(List<String> inputs) {
		
		if(inputs.size() >= 2) {
			if(inputs.get(1).equalsIgnoreCase("category") || inputs.get(1).equalsIgnoreCase("categories")) {
				List<UnitCategory> categories = UnitHandler.getUnitCategories();
				printMessageInConsole("UnitsCategory overview (Total "+categories.size()+"): ", true);
				for(int i = 0 ; i < categories.size() ; i++) {
					UnitCategory category = categories.get(i);
					printMessageInConsole((i+1)+". Category: "+category.getCategory()+" - Description: "+category.getDescription().substring(0, 70)+"...", true);
				}
			}else if(inputs.get(1).equalsIgnoreCase("pattern")) {
				List<UnitTargetPattern> targetPatterns = UnitHandler.getUnitTargetPattern();
				printMessageInConsole("UnitsTargetPattern overview (Total "+targetPatterns.size()+"): ", true);
				for(int i = 0 ; i < targetPatterns.size() ; i++) {
					UnitTargetPattern targetPattern = targetPatterns.get(i);
					printMessageInConsole((i+1)+". Pattern: "+targetPattern.getPattern()+" - RelativeTargetPoints: "+targetPattern.getTargetRelatives().size(), true);
				}
			}else {
				printMessageInConsole("'/units' or '/units [category|pattern]' ", true);
			}
		}else {
			printMessageInConsole("For more infos: /units [category|pattern] ", true);
			List<Unit> units = UnitHandler.getUnits();
			printMessageInConsole("Units overview (Total "+units.size()+"): ", true);
			for(int i = 0 ; i < units.size() ; i++) {
				Unit unit = units.get(i);
				printMessageInConsole((i+1)+". ID: "+unit.getId()+" - Name: "+unit.getName()+" - Category: "+unit.getCategory().getCategory()+" ("+unit.getType_attack().getPattern()+"|"+unit.getType_move().getPattern()+"|"+unit.getType_aura().getPattern()+")", true);
			}
		}
		
	}
	
	private static void sendCommand_game(List<String> inputs) {
		
		if(inputs.size() >= 2) {
			try {
				int gameID = Integer.parseInt(inputs.get(1));
				if(GameHandler.getGame(gameID) != null) {
					printBlankLineInConsole();
					printMessageInConsole("Joined game session for id ["+gameID+"]", true);
					printBlankLineInConsole();
					focusDebugID = gameID;
				}else {
					printMessageInConsole("There is no running game for id: "+gameID+"!", true);
				}
			}catch(NumberFormatException error) {
				printMessageInConsole(inputs.get(0)+" is not a valid number!", true);
			}
		}else {
			printMessageInConsole("/game [id] ", true);
		}
		
	}
	
	private static void sendCommand_games(List<String> inputs) {
		
		if(GameHandler.getRunningGames().isEmpty()) {
			//EMPTY
			printMessageInConsole("There are no running games at the moment!", true);
			return;
		}
		
		if(inputs.size() >= 3) {
			//HAS NUMBER
			try {
				int start = Integer.parseInt(inputs.get(1));
				int end = Integer.parseInt(inputs.get(2));
				start = ( start > 0 ? start : 1 );
				start = ( GameHandler.getRunningGames().size() >= start ? start : GameHandler.getRunningGames().size() );
				end = ( end > 0 ? end : 1 );
				end = ( GameHandler.getRunningGames().size() >= end ? end : GameHandler.getRunningGames().size() );
				if(start > end) { ConsoleHandler.printMessageInConsole("Invalid argument! ["+start+">"+end+"]", true); return; }
				printMessageInConsole("Showing "+start+" to "+end+" from max. "+GameHandler.getRunningGames()+" running games:", true);
				for(int i = start ; i <= end ; i++) {
					Game game = GameHandler.getRunningGames().get(i);
					printMessageInConsole(i+". "+game.getShortInfo(), true);
				}
			}catch(NumberFormatException error) {
				printMessageInConsole("/sessions or /sessions [start] [end]", true);
			}
		}else { 
			//NO NUMBER
			int start = 1;
			int end = 10;
			end = ( GameHandler.getRunningGames().size() >= end ? end : GameHandler.getRunningGames().size() );
			printMessageInConsole("Showing "+start+" to "+end+" from max. "+GameHandler.getRunningGames().size()+" running games:", true);
			for(int i = start ; i <= end ; i++) {
				Game game = GameHandler.getRunningGames().get(i);
				printMessageInConsole(i+". "+game.getShortInfo(), true);
			}
		}
		
	}
	
	private static void sendCommand_update(List<String> inputs) {
		
		if(inputs.size() >= 2) {
			
			String targetUpdate = inputs.get(1);
			
			switch (targetUpdate) {
			case "units":
				UnitHandler.loadUnitData();
				ConsoleHandler.printMessageInConsole("Loaded "+UnitHandler.getUnitCategories().size()+" unitCategories, "+UnitHandler.getUnitTargetPattern().size()+" unitTargetPatterns and "+UnitHandler.getUnits().size()+" units!", true);
				break;
			case "patchnotes":
				PatchnotesHandler.loadPatchnotesData();
				printMessageInConsole("Loaded patchnotes from DB on manuell reload! ("+(PatchnotesHandler.getPatchnotesData()!=null)+")", true);
				for(ClientSession session : SessionHandler.getConnectedSessions()) {
					PatchnotesHandler.sendPatchnotesData(session);
				}
				break;
			case "maps":
				MapHandler.loadMapsFromDB();
				printMessageInConsole("Loaded "+MapHandler.getLoadedMaps().size()+" maps from DB on manuell reload!", true);
				break;
			default:
				printMessageInConsole("/update [units|patchnotes|maps]", true);
				break;
			}
			
		}else {
			printMessageInConsole("/update [units|patchnotes|maps]", true);
		}
		
	}
	
	
	private static void sendCommand_stop(List<String> inputs) {
		
		OOE_Main_Server.terminateProgramm();
		
	}
	
}
