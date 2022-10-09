package de.bejoschgaming.orderofelements.decksystem;

import java.util.LinkedList;
import java.util.List;

import de.bejoschgaming.orderofelements.connection.ClientConnection;
import de.bejoschgaming.orderofelements.database.DatabaseHandler;

public class DeckHandler {

	public static List<Deck> getDecks(int playerID) {
		
		List<Integer> deckIDs = DatabaseHandler.getAllWhereEqual_Int(DatabaseHandler.tabellName_decks, "ID", "OwnerID", ""+playerID);
		
		List<Deck> output = new LinkedList<Deck>();
		for(int deckID : deckIDs) {
			output.add(new Deck(deckID, playerID));
		}
		
		return output;
		
	}
	
	public static void sendDecksToPlayer(List<Deck> decks, ClientConnection clientConnection) {
		
		for(Deck deck : decks) {
			//220-DeckID;DeckOwnerID;DeckName;DeckData
			clientConnection.sendPacket(220, deck.getDeckID()+";"+deck.getOwnerID()+";"+deck.getName()+";"+deck.getData());
		}
		
	}
	
}
