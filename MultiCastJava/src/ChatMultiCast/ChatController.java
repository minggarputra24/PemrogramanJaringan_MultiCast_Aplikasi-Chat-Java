package ChatMultiCast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author user
 */
public class ChatController {
    private GUIChat view;
    private MulticastSocket sendMulticast = null, listenMulticast = null;
    private InetAddress group;
    private InetAddress broadcast;
    int port = 0;
    String msg;
    private Thread t;

    public ChatController(GUIChat view) {
        this.view = view;
        this.view.getConnect().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connect();
                } catch (IOException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        this.view.getDisconnect().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t.stop();
                try {
                    sendMulticast.leaveGroup(group);
                    listenMulticast.leaveGroup(group);

                } catch (IOException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
                sendMulticast.close();
                listenMulticast.close();
            }
        });
        this.view.getKirimPesan().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendChat();
                } catch (IOException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        this.view.getKirimFile().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
    }

    void Connect() throws UnknownHostException, IOException {
        t = new Thread(new ListenChat());
        try {
            group = InetAddress.getByName(this.view.getIP().getText());
            port = Integer.parseInt(this.view.getPort().getText());
            broadcast = InetAddress.getByName("10.10.10.254");

            sendMulticast = new MulticastSocket(port);
            sendMulticast.joinGroup(group);
            sendMulticast.setBroadcast(true);
            sendMulticast.setTimeToLive(5);

            listenMulticast = new MulticastSocket(port);
            listenMulticast.joinGroup(group);
            listenMulticast.setTimeToLive(5);

            t.start();
            this.view.getChatBox().append("Listening...\n");
        } catch (UnknownHostException ux) {
           
        }
    }

    private class ListenChat implements Runnable {
        @Override
        public void run() {
            try {
                listenChat();
            } catch (IOException ex) {
                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void listenChat() throws IOException {
        byte[] buffer = new byte[8192];
        while (true) {
            DatagramPacket ReceivePacket = new DatagramPacket(buffer, buffer.length);
            ReceivePacket.setLength(ReceivePacket.getLength());
            listenMulticast.receive(ReceivePacket);
            String IPSender = ReceivePacket.getAddress().getHostAddress();
            String s = new String(ReceivePacket.getData(), 0, ReceivePacket.getLength());
            this.view.getChatBox().append(IPSender + " -> " + s + "\n");
        }
    }

    void sendChat() throws IOException {
        msg = this.view.getPesan().getText();
        byte[] msgByte = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(msgByte, msgByte.length, broadcast, port);
        sendMulticast.send(sendPacket);
        msg = "";
        this.view.getPesan().setText("");
        if (this.view.getPesan().getText().equalsIgnoreCase("exit")) {
            sendMulticast.leaveGroup(group);
            sendMulticast.close();
            System.exit(1);
        }
    }

    void sendFile() {
        JFileChooser loadFile = view.getLoadFile();
        if (JFileChooser.APPROVE_OPTION == loadFile.showOpenDialog(view)) {
            BufferedInputStream reader = null;
            try {
                reader = new BufferedInputStream(new FileInputStream(loadFile.getSelectedFile()));
                int temp = 0;
                List<Integer> list = new ArrayList<>();
                while ((temp = reader.read()) != -1) {
                    list.add(temp);
                }
                if (!list.isEmpty()) {
                    byte[] readFile = new byte[list.size()];
                    int i = 0;
                    for (Integer integer : list) {
                        readFile[i] = integer.byteValue();
                        i++;
                    }
                    String isiFile = new String(readFile);
                    String sendedMessage = "isi file: " + isiFile;
                    byte[] sendFile = sendedMessage.getBytes();
                    JOptionPane.showMessageDialog(view, "File berhasil dikirim!!!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    DatagramPacket sendPacket = new DatagramPacket(sendFile, sendFile.length, broadcast, port);
                    sendMulticast.send(sendPacket);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
