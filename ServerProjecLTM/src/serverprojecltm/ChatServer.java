
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        // System.out.println("Server IP: " + "172.20.10.5");
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
        public static String notify = "";
        public static String uonline = "All User";

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), "utf8"));
                out = new PrintWriter(socket.getOutputStream(), true);

                String check = in.readLine();
                //register 
                if (check.startsWith("REGISTER")) {
                    //String FILENAME = "d:/users.txt";
                    String FILENAME = "src/asset/users.txt";
                    BufferedWriter bw = null;
                    FileWriter fw = null;
                    try {
                        String user = in.readLine();
                        String pass = in.readLine();
                        File file = new File(FILENAME);

                        // kiểm tra nếu file chưa có thì tạo file mới
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        boolean dec = false;

                        FileInputStream fi = new FileInputStream(file);
                        DataInputStream di = new DataInputStream(fi);
                        String str = null;
                        str = di.readLine();

                        while (str != null) {
                            String pas = di.readLine();
                            if (str.equals(user)) {
                                dec = true;
                                break;
                            }
                            str = di.readLine();
                        }

                        if (dec) {
                            out.println("User Name is already,please use another name!");

                        } else {
                            fw = new FileWriter(file.getAbsoluteFile(), true);
                            bw = new BufferedWriter(fw);
                            System.out.println(user);
                            System.out.println(pass);
                            bw.write(user + "\r\n");
                            bw.write(MD5Library.md5(pass) + "\r\n");
                            out.println("REGISTER SUCCESS!");
                        }

                        // true = append file
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bw != null) {
                                bw.close();
                            }
                            if (fw != null) {
                                fw.close();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                if (check.startsWith("USER")) {
                    ///Login
                    while (true) {
                        String usr = in.readLine();
                        String pass = MD5Library.md5(in.readLine());
                        //File fl = new File("D:\\users.txt");
                        File fl = new File("src/asset/users.txt");
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
                            uonline += str;
                            out.println("Login successful.");

                            break;
                        } else {
                            out.println("USER or PASSWORD are not correct!");
                        }

                    }
                }//xac nhan ten va bat dau thu hien chat.

                if (check.startsWith("NAME")) {

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
                        if (input.startsWith("GETUSER")) {
                            out.println("USER" + uonline);
                            System.out.println(input);
                        } else {
                            if (input.substring(17).startsWith(" is offline...")) {
                                for (PrintWriter writer : writers) {

                                    writer.println("MESSAGE " + name + ":" + input);

                                }
                                for (int i = 8; i < uonline.length(); i += 8) {
                                    if (uonline.substring(i).startsWith(input.substring(9, 17))) {
                                        uonline = uonline.substring(0, i) + uonline.substring(i + 8);
                                    }
                                }
                            } else {
                                System.out.println("MESSAGE " + name + ":" + input);
                                if (input == null) {
                                    return;
                                }
                                for (PrintWriter writer : writers) {

                                    writer.println("MESSAGE " + name + ":" + input);

                                }
                            }

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
