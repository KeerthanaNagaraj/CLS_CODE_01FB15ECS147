/*Program for the CLient interface- implements the Client Java Code to run the Client Server and provide the Key-Value Based Functionality for the User*/  

import java.io.*;
import java.net.*;
import java.lang.*;

public class client {
    public static void main(String[] args) throws Exception {
    
    	int numServers = Integer.parseInt(args[2]);
    	int numElements = (26/numServers) + 1;
    	String[] iptable=new String[numServers];
        boolean secondRun=true;
        int key=0;
        //Wrong user input Error
        if (args.length != 3) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
	// Get the details of the Master Node and Connect the Client to the Master
        String hostName = args[0];
        String Master=hostName;
        int portNumber = Integer.parseInt(args[1]);
        try (
            Socket cSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
        ) 
        {             
        	//Get the MetaData about Servers 
		String fromServer= "";
		String fromUser = "getMETA";
		out.println(fromUser);
                int i=0;
		while((fromServer=in.readLine())!="\n" && i<numServers){
			String[] x=fromServer.split(" ");
			iptable[i]=x[1];
			i++;
		}
              		System.out.println("Meta Table cached by Client :- ");
                   	for(i=0;i<numServers;i++){
                   		System.out.println("Server " + (i+1) + " : " + iptable[i]);
                   	}
                   	System.out.println("\n\n\n CLIENT STARTED ");
                   		
                cSocket.close();
             
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +hostName);
            System.exit(1);
        }
        //Procedure to perform the PUT and GET requests from the User
        BufferedReader stdIn =new BufferedReader(new InputStreamReader(System.in));
        String fromServer="";
        String fromUser="";
	while(true){
		if(secondRun){
			fromUser = stdIn.readLine();
			if (fromUser.equals("exit"))
			{
				System.out.println("Server: " + "disconnected");
				System.exit(0);
			}
			//Get the Server to which Client has to connect based on User Input
			key = ((int)fromUser.split(" ")[1].charAt(0)-(int)'a')/numElements;
	                hostName=iptable[key];	  			                   
		}
		secondRun=true;
		//Connect to the corresponding host based on the key evaluated
		System.out.println("Establishing connection to Server "+ key + " (" + hostName + ")");
		try (
			Socket cSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
			) {
				if (fromUser != null) {
					System.out.println("Client: " + fromUser);
					out.println(fromUser);
				}
				while ((fromServer = in.readLine()) != null) {
					System.out.println("Server: " + fromServer);
					break;
				}
				System.out.println("\n\n");
			cSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		        System.exit(1);
		//Procedure to handle the Server Failure
	        } catch (IOException e) {
	            	System.err.println("Couldn't get I/O for the connection to (SERVER FAILURE) : " + hostName);
		 	try (Socket cSocket = new Socket(Master, portNumber);
		            PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
		            BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));) 
			 {
		 		secondRun=false;
		                String R = Integer.toString(key);
	        	        out.println(R);
	                	hostName= in.readLine();
		                System.out.println("REPLICA FOR THE FAILED SERVER " + key + " : " + hostName);
		                iptable[key]=hostName;
		           	cSocket.close();
		         }catch (UnknownHostException f) {
	        	    System.err.println("Don't know about host " + hostName);
	            	    System.exit(1);
		        } catch (IOException f) {
			            System.err.println("Couldn't get I/O for the connection to " +hostName);
		   	}
	   	 }
	   }
    }
}
