/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicastjava;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author ASUS
 */
public class MultiCastJava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            MulticastSocket ms = new MulticastSocket(111);
            InetAddress ia = InetAddress.getByName("234.5.6.7");
            ms.joinGroup(ia);
            
            while (true) {
                byte[] data = new byte[1234];
                DatagramPacket dp = new DatagramPacket(data, data.length);
                ms.receive(dp);
                String pesan = new String(dp.getData()).trim();
                        System.out.print("Pesan Anda : " + pesan);
                
            }
            
        } catch (Exception e) {
        }
    }
    
}
