package de.bejoschgaming.orderofelements.connection;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import de.bejoschgaming.orderofelements.session.ClientSession;
import de.bejoschgaming.orderofelements.session.SessionHandler;

public class ConnectionEventHandler extends IoHandlerAdapter {

	@Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        
//		cause.printStackTrace();
		
    }
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		
		new ClientConnection(session);
//		ConsoleHandler.printMessageInConsole("Client connected from "+session.getRemoteAddress(), true);
		
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		
		SessionHandler.getSession(session).disconnect();
//		ConsoleHandler.printMessageInConsole("Client disconnected from "+session.getRemoteAddress(), true);
		
	}
	
    @Override
    public void messageReceived(IoSession session, Object messageReceived) throws Exception {
        
    	String rawMessage = messageReceived.toString();
    	String[] splitMessage = rawMessage.split(ConnectionHandler.packetDivider);
        int signal = Integer.parseInt(splitMessage[0]);
        String message = splitMessage[1];
    	
    	ClientSession clientSession = SessionHandler.getSession(session);
    	clientSession.getConnection().handlePacket(clientSession, signal, message);
        
    }
    
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {}
	
}
