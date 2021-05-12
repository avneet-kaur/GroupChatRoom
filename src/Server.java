
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class accepting incoming requests on port 9999
 */

public class Server {

	public static void main(String[] args) {
		int port = 9999;
		Socket clientSocket;
		int clientCounter = 0;
		System.out.println("[Server] Server is started and listening on port: " + port);
		// ServerSocket is bound to specific port and listens clients on that port
		try (ServerSocket serverSocket = new ServerSocket(port);) {
			// Server keeps accepting incoming requests.
			while (true) {
				// accept is blocking method, keeps waiting for the incoming connections.
				// creates new socket for each client it accepts.
				// accept method returns client socket which helps in communication
				clientSocket = serverSocket.accept();
				clientCounter++;
				System.out.println("[Server] Request came for Client Number: " + clientCounter);
				// clientSocket has an InputStream to read incoming data(read bytes sequentially)
				BufferedReader readFromClient = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				// clientSocket has an OutputStream to send data (write bytes sequentially)
				PrintWriter writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
				// creating object to handle the particular request
				ClientHandler clientHandler = new ClientHandler(clientSocket, readFromClient, writeToClient);
				// new thread assigned to specific client, so that main thread shall be kept
				// waiting for another client requests.
				Thread clientThread = new Thread(clientHandler);
				clientThread.start();
			}
		}
		catch (IOException e) {
			System.err.println("[Server]: Getting an exception with error message: " + e.getMessage());
		}
	}
}
