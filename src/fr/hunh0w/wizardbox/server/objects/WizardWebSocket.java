package fr.hunh0w.wizardbox.server.objects;

import org.java_websocket.WebSocket;

import java.util.ArrayList;

public class WizardWebSocket {

    private static ArrayList<WizardWebSocket> websocks = new ArrayList<>();

    private WebSocket sock;
    private boolean authenticated = false;
    private WizardCTF CTF;

    public WizardWebSocket(WebSocket sock) {
        this.sock = sock;
        websocks.add(this);
    }

    public WebSocket getWebSocket() {
        return sock;
    }

    public static ArrayList<WizardWebSocket> getSocks(){
        return websocks;
    }

    public static WizardWebSocket getWizardWebSock(WebSocket ws){
        for(WizardWebSocket wbsock : websocks){
            if(wbsock.getWebSocket().equals(ws)) return wbsock;
        }
        return null;
    }

    public static void removeWebsock(WebSocket ws){
        ArrayList<WizardWebSocket> list = new ArrayList<>();
        for(WizardWebSocket wws : websocks){
            if(wws.getWebSocket().equals(ws)) list.add(wws);
        }
        for(WizardWebSocket wws : list){
            while(websocks.contains(wws))
                websocks.remove(wws);
        }
    }

    public void setAuthenticated(boolean bol){
        this.authenticated = bol;
    }

    public boolean isAuthenticated(){
        return this.authenticated;
    }

    public WizardCTF getCTF() {
        return CTF;
    }

    public void setCTF(WizardCTF CTF) {
        this.CTF = CTF;
    }
}
