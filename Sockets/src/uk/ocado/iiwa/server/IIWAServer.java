package uk.ocado.iiwa.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class IIWAServer extends Thread {
	
	private static ServerSocket serverSocket;
    private volatile boolean running = true;
	
	public IIWAServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}
	
	public void terminate() {
		running = false;
	}

    @Override
	public void run() {
        Socket clientSocket = null;
        DataInputStream inputStream = null;
		while(running) {
            try {
            	System.out.println("waiting for client");
				clientSocket = serverSocket.accept();
            } catch (IOException e) {
            	System.out.println("unable to accept connection");
				e.printStackTrace();
			}

		try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
			String message = parseInputStream(inputStream);		
			System.out.println("message: " + message);			
		}catch (IOException e1) {
			e1.printStackTrace();
		}
            
			/*try {

				while(clientSocket.isBound()) {
					inputStream = new DataInputStream(clientSocket.getInputStream());

					if(inputStream.available() > 0) {
						String message = parseInputStream(inputStream);		
						System.out.println("message: " + message);
					}
				}
				System.out.println("socket closed!");
			}catch (IOException e1) {
				e1.printStackTrace();
			}*/
		}

	}
    
    private String parseInputStream(DataInputStream inputStream) {
    	String line = "";
    	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    	try {
			line = reader.readLine();
		} catch (IOException e1) {
			System.out.println("unable to read line");
			e1.printStackTrace();
		}
    	return line;
    }
	
	public static void main(String[] args) {
		System.out.println("staring IIWA socket server!");
		int port = 6066;
		
		Thread iiwaServer;
		try {
			iiwaServer = new IIWAServer(port);
			iiwaServer.start();	
		} catch (IOException e) {
			System.out.println("Unable to start IIWA Server");
			e.printStackTrace();
		}
	}

}
