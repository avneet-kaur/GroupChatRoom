GROUP CHAT APPLICATION:-

	The chat application handle multiple client requests at the same time using Multithreading. You can run Client code multiple times using Satrtup command so that multilpe clients can communicate with the server. Port used is 9999.

Startup command to run the client :- 
	
	java Client [serverAddress] 
	eg :- java Client localhost

Server Class

	1. Server socket waits for requests to come in over the network and listens clients on that port.
	2. Server keeps accepting incoming request by running an infinte loop.
	3. New socket "clientSocket" is created for each client it accepts. "clientSocket" acts an endpoint for communication between two machines.
	4. Upon arrival of new request, clientHandler object is created and new thread is assigned to that object to Handle the communication.
	5. Start method of thread is invoked to begin execution.

ClienHandler Class

	1. This class is used to porcess and handle client's request.
	2. The client names are stored in vetcor to keep track of active clients.
	3. To broadcast message to active clients, set of print writer is maintained which consists of active output stream of active clients.
	4. Initially, client will enter unique name in popup dialog box. Client will not be allowed to enter into chat group until unique name is entered.
	5. If client, tries to cancel or press ok without typing name the popup dialog box will pop up alert dialog box with alert to type name. And client will be disconnected.
	6. After the client enters the group chat, the name of the client will be appeared as a title. And all the clients will get notified that new user has joined.
	7. Client can logout from the chat group by typing "logout".
	8. Client can get the active clients list by typing "activeclients".
	9. As soon as the client logoff from the group chat, all the clients will get notified that user has left.
	10. After the client left the group chat, the name of that client will be removed from "clientNames" vector and output stream of that client is removed from "printWriters" Set and corresponding active streams and socket of the client will be closed. 

Client Class

	 GUI based client class. This class reads from keyboard and pass it to the server.