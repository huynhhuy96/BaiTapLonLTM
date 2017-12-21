
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

class transferfile2 extends Thread {

    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;

    transferfile2(Socket soc) {
        try {
            ClientSoc = soc;
            din = new DataInputStream(ClientSoc.getInputStream());
            dout = new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();

        } catch (Exception ex) {
        }
    }

    void SendFile() throws Exception {
        String filename = din.readUTF();
        File f = new File("E:" + filename);
        if (!f.exists()) {
            dout.writeUTF("File Not Found");
            return;
        } else {
            dout.writeUTF("READY");
            FileInputStream fin = new FileInputStream(f);
            int ch;
            do {
                ch = fin.read();
                dout.writeUTF(String.valueOf(ch));
            } while (ch != -1);
            fin.close();
            dout.writeUTF("File Receive Successfully");
        }
    }

    void ReceiveFile() throws Exception {
        String filename = din.readUTF();
        File f = new File("E:" + filename);
        FileOutputStream fout = new FileOutputStream(f);
        int ch;
        String temp;
        if (din.readUTF().equals("SEND")) {
            do {
                temp = din.readUTF();
                ch = Integer.parseInt(temp);
                if (ch != -1) {
                    fout.write(ch);
                }
            } while (ch != -1);
            fout.close();
            dout.writeUTF("File Send Successfully");

        }

    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for Command ...");
                String Command = din.readUTF();
                if (Command.compareTo("GET") == 0) {
                    System.out.println("\tGET Command Received ...");
                    SendFile();
                    continue;
                } else if (Command.compareTo("SEND") == 0) {
                    System.out.println("\tSEND Command Receiced ...");
                    ReceiveFile();
                    continue;
                } else if (Command.compareTo("DISCONNECT") == 0) {
                    System.out.println("\tDisconnect Command Received ...");
                    System.exit(1);
                }
            } catch (Exception ex) {
            }
        }
    }
}
