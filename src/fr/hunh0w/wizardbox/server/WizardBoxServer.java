package fr.hunh0w.wizardbox.server;

import fr.hunh0w.wizardbox.server.managers.TokenManager;
import fr.hunh0w.wizardbox.server.objects.WizardCTF;
import fr.hunh0w.wizardbox.server.objects.WizardWebSocket;
import fr.hunh0w.wizardbox.server.utils.VarUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WizardBoxServer extends WebSocketServer {

    private boolean run = false;

    public WizardBoxServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onClose(WebSocket ws, int arg1, String arg2, boolean arg3) {
        System.out.println("[WS Server] Un client s'est déconnecté");
        WizardWebSocket.removeWebsock(ws);
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {
        run = false;
        ex.printStackTrace();
        WizardWebSocket.removeWebsock(ws);
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        System.out.println("[WS Server] MESSAGE : " + message);
        String request = message.split("::")[0];
        String data = message.replaceFirst(request+"::", "");
        if(request.equalsIgnoreCase("TOKEN")){
            if(TokenManager.tokenExists(data)){
                WizardWebSocket wbsock = new WizardWebSocket(ws);
                wbsock.setAuthenticated(true);
                TokenManager.removeToken(data);
                ws.send("AUTH_SUCCESS");
            }else ws.send("AUTH_FAILED");
        }else if(request.equalsIgnoreCase("CTF")){
            WizardWebSocket wbsock = WizardWebSocket.getWizardWebSock(ws);
            if(wbsock == null || !wbsock.isAuthenticated()){
                ws.send("AUTH_REQUIRED");
                return;
            }
            int ctfid = VarUtils.getInt(data);
            WizardCTF ctf = WizardCTF.getCTF(ctfid);
            if(ctf == null){
                ws.send("INVALID_CTFID");
                return;
            }
            wbsock.setCTF(ctf);
            ctf.linkCTF(ws);
            ws.send("CTF_LINKED::"+ctf.getName());
        }else if(request.equalsIgnoreCase("CMD")){
            WizardWebSocket wbsock = WizardWebSocket.getWizardWebSock(ws);
            if(wbsock == null || !wbsock.isAuthenticated()){
                ws.send("AUTH_REQUIRED");
                return;
            }
            if(wbsock.getCTF() == null){
                ws.send("NO_CTF");
                return;
            }
            System.out.println("Executing..");
            data = new String(Base64.getDecoder().decode(data));
            wbsock.getCTF().exec(data, ws);
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake handshake) {
        if(this.getConnections().size() > 50){
            ws.close();
            System.out.println("[WS Server] Nombre de clients > 50 : Rejet.");
            return;
        }
        System.out.println("[WS Server] Un nouveau client s'est connecté ! : " + handshake.getResourceDescriptor());
    }

    public boolean isStarted() {
        return this.run;
    }

    @Override
    public void onStart() {
        run = true;
        System.out.println("[WS Server] Le serveur a bien démarré !");
        setConnectionLostTimeout(0);
    }
}