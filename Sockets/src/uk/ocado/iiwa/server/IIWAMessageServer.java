package uk.ocado.iiwa.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class IIWAMessageServer extends Thread {
	
	private static ServerSocket serverSocket;
    private Socket clientSocket = null;
	private volatile boolean running = true;
	public ConcurrentLinkedDeque<String> message_queue = null;
	private int queue_size;
	
	
	public IIWAMessageServer(int port, int queue_size_) throws IOException {
		serverSocket = new ServerSocket(port);
		message_queue = new ConcurrentLinkedDeque<String>();
		queue_size = queue_size_;
	}
	
	public void terminate() {
		running = false;
	}

    @Override
	public void run() {
        
    	InputStream inputStream = null;
    	OutputStream outputStream = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutPutStream = null;
		
        while(running) {

        	Iterator<String> itr = message_queue.iterator();
        	while (itr.hasNext()) {
        	   System.out.println(itr.next());
        	}

        	waitForClientConnection();
        	
        	inputStream = getInputStream(clientSocket);
        	outputStream = getOutputStream(clientSocket);
        	
        	if(inputStream == null || outputStream == null) {
        		continue;
        	}
        	dataInputStream = new DataInputStream(inputStream);
        	parseDataInputStream(dataInputStream);     
		}

	}
    
    private void parseDataInputStream(DataInputStream inputStream) {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      	String inputLine;
    	try {
			while ((inputLine = reader.readLine()) != null) {
				if("END".equals(inputLine)) {
				}else {
					if(message_queue.size() == queue_size) {
						message_queue.remove();
					}
					message_queue.add(inputLine);
				}
				inputLine = "";
			}
		} catch (IOException e) {
			System.out.print("unable to read line");
			e.printStackTrace();
		}
    }
    
    private void waitForClientConnection() {
        try {
        	System.out.println("waiting for client");
			clientSocket = serverSocket.accept();
        } catch (IOException e) {
        	System.out.println("Unable to accept connection from client");
			e.printStackTrace();
		}
    }
    
    private InputStream getInputStream(Socket socket) {
        try {
			return socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

    private OutputStream getOutputStream(Socket socket) {
    	try {
			return socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public static void main(String[] args) {
		System.out.println("staring IIWA socket server!");
		int port = 6066;
		
		Thread iiwaServer;
		try {
			iiwaServer = new IIWAMessageServer(port, 10);
			iiwaServer.start();	
		} catch (IOException e) {
			System.out.println("Unable to start IIWA Server");
			e.printStackTrace();
		}
	}

}
