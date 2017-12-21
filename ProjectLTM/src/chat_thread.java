

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
    public static JTextField txt_msg = new JTextField();
    public static JTextArea jTextArea1 = new JTextArea();
    public static  JTextArea txt_notify = new JTextArea();
    public static JComboBox comboOnline = new JComboBox();
    @Override
    
    
    public void  run()
    {
        try {

           String serverAddress = "localhost";
          //  String serverAddress = "172.16.0.91";
           try {
                socket = new Socket(serverAddress, 9001);
            } catch (IOException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(),"utf8"));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("NAME");
      
            while (true) {
                String line = in.readLine();
                //line.startsWith("USER")
                if(line.startsWith("USER"))
                {
                    comboOnline.removeAllItems();
                    for(int i=4;i<=line.length()-8;i+=8)
                    {
                        comboOnline.addItem(line.substring(i, i+8));
                    }
                }
                if (line.startsWith("SUBMITNAME")) {
                    out.println(nickname);

                } else if (line.startsWith("NAMEACCEPTED")) {

                    out.println("notify" + " > "+nickname + " is online...");
                    
                    txt_msg.setEditable(true);

                }
                else if (line.startsWith("MESSAGE")) {
                                                       
                    EditorKit kit = jTextPane1.getEditorKitForContentType("text/html");
                    jTextPane1.setEditorKit(kit);
                    //Notify from system
                    if(line.substring(8 + 8 + 1).startsWith("notify")){
                     txt_notify.append(line.substring(8 + 8 + 1+6)+"\n");
                    // comboOnline.addItem(line.substring(8 + 8 + 1+6,31));
                    }
                    else
                    {
                         //if send-name = everbody background = violet, color white
                    if(line.substring(8 + 8 + 1).startsWith("All User")){
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
