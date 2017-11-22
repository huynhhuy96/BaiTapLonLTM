

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.EditorKit;


public class chat_thread extends Thread{
    public String serverAddress;
    public static Socket socket;
    public String nickname;
    public String textsend;
    public static BufferedReader in;
    public static PrintWriter out;
    public static JTextPane jTextPane1 = new JTextPane();
    public static JTextArea txt_msg = new JTextArea();
    public static JTextArea jTextArea1 = new JTextArea();
    public static  JTextArea txt_notify = new JTextArea();
    @Override
    
    
    public void  run()
    {
        try {

           String serverAddress = "localhost";
           try {
                socket = new Socket(serverAddress, 9001);
            } catch (IOException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("NAME");
            while (true) {
                String line = in.readLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(nickname);

                } else if (line.startsWith("NAMEACCEPTED")) {

                    out.println("notify" + " "+nickname + " \"Đã tham gia cuộc trò chuyện\"");
                    txt_msg.setEditable(true);

                }
                else if (line.startsWith("MESSAGE")) {
                 /*   EditorKit kit = jTextPane1.getEditorKitForContentType("text/html");
                    jTextPane1.setEditorKit(kit);
                    if (line.substring(8).startsWith(nickname)) {
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:right;color: #3399FF;font-size:110%;\"><span style=\"background-color:#99FF33\">" + " " + line.substring(8 + nickname.length() + 1) + " " + "</span></p>");
                    } else {
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:left;color: #FF0000; font-size:110%;\"><span style=\"background-color:#99FFFF\">" + " " + line.substring(8) + " " + "</span></p>");
                    }
                    jTextPane1.setText(jTextArea1.getText());
                }*/
                                      
                    EditorKit kit = jTextPane1.getEditorKitForContentType("text/html");
                    jTextPane1.setEditorKit(kit);
                    //Notify from system
                    if(line.substring(8 + 8 + 1).startsWith("notify")){
                     txt_notify.append(line.substring(8 + 8 + 1+6)+"\n");
                    }
                    else
                    {
                         //if send-name = everbody background = violet, color white
                    if(line.substring(8 + 8 + 1).startsWith("Everbody")){
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:left;color: #FFFFFF; font-size:110%;\"><span style=\"background-color:#FF33FF\">" + " " + line.substring(8,17) + line.substring(25,line.length()) + " " + "</span></p>");
                    }
                    //star with your name && name-send not same your name: color = blue, background = green
                    if (line.substring(8).startsWith(nickname)&&!line.substring(8 + 8 + 1).startsWith(nickname)) {
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:right;color: #3399FF;font-size:110%;\"><span style=\"background-color:#99FF33\">" +"« Send to "+line.substring(17, 26) +" » " + line.substring(8 + nickname.length() + 1 + 8) + " " + "</span></p>");
                    }
                    //name-recive same your name background = blue,color = red
                    if(line.substring(8 + 8 + 1).startsWith(nickname)){
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:left;color: #FF0000; font-size:110%;\"><span style=\"background-color:#99FFFF\">" + " " + line.substring(8,17) + line.substring(25,line.length()) + " " + "</span></p>");
                    }
                    
                    jTextPane1.setText(jTextArea1.getText());
                    }
                   
                }

            }           
            
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendText(String text)
    {
        out.println(text);
        txt_msg.setText("");
    }
    
   
}
