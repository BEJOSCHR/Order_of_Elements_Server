package de.bejoschgaming.orderofelements.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import de.bejoschgaming.orderofelements.main.OOE_Main_Server;

public class ConsoleHandler {

	private static String prefix = "[OOE_S] ";
	public static int focusDebugID = -1;
	public static Timer consoleInputScanner = new Timer();
	
//==========================================================================================================
	/**
	 * Print simple Message in the console
	 * @param gameID - int - (-1) if not game related, else the gameID
	 * @param text - String - The message to print
	 * @param prefix - boolean - Enable/Disable Prefix
	 */
	public static void printMessageInConsole(String text, boolean prefix) { printMessageInConsole(-1, text, prefix); }
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
								printMessageInConsole("Terminated game session for game ["+oldID+"]", true);
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
							/*case "/game":
								sendCommand_game(inputs);
								break;
							case "/games":
								sendCommand_games(inputs);
								break;
							case "/player":
								sendCommand_player(inputs);
								break;
							case "/players":
								sendCommand_players(inputs);
								break;
							case "/groups":
								sendCommand_groups(inputs);
								break;
							case "/packets":
								sendCommand_packets(inputs);
								break;
							case "/overview":
								sendCommand_overview(inputs);
								break;
							case "/update":
								sendCommand_update(inputs);
								break;*/
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
		
		printMessageInConsole("ConsoleInputScanner started", true);
		
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
		
		printMessageInConsole("ConsoleInputScanner stopped", true);
		
	}
	
	private static void sendCommand_help(List<String> inputs) {
		
		printMessageInConsole("Choose one of these commands:", true);
		/*printMessageInConsole("'/game [id] ' - Join the game session so you see the log of the game", true);
		printMessageInConsole("'/games [quantity] ' - Shows the list of running games", true);
		printMessageInConsole("'/player [id|name] ' - Gives info about the player", true);
		printMessageInConsole("'/players [quantity] ' - Shows the list of online player", true);
		printMessageInConsole("'/groups [quantity] ' - Shows the list of active groups", true);
		printMessageInConsole("'/packets ' - Join the packet session so you see the traffic of packets", true);
		printMessageInConsole("'/overview ' - Gives a general overview about everything interesting", true);
		printMessageInConsole("'/update [units|upgrades] ' - Reloads the units or the upgrades from the DB", true); */
		printMessageInConsole("'/stop ' - Stoppes the whole server", true);
		
	}
	
	private static void sendCommand_stop(List<String> inputs) {
		
		OOE_Main_Server.terminateProgramm();
		
	}
	
}
