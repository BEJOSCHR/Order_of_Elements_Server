package de.bejoschgaming.orderofelements.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import de.bejoschgaming.orderofelements.debug.ConsoleHandler;

public class ConnectionHandler {

	public static final int PORT = 6776;
	private static IoAcceptor connectionAcceptor = null;

	public static final String packetDivider = "_:_";
	
//	https://mina.apache.org/mina-project/userguide/ch2-basics/ch2.2-sample-tcp-server.html
	
	public static void startServerConnection() {
		
        try {
        	connectionAcceptor = new NioSocketAcceptor();
//        	connectionAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
            connectionAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
            connectionAcceptor.setHandler(new ConnectionEventHandler());
            connectionAcceptor.setCloseOnDeactivation(true);
            connectionAcceptor.getSessionConfig().setReadBufferSize(2048);
            connectionAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			connectionAcceptor.bind(new InetSocketAddress(PORT));
			ConsoleHandler.printMessageInConsole("Serverconnection started!", true);
        } catch(IOException error) {
			error.printStackTrace();
			ConsoleHandler.printMessageInConsole("Starting serverconnection failed!", true);
			System.exit(1);
		}
		
	}
	
	public static void closeServerConnection() {
		
		connectionAcceptor.unbind();
		connectionAcceptor.dispose();
		ConsoleHandler.printMessageInConsole("Serverconnection closed!", true);
		
	}
	
}
