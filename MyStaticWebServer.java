/////////////////////////////////////////////////////////////
//
// Carson Uecker-Herman
//
/////////////////////////////////////////////////////////////
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
// This is mostly a copy of the sample code.  Use what you want, delete the rest.
// Note: This code is not well organized. It should be broken into smaller methods.

public class MyStaticWebServer {
	
	private static File f;
	private static FileInputStream fis;
	//private static Socket socket;
    // You can use this to map extensions to MIME types.  Feel free to add other mappings as desired.
    private static final HashMap<String, String> extensions = new HashMap<String, String>();
    static {
        extensions.put("jpeg", "image/jpeg");
        extensions.put("jpg", "image/jpeg");
        extensions.put("png", "image/png");
        extensions.put("gif", "image/gif");
        extensions.put("html", "text/html");
        extensions.put("htm", "text/html");
        extensions.put("pdf", "application/pdf");
        extensions.put("ico", "image/vnd.microsoft.icon");
    }

    // This method demonstrates how to isolate a file's extension
    public static String getExtension(String filename) {
        String type = "text/plain";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            String extension = filename.substring(i + 1);
            if (extensions.containsKey(extension)) {
                type = extensions.get(extension);
            }
        }
        System.out.println("Extension:" + type);
        return type;
    }
    
    public static boolean isValidExtension(String filename) {
    	int i = filename.lastIndexOf('.');
        if (i > 0) {
            String extension = filename.substring(i + 1);
            return extensions.containsKey(extension);
        }
        
        return true;
    }


  public static void send404(PrintStream out, String toPrint) {
    out.println("HTTP/1.1 404 Not Found");
    out.println("Content-Type: text/html");
    out.println("Content-Length: " + toPrint.length());
    out.println("Connection: close");
    out.println("");
    out.println(toPrint);
  }
  
  public static void send400(PrintStream out, String toPrint) {
	    out.println("HTTP/1.1 404 Not Found");
	    out.println("Content-Type: text/html");
	    out.println("Content-Length: " + toPrint.length());
	    out.println("Connection: close");
	    out.println("");
	    out.println(toPrint);
	  }
  
  public static void dirList(PrintStream out, String toPrint ) {
	  	out.println("HTTP/1.1 200 OK");
	    out.println("Content-Type: text/html");
	    out.println("Content-Length: " + toPrint.length());
	    out.println("Connection: keep-alive");
	    out.println("");
	    out.println(toPrint);
  }
  

    // zk Please remove dead code from final submissions.  
  public static void send200(PrintStream out, File file) {
	  
	  /*

      try {
        fis = new FileInputStream(file);
      } catch (Exception e) {
        String toPrint = "<html><body>Problem opening/reading \"" + file.getName() + "\"</body></html>";
        send404(out, toPrint);
        socket.close();
        //break;
      }
      
      // Respond
      out.println("HTTP/1.1 200 OK");
      out.println(getExtension(file.toString()));
      out.println("Content-Length: " + file.length());
      out.println("Connection: close");
      out.println("");

      // read data from the file and send it to the client.
      byte[] buffer = new byte[8192];
      int read = fis.read(buffer);
      while (read != -1) {
        out.write(buffer, 0, read);
        read = fis.read(buffer);
      }
      fis.close();
	*/  
  }


  public static void main(String[] args) throws IOException {

    // Create a socket that listens on port 8534.
    int port = 8534;
    ServerSocket serverSocket = new ServerSocket(port);

    // Handle multiple requests sequentially
    while (true) {
      System.out.println("\n\nAwaiting new connection on port " + port);

      // Return a Socket object for the next connection in the queue
      Socket socket = serverSocket.accept();

      // Created a BufferedReader that can read from the socket
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Create a PrintStream than can write to the socket
      // Passing "true" as the second parameter causes each write to be followed by a
      // flush.
      PrintStream out = new PrintStream(socket.getOutputStream(), true);

      // Read the main command.
      String command = input.readLine();
      System.out.println("Command Received: =>" + command + "<=");

      // Read the request headers
      System.out.println("\nRequest Headers:");
      String headerLine = input.readLine();
      while (headerLine != null && !headerLine.isEmpty()) {
        System.out.println("\t" + headerLine);
        headerLine = input.readLine();
      }

      
      if(command == null) {
    	  out.flush();
    	  continue;
      }
      
      // split the command by spaces.
      String[] parts = command.split("\\s+");
      System.out.printf("Command; %s; path %s; protocol %s\n", parts[0], parts[1], parts[2]);

      String filename = parts[1];

      System.out.println("Filename: " + filename);
      // If the path begins with "/", remove the "/".
      if (filename.startsWith("/")) {
        filename = filename.substring(1);
      }

      f = new File(filename);

      System.out.println("File: " + f.getName());
      // send 404 if file doesn't exist, or is not readable.
     
      if (!f.exists()) {
        System.out.println(filename + " not found.  Returning 404.");
        String toPrint = "<html><body>Problem finding/reading \"" + filename + "\"</body></html>";
        send404(out, toPrint);
        socket.close();
        continue;
      }
      
      if(!isValidExtension(f.toString())) {
    	  try {
              fis = new FileInputStream(f);
            } catch (Exception e) {
              String toPrint = "<html><body>Problem opening/reading \"" + filename + "\"</body></html>";
              send404(out, toPrint);
              socket.close();
              break;
            }
            
            // Respond
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println("Content-Length: " + f.length());
            out.println("Connection: close");
            out.println("");

            // read data from the file and send it to the client.
            byte[] buffer = new byte[8192];
            int read = fis.read(buffer);
            while (read != -1) {
              out.write(buffer, 0, read);
              read = fis.read(buffer);
            }
            fis.close();
            
        	
        	//send200(out, dirIndex);
          	System.out.print("found");
          	socket.close();
          	continue;
          
      }
      
      if (f.isDirectory()){
    	  System.out.println(f.getName() + " is a directory");
 
    	  
          File dirIndex = new File(f.getName()+"/index.html");
          System.out.println("index location: " + dirIndex.getCanonicalPath());
          System.out.print("Searching for index.....");
          if(dirIndex.exists() && dirIndex.canRead() && dirIndex.isFile()){
          //	f = dirIndex;

        	
        	  
            try {
              fis = new FileInputStream(dirIndex);
            } catch (Exception e) {
              String toPrint = "<html><body>Problem opening/reading \"" + filename + "\"</body></html>";
              send404(out, toPrint);
              socket.close();
              break;
            }
            
            // Respond
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + getExtension(dirIndex.toString()));
            out.println("Content-Length: " + dirIndex.length());
            out.println("Connection: close");
            out.println("");

            // read data from the file and send it to the client.
            byte[] buffer = new byte[8192];
            int read = fis.read(buffer);
            while (read != -1) {
              out.write(buffer, 0, read);
              read = fis.read(buffer);
            }
            fis.close();
            
        	
        	//send200(out, dirIndex);
          	System.out.print("found");
          	socket.close();
          	continue;
          }
          
          else if(dirIndex.exists() && !dirIndex.canRead() ) {
        	  
        	  String toPrint = "<html><body><h1>Contents of " + f.getName() + "/</h1>";
        	  for (File file : f.listFiles()) {
        	
        		if(file.isDirectory()) {
        		
        			toPrint += "<a style='display:block;' href=" + file.getName()+ "/>"+file.getName()+"/</a>";
        		}
        		else {
        			toPrint += "<a style='display:block;' href=" + file.getName()+ ">"+file.getName()+"</a>";
        		}
        	  }
        	  toPrint += "</body></html>";
    
        	dirList(out, toPrint);
          	socket.close();
          	continue;
        	  
          }
          // create list of files in directory
          else {
        	  System.out.print("not found");
        	  System.out.println("\nShow file list....");
            	
        	//  File[] filesList = f.listFiles();
        	  String toPrint = "<html><body><h1>Contents of " + f.getName() + "/</h1>";
        	  for (File file : f.listFiles()) {
        	
        		if(file.isDirectory()) {
        		
        			toPrint += "<a style='display:block;' href=" + file.getName()+ "/>"+file.getName()+"/</a>";
        		}
        		else {
        			toPrint += "<a style='display:block;' href=" + file.getName()+ ">"+file.getName()+"</a>";
        		}
        	  }
        	  toPrint += "</body></html>";
    
        	dirList(out, toPrint);
          	socket.close();
          	continue;
          	
          }
      }
     
      
      if(!parts[0].contains("GET") || !isValidExtension(f.toString())) {
    	  System.out.println(">"+parts[0]+"<");
    	  System.out.println(command + " is not recognized. Returning 400.");
    	  String toPrint = "<html><body>" + command + " is not recognized. </body></html>";
    	  send400(out, toPrint);
    	  socket.close();
    	  continue;
      }

      

      
      try {
        fis = new FileInputStream(f);
      } catch (Exception e) {
        String toPrint = "<html><body>Problem opening/reading \"" + filename + "\"</body></html>";
        send404(out, toPrint);
        socket.close();
        break;
      }


        

      // Respond
      out.println("HTTP/1.1 200 OK");
      out.println("Content-type: " + getExtension(f.toString()));
      out.println("Content-Length: " + f.length());
      out.println("Connection: close");
      out.println("");

      // read data from the file and send it to the client.
      byte[] buffer = new byte[8192];
      int read = fis.read(buffer);
      while (read != -1) {
        out.write(buffer, 0, read);
        read = fis.read(buffer);
      }
      fis.close();
      
      
      
      //send200(out, f);

      socket.close();

    } // end while(true)

    serverSocket.close();

    // When the connection ends, so does this program.
  }



}