
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Love
 */
public class FTP_thread extends Thread {

    DataInputStream din;
    DataOutputStream dout;

    public void run() {
        try {
            int port = 9002;
            ServerSocket soc = new ServerSocket(port);
            System.out.println("FTP Server Started on Port Number " + port);
            while (true) {
                System.out.println("Waiting for Connection ...");
                transferfile2 t = new transferfile2(soc.accept());

            }
        } catch (IOException ex) {
            Logger.getLogger(FTP_thread.class.getName()).log(Level.SEVERE, null, ex);
        }

    } 

}
