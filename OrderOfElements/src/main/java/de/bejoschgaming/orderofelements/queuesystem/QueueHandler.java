package de.bejoschgaming.orderofelements.queuesystem;

import java.util.ArrayList;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.gamesystem.GameHandler;
import de.bejoschgaming.orderofelements.gamesystem.GameType;
import de.bejoschgaming.orderofelements.sessionsystem.ClientSession;
import de.bejoschgaming.orderofelements.sessionsystem.SessionHandler;

public class QueueHandler {

	private static final int normalQueue_rankingDifferenz = 2;
	private static final int normalQueue_rankingPointsDifferenz = 100; // 0 means has to fit exactly, 100 means it allways fits
	private static final int rankedQueue_rankingDifferenz = 1; //1 means as example: you are GOLD, then you can get matched with PLATINUM, GOLD or SILVER, with 0 it would only be GOLD
	private static final int rankedQueue_rankingPointsDifferenz = 100; // 0 means has to fit exactly, 100 means it allways fits
	private static final int spezialQueue_rankingDifferenz = 3;
	private static final int spezialQueue_rankingPointsDifferenz = 100; // 0 means has to fit exactly, 100 means it allways fits
	
	private static ArrayList<ClientSession> queues_normal = new ArrayList<ClientSession>();
	private static ArrayList<ClientSession> queues_ranked = new ArrayList<ClientSession>();
	private static ArrayList<ClientSession> queues_spezial = new ArrayList<ClientSession>();
	
	private static void matchQueues(QueueType type) {
		
		switch (type) {
		case NORMAL:
			for(ClientSession session1 : queues_normal) {
				for(ClientSession session2 : queues_normal) {
					int rankingDifferenz = session1.getProfile().getRanking().getNumber()-session2.getProfile().getRanking().getNumber();
					if(rankingDifferenz >= -normalQueue_rankingDifferenz && rankingDifferenz <= normalQueue_rankingDifferenz) {
						//RANKING FITS
						int rankingPointDifferenz = session1.getProfile().getRankingPoints()-session2.getProfile().getRankingPoints();
						if(rankingPointDifferenz >= -normalQueue_rankingPointsDifferenz && rankingPointDifferenz <= normalQueue_rankingPointsDifferenz) {
							//RANKING POINTS FIT
							
							//CREATE GAME
							GameHandler.registerNewGame(session1, session2, GameType.NORMAL);
							
							ConsoleHandler.printMessageInConsole("Matched "+session1.getProfile().getName()+" ("+session1.getProfile().getRanking()+"-"+session1.getProfile().getRankingPoints()+") with "+session2.getProfile().getName()+" ("+session2.getProfile().getRanking()+"-"+session2.getProfile().getRankingPoints()+") in queue "+type+"!", true);
							return;
						}
					}
				}
			}
			break;
		case RANKED:
			for(ClientSession session1 : queues_ranked) {
				for(ClientSession session2 : queues_ranked) {
					int rankingDifferenz = session1.getProfile().getRanking().getNumber()-session2.getProfile().getRanking().getNumber();
					if(rankingDifferenz >= rankedQueue_rankingDifferenz && rankingDifferenz <= rankedQueue_rankingDifferenz) {
						//RANKING FITS
						int rankingPointDifferenz = session1.getProfile().getRankingPoints()-session2.getProfile().getRankingPoints();
						if(rankingPointDifferenz >= -rankedQueue_rankingPointsDifferenz && rankingPointDifferenz <= rankedQueue_rankingPointsDifferenz) {
							//RANKING POINTS FIT
							
							//CREATE GAME
							GameHandler.registerNewGame(session1, session2, GameType.RANKED);
							
							ConsoleHandler.printMessageInConsole("Matched "+session1.getProfile().getName()+" ("+session1.getProfile().getRanking()+"-"+session1.getProfile().getRankingPoints()+") with "+session2.getProfile().getName()+" ("+session2.getProfile().getRanking()+"-"+session2.getProfile().getRankingPoints()+") in queue "+type+"!", true);
							return;
						}
					}
				}
			}
			break;
		case SPEZIAL:
			for(ClientSession session1 : queues_spezial) {
				for(ClientSession session2 : queues_spezial) {
					int rankingDifferenz = session1.getProfile().getRanking().getNumber()-session2.getProfile().getRanking().getNumber();
					if(rankingDifferenz >= -spezialQueue_rankingDifferenz && rankingDifferenz <= spezialQueue_rankingDifferenz) {
						//RANKING FITS
						int rankingPointDifferenz = session1.getProfile().getRankingPoints()-session2.getProfile().getRankingPoints();
						if(rankingPointDifferenz >= -spezialQueue_rankingPointsDifferenz && rankingPointDifferenz <= spezialQueue_rankingPointsDifferenz) {
							//RANKING POINTS FIT
							
							//CREATE GAME
							GameHandler.registerNewGame(session1, session2, GameType.SPEZIAL);
							
							ConsoleHandler.printMessageInConsole("Matched "+session1.getProfile().getName()+" ("+session1.getProfile().getRanking()+"-"+session1.getProfile().getRankingPoints()+") with "+session2.getProfile().getName()+" ("+session2.getProfile().getRanking()+"-"+session2.getProfile().getRankingPoints()+") in queue "+type+"!", true);
							return;
						}
					}
				}
			}
			break;
		}
		
	}
	
	public static void addToQueue(int playerID, QueueType type) {
		addToQueue(SessionHandler.getSession(playerID), type);
	}
	public static void addToQueue(ClientSession session, QueueType type) {
		
		switch (type) {
		case NORMAL:
			queues_normal.add(session);
			break;
		case RANKED:
			queues_ranked.add(session);
			break;
		case SPEZIAL:
			queues_spezial.add(session);
			break;
		}
		ConsoleHandler.printMessageInConsole("Added "+session.getProfile().getID()+":"+session.getProfile().getName()+" to "+type+" queue!", true);
		matchQueues(type);
		
	}
	
	public static boolean removeFromQueue(ClientSession session, QueueType type) {
		return removeFromQueue(session.getSessionID(), type);
	}
	public static boolean removeFromQueue(int playerID, QueueType type) {
		
		switch (type) {
		case NORMAL:
			for(ClientSession session : queues_normal) {
				if(session.getSessionID() == playerID) {
					queues_normal.remove(session);
					ConsoleHandler.printMessageInConsole("Removed "+session.getProfile().getID()+":"+session.getProfile().getName()+" from "+type+" queue!", true);
					return true;
				}
			}
			break;
		case RANKED:
			for(ClientSession session : queues_ranked) {
				if(session.getSessionID() == playerID) {
					queues_ranked.remove(session);
					ConsoleHandler.printMessageInConsole("Removed "+session.getProfile().getID()+":"+session.getProfile().getName()+" from "+type+" queue!", true);
					return true;
				}
			}
			break;
		case SPEZIAL:
			for(ClientSession session : queues_spezial) {
				if(session.getSessionID() == playerID) {
					queues_spezial.remove(session);
					ConsoleHandler.printMessageInConsole("Removed "+session.getProfile().getID()+":"+session.getProfile().getName()+" from "+type+" queue!", true);
					return true;
				}
			}
			break;
		}
		return false;
		
	}
	
}
