/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicastjava;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 *
 * @author ASUS
 */
public class Client {
    public static void main(String[] args) {
        try {
            MulticastSocket ms = new MulticastSocket(1111);
            InetAddress ia = InetAddress.getByName("234.5.6.7");
            ms.joinGroup(ia);
            
            Scanner sc = new Scanner(System.in);
            String pesan = sc.nextLine();
            byte[] b = pesan.getBytes();
            DatagramPacket dp = new DatagramPacket(b, 0, b.length, ia, 1111);
        } catch (Exception e) {
        }
    }
}
