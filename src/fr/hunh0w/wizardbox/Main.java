package fr.hunh0w.wizardbox;

import fr.hunh0w.wizardbox.server.WizardBoxServer;
import fr.hunh0w.wizardbox.server.objects.WizardCTF;
import fr.hunh0w.wizardbox.server.objects.WizardLinkThread;
import fr.hunh0w.wizardbox.server.objects.WizardSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Main {

    public static WebSocketServer wss = null;
    public static ServerSocket server = null;

    public static ArrayList<WizardLinkThread> clients = new ArrayList<>();

    public static void main(String[] args) {
        wss = new WizardBoxServer(8080);
        wss.start();

        new WizardCTF(1, "/bin/bash", "TEST", "FLAG289");

        new Thread(() -> {
            try {
                server = new ServerSocket(3787, 0, InetAddress.getLoopbackAddress());
                while(true){
                    while(clients.size() > 20){
                        System.out.println("[WB-Tunnel] Trop de clients : Rejet des connexions");
                        Thread.sleep(2000);
                    }
                    WizardSocket sock = new WizardSocket(Main.server.accept());
                    WizardLinkThread wlt = new WizardLinkThread(sock);
                    clients.add(wlt);
                    new Thread(wlt).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
