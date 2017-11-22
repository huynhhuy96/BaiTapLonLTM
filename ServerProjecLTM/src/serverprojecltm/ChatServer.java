
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class ChatServer {

    private static final int PORT = 9001;
    public static SourceDataLine audio_out;
    public static boolean calling = true;
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    //voice  
    public static AudioFormat getauAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeIntbits = 16;
        int channel = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeIntbits, channel, signed, bigEndian);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Server is running with PORT " + PORT);
        System.out.println("Server IP: " + "127.0.0.1");
        ServerSocket listener = new ServerSocket(PORT);
        //for voice     
        AudioFormat format = getauAudioFormat();
        DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info_out)) {
            System.out.println("Not Suport");
            System.exit(0);
        }

        ///////////////
        audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
        audio_out.open(format);
        audio_out.start();
        player_thread p = new player_thread();
        p.din = new DatagramSocket(PORT);
        p.audio_out = audio_out;
        p.start();
        System.out.println("Voice server ready!.");
        System.out.println("Chat server ready!.");
        ///     
        //LOGIN
     
       
        //
        try {
            while (true) {
              
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public  static String notify = "";
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
           
            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

             String check = in.readLine();
             if(check.startsWith("USER"))
             {
                ///Login
                while (true) {
                    String usr = in.readLine();
                    String pass = in.readLine();
                    File fl = new File("D:\\users.txt");
                    FileInputStream fi = new FileInputStream(fl);
                    DataInputStream di = new DataInputStream(fi);
                    String str = null;
                    boolean dec = false;
                    str = di.readLine();
                    while (true) {
                        if (str == null) {
                            break;
                        }
                        String pas = di.readLine();
                        if (str.equals(usr) && pas.equals(pass)) {
                            dec = true;
                            break;
                        }
                        str = di.readLine();
                    }
                    if (dec) {
                        out.println("Login successful");
                        break;
                    } else {
                        out.println("Login failed");
                    }

                }
             }//xac nhan ten va bat dau thu hien chat.
             
             if(check.startsWith("NAME"))
             {
                 
                   while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                out.println("NAMEACCEPTED");
                writers.add(out);
               
                
                while (true) {
                    String input = in.readLine();
                    System.out.println("MESSAGE " + name + ":" + input);
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                       
                           writer.println("MESSAGE " + name + ":" + input);
                                              
                    }
                }
             }

              
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
   
    
}
