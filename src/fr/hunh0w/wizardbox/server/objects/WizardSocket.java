package fr.hunh0w.wizardbox.server.objects;

import java.io.InputStream;
import java.net.Socket;

public class WizardSocket {

    private Socket sock;

    public WizardSocket(Socket sock) {
        this.sock = sock;
    }

    public synchronized InputStream getIN() {
        InputStream in = null;
        try { in = sock.getInputStream();
        }catch(Exception e) {}
        return in;
    }

    public Socket getSocket() {
        return sock;
    }

    public void close() {
        try { sock.close();
        }catch(Exception e) {}
    }

    public String getIPv4() {
        return sock.getInetAddress().getHostAddress();
    }

    public String getHostname() {
        return sock.getInetAddress().getHostName();
    }


}
