package fr.hunh0w.wizardbox.server.objects;

import fr.hunh0w.wizardbox.Main;
import fr.hunh0w.wizardbox.server.managers.CryptoManager;
import fr.hunh0w.wizardbox.server.managers.TokenManager;
import fr.hunh0w.wizardbox.server.protocol.WizardMessage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WizardLinkThread implements Runnable {

    private WizardSocket sock;

    public WizardLinkThread(WizardSocket sock){
        this.sock = sock;
    }

    @Override
    public void run() {
        System.out.println("[WB-Tunnel] Connection established : "+sock.getIPv4());
        while(true){
            String message = getMessage(sock);
            if(message == null){
                close();
                return;
            }
            System.out.println("[WB-Tunnel] "+sock.getIPv4()+" -> "+message);
            String request = message.split("::")[0];
            String data = message.replaceFirst(request+"::", "");
            if(request.equalsIgnoreCase("TOKEN")){
                TokenManager.addToken(data);
            }
        }
    }

    public void close(){
        System.out.println("[WB-Tunnel] "+sock.getIPv4()+" Closed.");
        sock.close();
        while(Main.clients.contains(this))
            Main.clients.remove(this);
    }

    private static String getMessage(WizardSocket sock){
        try {
            ObjectInputStream os = new ObjectInputStream(sock.getSocket().getInputStream());
            Object packet = (Object) os.readObject();
            if(packet instanceof WizardMessage) {
                WizardMessage wzmess = (WizardMessage) packet;
                return new String(CryptoManager.decrypt(wzmess.getData()));
            }
            System.out.println("[WB-Tunnel] Objet inconnu envoyé par : "+sock.getIPv4());
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean send(WizardSocket sock, String message){
        try{
            WizardMessage wzmess = new WizardMessage(CryptoManager.encrypt(message.getBytes(StandardCharsets.UTF_8)));
            ObjectOutputStream oos_sock = new ObjectOutputStream(sock.getSocket().getOutputStream());
            oos_sock.writeObject(wzmess);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

}
