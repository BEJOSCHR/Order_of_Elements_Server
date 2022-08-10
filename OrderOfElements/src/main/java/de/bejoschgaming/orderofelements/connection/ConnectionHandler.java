package de.bejoschgaming.orderofelements.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.filesystem.FileHandler;

public class ConnectionHandler {

	public static final int PORT = Integer.parseInt(FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_Port"));
	public static final int IDLETIME = Integer.parseInt(FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_Idletime"));
	private static IoAcceptor connectionAcceptor = null;

	public static final String packetDivider = FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_Packetdivider");
	
//	https://mina.apache.org/mina-project/userguide/ch2-basics/ch2.2-sample-tcp-server.html
	
	public static void startServerConnection() {
		
        try {
        	connectionAcceptor = new NioSocketAcceptor();
//        	connectionAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
            connectionAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
            connectionAcceptor.setHandler(new ConnectionEventHandler());
            connectionAcceptor.setCloseOnDeactivation(true);
            connectionAcceptor.getSessionConfig().setReadBufferSize(2048);
            connectionAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLETIME);
			connectionAcceptor.bind(new InetSocketAddress(PORT));
			ConsoleHandler.printMessageInConsole("Serverconnection started!", true);
        } catch(IOException error) {
			error.printStackTrace();
			ConsoleHandler.printMessageInConsole("Starting serverconnection failed!", true);
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					ConsoleHandler.printMessageInConsole("Terminating...", true);
					System.exit(0);
				}
			}, 1000*3);
			while(true) {} //FREEZE FOR TIMER EXIT
		}
		
	}
	
	public static void closeServerConnection() {
		
		connectionAcceptor.unbind();
		connectionAcceptor.dispose(true);
		ConsoleHandler.printMessageInConsole("Serverconnection closed!", true);
		
	}
	
}
