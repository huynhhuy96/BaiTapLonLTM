
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
        File f = new File("src/document/" + filename);
        if (din.readUTF().equals("READY")) {
            FileInputStream fin = new FileInputStream(f);
            int ch;
            do {
                ch = fin.read();
                dout.writeUTF(String.valueOf(ch));
            } while (ch != -1);
            fin.close();
            dout.writeUTF("Download Complete");

        }

    }

    void ReceiveFile() throws Exception {
        String filename = din.readUTF();
        File f = new File("src/document/" + filename);
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
            System.out.println("Đã nhận file " + filename);
        }

    }

    void sendList() throws Exception {
        File dir = new File("src/document");
        String[] paths = dir.list();
        String stfilename = "";
        for (String path : paths) {
            //  System.out.println(path);
            stfilename += path + " ";
        }
        dout.writeUTF(stfilename);

    }

    @Override
    public void run() {
        System.out.println("Waiting for Command ...");
        while (true) {
            try {
                // System.out.println("Waiting for Command ...");
                String Command = din.readUTF();
                if (Command.compareTo("GET") == 0) {
                    System.out.println("\tGET Command Received ...");
                    SendFile();
                    continue;
                } else if (Command.compareTo("SEND") == 0) {
                    System.out.println("\tSEND Command Receiced ...");
                    ReceiveFile();
                    continue;
                } else if (Command.compareTo("GETLIST") == 0) {
                    System.out.println("\tSEND LIST ...");
                    sendList();
                    continue;
                }
            } catch (Exception ex) {
            }
        }
    }
}
