import java.util.*;
import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;

public class serverProtocol {
    private static final int WAITING = 0;
    private static final int SENTKNOCKKNOCK = 1;
    StringBuilder sb = new StringBuilder("");
    private int state = SENTKNOCKKNOCK;
    JSONObject obj = new JSONObject();
    public String processInput(String theInput)throws Exception {
        String theOutput = null;
            String[] ch = theInput.split(" ");
            if(ch[0].equalsIgnoreCase("getMETA"))
            {
            	FileInputStream fis = new FileInputStream("META.txt");
          	BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
	    	String line="";
            	while((line = reader.readLine())!= null){
               		 String[] input = line.split(" ");
                	 String temp = input[0] + " " + input[1];
                	 sb.append(temp+"\n");
            	} 
            	theOutput = sb.toString();
            }
            
            else if(ch[0].equalsIgnoreCase("Replica"))
            {
            	FileInputStream fis = new FileInputStream("META.txt");
            	StringBuilder sb = new StringBuilder("");
          	BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
          	String line = reader.readLine();
            	while(line != null){
               		 String[] input = line.split(" ");
               		 String temp;
               		 if(input.length==2){
                	 temp = input[1]+" "+input[2];}
                	 else{temp = input[1]+" Master";}
                	 sb.append(temp+" ");
                	 line = reader.readLine();
            	} 
            	theOutput = sb.toString();
            }
            
            else if (ch[0].equalsIgnoreCase("put")) {
                String[] data = ch[1].split(",");
                obj.put(data[0],data[1]);
                theOutput = "OK";
             }
             else if(ch[0].equalsIgnoreCase("get"))
             {
                	StringWriter out = new StringWriter();
                	if(obj.get(ch[1]) != null){
	      			theOutput=(obj.get(ch[1])).toString();
	      		}
	      		else
	      			theOutput = "Data Doesn't Exists !!";
	      }
            
            
            else if(ch[0].equalsIgnoreCase("exit")) {
                theOutput = "exit";          
            }
        return theOutput;
    }
    }

