

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

                    out.println("\"Đã tham gia cuộc trò chuyện\"");
                    txt_msg.setEditable(true);

                }
                else if (line.startsWith("MESSAGE")) {
                    EditorKit kit = jTextPane1.getEditorKitForContentType("text/html");
                    jTextPane1.setEditorKit(kit);
                    if (line.substring(8).startsWith(nickname)) {
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:right;color: #3399FF;font-size:110%;\"><span style=\"background-color:#99FF33\">" + "<i>" + " " + line.substring(8 + nickname.length() + 1) + " " + "</span></p>");
                    } else {
                        jTextArea1.append("<p style=\"font-family:Helvetica;text-align:left;color: #FF0000; font-size:110%;\"><span style=\"background-color:#99FFFF\">" + "<i>" + " " + line.substring(8) + " " + "</span></p>");
                    }
                    jTextPane1.setText(jTextArea1.getText());
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
