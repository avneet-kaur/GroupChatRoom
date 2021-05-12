
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Helper class to handle client requests
 */

class ClientHandler implements Runnable {
	boolean isActive;
	private String clientName;
	private Socket clientSocket;
	private PrintWriter writeToClient;
	private BufferedReader readFromClient;

	// vector storing clients
	static Vector<String> clientNames = new Vector<>();

	// using set of printwriter to successfully deliver message to the clients
	private static Set<PrintWriter> printWriters = new HashSet<>();

	public ClientHandler(Socket clientSocket, BufferedReader readFromClient, PrintWriter writeToClient) {
		this.clientSocket = clientSocket;
		this.readFromClient = readFromClient;
		this.writeToClient = writeToClient;
		isActive = true;
	}

	public void run() {
		try {
			// storing output stream of the connected clients
			printWriters.add(writeToClient);

			// checking for unique name of the connected client
			while (true) {
				writeToClient.println("NAMEACKNOWLEDGED");
				clientName = readFromClient.readLine();

				if (!clientName.isEmpty() && !clientNames.contains(clientName)) {
					// adding unique client name to the vector
					clientNames.add(clientName);
					break;
				} else {
					System.err.println("[ClientHandler]: Client name with " + clientName
							+ " already exists. Please try with other user name");
				}
			}

			// if client name is unique and it has been accepted, all clients will be
			// notified
			// that new client user has joined the chat room
			writeToClient.println("NAMEAPPROVED" + clientName);
			for (PrintWriter writer : printWriters) {
				writer.println("TEXT" + clientName + ": joined the group chat");
			}
			System.out.println("[ClientHandler]: Client: " + clientName + " is online");

			while (true) {
				// read message from client
				String input = readFromClient.readLine();
				if (input.isEmpty() || input == null) {
					System.err.println("[ClientHandler]: Please enter your message, your text field is empty");
				}

				// client wants to logout
				if (input.equalsIgnoreCase("logout")) {
					this.isActive = false;
					break;
				}

				// broadcasting the message
				for (PrintWriter writer : printWriters) {
					if (!input.trim().isEmpty() && input != null && !input.equalsIgnoreCase("ActiveClients")) {
						writer.println("TEXT" + clientName + ": " + input);
					}
				}

				// checking list of active clients
				if (input.equalsIgnoreCase("ActiveClients")) {
					System.out.println("[ClientHandler]: List of Active Clients:");
					int counter = 1;
					writeToClient.println("TEXT" + clientName + ": " + "List of Active Clients: ");
					for (String name : clientNames) {
						writeToClient.println("TEXT" + counter + ". " + name);
						System.out.println(counter + ". " + name);
						counter++;
					}
				}

			}
		} catch (Exception e) {
			System.err.println("[ClientHandler]: The chat window is closed by the client: " + clientName);
		} finally {
			// removing client's output stream from the list
			if (writeToClient != null) {
				printWriters.remove(writeToClient);
			}

			// removing client's name from the list
			if (clientName != null) {
				clientNames.remove(clientName);
				this.isActive = false;
				for (PrintWriter writer : printWriters) {
					writer.println("TEXT" + clientName + " has left");
				}
				System.out.println("[ClientHandler]: Client: " + clientName + " is offline");
			}

			// closing the streams and socket
			try {
				readFromClient.close();
				writeToClient.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("[ClientHandler] Getting an exception with error message: " + e.getMessage());
			}
		}
	}
}
