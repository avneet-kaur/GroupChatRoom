
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * Demonstration of Client and connecting client to host server (localhost) on
 * port 9999.
 */

@SuppressWarnings("serial")
public class Client extends JFrame {

	final static int port = 9999;
	private String serverAddress;
	private PrintWriter writeToServer;
	private Scanner readFromServer;
	Socket clientSocket;
	private JButton button;
	private JFrame frame = new JFrame("GROUP CHAT");
	private JTextField textField = new JTextField(45);
	private JTextArea textArea = new JTextArea(20, 45);

	public Client(String ip) {
		// client opens a connection to server address: localhost and port 9999 by
		// constructing clientSocket
		// if the connection is successful, client will get two streams to communicate
		// with server.
		try {
			this.serverAddress = ip;
			clientSocket = new Socket(serverAddress, port);
			// an input stream for reading from this socket.
			readFromServer = new Scanner(clientSocket.getInputStream());
			// an output stream for writing to this socket.
			writeToServer = new PrintWriter(clientSocket.getOutputStream(), true);
		}
		catch (UnknownHostException e1) {
			System.err.println("[Client]: Getting an exception with error message: " + e1.getMessage());
			e1.printStackTrace();
			System.exit(1);
		}
		catch (IOException e1) {
			System.err.println("[Client]: Getting an exception with error message: " + e1.getMessage());
			e1.printStackTrace();
		}
		
		button = new JButton("SEND");
		button.setVisible(true);
		button.setFont(new Font("SAN_SERIF", Font.ITALIC, 16));
		button.setBorder(new LineBorder(new Color(122, 18, 48), 3));
		frame.add(button, BorderLayout.EAST);

		textField.setEditable(false);
		textField.setForeground(new Color(122, 67, 83));
		textField.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
		textField.setBorder(new LineBorder(new Color(122, 18, 48), 2));

		textArea.setEditable(false);
		textArea.setForeground(new Color(122, 67, 83));
		textArea.setBackground(new Color(255, 253, 255));
		textArea.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
		textArea.setBorder(new LineBorder(new Color(122, 18, 48), 4));

		frame.getContentPane().add(textField, BorderLayout.PAGE_END);
		frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		frame.pack();

		// pressing enter will send message and clear the text field
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeToServer.println(textField.getText());
				textField.setText("");
			}
		});

		// clicking on send button will send message and clear the text field
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeToServer.println(textField.getText());
				textField.setText("");
			}
		});
	}

	/**
	 * This function pop up the dialog box so that user can type unique name and if
	 * name is not unique it will display the same screen again. If user tries to
	 * press enter without typing name or click on OK or Cancel the screen, the
	 * alert box will be popped up displaying alert message. And after that window
	 * will be closed and client have to enter again with the startup command.
	 * 
	 * @return unique user name
	 */
	public String getUniqueUserName() {
		String response = JOptionPane.showInputDialog(frame, "Please enter your name:", "Client Screen",
				JOptionPane.PLAIN_MESSAGE);
		try {
			if ((response == null) || (response.length() == 0) || (response.equals(" "))
					|| (response.trim().isEmpty())) {
				JOptionPane.showMessageDialog(frame,
						"This field cannot be empty. Please try again and type your name to enter in the Chat Room",
						"Alert", JOptionPane.INFORMATION_MESSAGE);
				System.exit(1);
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
		return response;
	}

	/**
	 * listen to socket and print the broadcasted message on Text Area
	 */
	private void listen() {

		while (readFromServer.hasNextLine()) {
			String line = readFromServer.nextLine();
			// Writing to server the name that is entered on pop up dialog box
			if (line.startsWith("NAMEACKNOWLEDGED")) {
				writeToServer.println(getUniqueUserName());
			}
			// Unique user name approved and displayed as a Title
			else if (line.startsWith("NAMEAPPROVED")) {
				this.frame.setTitle("GROUP CHAT - " + line.substring(12));
				textField.setEditable(true);
			}
			// Broadcasted message displayed on client's text area
			else if (line.startsWith("TEXT")) {
				textArea.append(line.substring(4) + "\n");
			}
		}
		// closing streams
		readFromServer.close();
		writeToServer.close();
		try {
			clientSocket.close();
		}
		catch (IOException e) {
			System.err.println("[Client]: Getting an exception with error message: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			frame.setVisible(false);
			frame.dispose();
		}
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.err.println("Please use this Startup Command: java Client [serverAddress]");
			throw new IllegalArgumentException("ServerAddress required");
		}

		Client client = new Client(args[0]);
		System.out.println("[Client]: Welcome to the Group Chat");
		System.out.println("[Client]: Please read the following instructions to proceed: ");
		System.out.println("[Client]: 1. Enter your unique name in the popup dialog box to enter in the Group Chat");
		System.out.println(
				"[Client]: 2. In case you click OK or CANCEL the dialog box without typing your name, you will be logged out from the Chat Window and you have to enter again by running the startup command.");
		System.out.println("[Client]: 3. Type the Message on text Field to broadcast it to another clients");
		System.out.println("[Client]: 4. Type 'ACTIVECLIENTS' to check the active clients.");
		System.out.println("[Client]: 5. Type 'LOGOUT' if you want to exit from the group.");

		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.listen();
	}
}
