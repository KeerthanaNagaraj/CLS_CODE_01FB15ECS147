/*Program for the Server interface- implements the Server Java Code to run the Server and provide the Result to User's queries from the ServerProtocol*/ 

import java.net.*;
import java.io.*;
import java.lang.*;
 
public class server {
    public static void main(String[] args) throws Exception {
         if (args.length != 2) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        String[] Replicas={"0","",""};
        String Replica="";
        String inputLine, outputLine;
 	serverProtocol ser = new serverProtocol();
        String ip = args[0];
        //
        if(args[1].equals("true")){
        try (
            ServerSocket serverSocket = new ServerSocket(6000,5,InetAddress.getByName(ip));
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
             System.out.println("Master Server");
             //Initiate conversation with client
   
            	inputLine = in.readLine();
                outputLine = ser.processInput(inputLine);
                out.println(outputLine);
           	serverSocket.close();
          	//get Replica Information for the Master to keep track of the mappings and handle failure  	
           	outputLine=ser.processInput("Replica");
           	String[] temp=outputLine.split(" ");
           	for(int i=0,j=1;i<=5;i++){
           		if(i==0 || i==1){continue;
           			}
           			
           		else{
           			Socket cSocket = new Socket(temp[i], 6000);
            			PrintWriter out2 = new PrintWriter(cSocket.getOutputStream(), true);
            			if(i%2==1){out2.println("");}
            			else{
           			Replicas[j]=temp[i+1];j++;
           			out2.println(temp[i+1]);}
           			cSocket.close();
           		}
           	}/*
           	Socket cSocket = new Socket(temp[5], 6000);
            	PrintWriter out2 = new PrintWriter(cSocket.getOutputStream(), true);
           	out2.println("");*/
           }
           	
            
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        }
        if(!(args[1].equals("true"))){
		ServerSocket serverSocket = new ServerSocket(6000,5,InetAddress.getByName(ip));
	        Socket clientSocket = serverSocket.accept();
	        BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
	        Replica=in.readLine();
	        if(Replica.equals("")){
	        	System.out.println("REPLICA SERVER");
	        }
	        else{
	        	System.out.println("MY REPLICA IS : " + Replica);
	        }
        	serverSocket.close();
    	}
    	
    	//Start the server to start listening to the Client Requests
    	
        while(true){
 		
       		 try (
	            ServerSocket serverSocket = new ServerSocket(6000,5,InetAddress.getByName(ip));
	            Socket clientSocket = serverSocket.accept();
	            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
	        ) {
			String tempInput="";
		        while ((inputLine = in.readLine()) != null) {
		            	tempInput=inputLine;
		            	//Return Replica in case of failure message by Client
		            	if(inputLine.charAt(0)!='p' && inputLine.charAt(0)!='P' && inputLine.charAt(0)!='G' && inputLine.charAt(0)!='g'){
            				outputLine=Replicas[Integer.parseInt(tempInput)];}
		            	else{
			                outputLine = ser.processInput(inputLine);}
			                out.println(outputLine);
			                if (outputLine.equals("Bye."))
				                    break;
	            	}
		         serverSocket.close();
		         
		         //Copy of the data input in server placed in Replica 
		         
		         if(!(args[1].equals("true")) && Replica!="" && (tempInput.charAt(0)=='p' || tempInput.charAt(0)=='P')){
	            	 	Socket cSocket = new Socket(Replica, 6000);
           	 		PrintWriter out2 = new PrintWriter(cSocket.getOutputStream(), true);
	            		 out2.println(tempInput);
	            		 while ((outputLine = in.readLine()) != null) {
			                if (outputLine.equals("exit"))
			                {
                   				 System.out.println("Server: " + "disconnected");
			                       System.exit(0);
			                   }cSocket.close();
			           }
		        }
	} catch (IOException e) {
          	System.out.println("ORIGINAL FAILED");
        }
    }
    }
}
