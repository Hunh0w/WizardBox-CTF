package fr.hunh0w.wizardbox.server.objects;

import fr.hunh0w.wizardbox.Main;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

public class WizardCTF {

    public static ArrayList<WizardCTF> CTFs = new ArrayList<>();

    private ArrayList<WebSocket> linkeds = new ArrayList<>();
    private int id;
    private String start_command;
    private String name;
    private String flag;
    private Process proc;
    private Scanner scanner;
    private Thread threadProc;

    public WizardCTF(int id, String start_command, String name, String flag) {
        this.id = id;
        this.start_command = start_command;
        this.name = name;
        this.flag = flag;
        CTFs.add(this);
        try{
            regenProc();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getStart_command() {
        return start_command;
    }

    public String getName() {
        return name;
    }

    public String getFlag() {
        return flag;
    }

    public void remove(){
        while(CTFs.contains(this))
            CTFs.remove(this);
    }

    public static WizardCTF getCTF(int id){
        for(WizardCTF ctf : CTFs){
            if(ctf.getId() == id) return ctf;
        }
        return null;
    }


    public void linkCTF(WebSocket sock){
        if(!linkeds.contains(sock))
            linkeds.add(sock);
    }

    public void destroyLinkCTF(WebSocket sock){
        while(linkeds.contains(sock))
            linkeds.remove(sock);
    }

    public void sendToAll(String str){
        refreshClients();
        for(WebSocket wbsock : linkeds)
            wbsock.send(str);
    }

    public void refreshClients(){
        ArrayList<WebSocket> delete = new ArrayList<>();
        for(WebSocket wbsock : linkeds)
            if(wbsock.isClosed() || wbsock.isClosing()) delete.add(wbsock);
        for(WebSocket wbsock : delete){
            destroyLinkCTF(wbsock);
        }
    }

    private void regenProc(){
        if(threadProc != null) {threadProc.interrupt();}
        if(proc != null) proc.destroyForcibly();
        try{
            proc = new ProcessBuilder()
                    .command("/bin/bash")
                    .redirectErrorStream(true)
                    .directory(new File("/")).start();
            threadProc = new Thread(new Runnable() { // READER THREAD
                @Override
                public void run() {
                    try{
                        while(true){
                            scanner = new Scanner(proc.getInputStream());
                            while(scanner.hasNextLine()){
                                String line = scanner.nextLine();
                                if(line.toUpperCase().contains("EXITPROCESS2097426789297647246298")){
                                    line = line.replaceAll("EXITPROCESS2097426789297647246298", "");
                                    if(!line.isEmpty())
                                        sendToAll("CMDRESP::"+Base64.getEncoder().encodeToString(line.getBytes(StandardCharsets.UTF_8)));
                                    sendToAll("CMDFIN");
                                    break;
                                }
                                if(!line.isEmpty())
                                    sendToAll("CMDRESP::"+Base64.getEncoder().encodeToString(line.getBytes(StandardCharsets.UTF_8)));
                            }
                        }
                    }catch(Exception ex){

                    }
                }
            });
            threadProc.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


    public void exec(String cmd, WebSocket sock){
        try{
            /*
            WebSocketImpl wsi = (WebSocketImpl) sock;
            ByteChannel channel = wsi.channel;
            if(!(channel instanceof SocketChannel)){
                System.out.println("no socket channel");
                return;
            }
            Socket socket = ((SocketChannel)channel).socket();
            */
            if(cmd.equalsIgnoreCase("\003")){
                regenProc();
                System.out.println("SIGINT");
            }else {
                proc.outputWriter().write(cmd);
                proc.outputWriter().newLine();
                proc.outputWriter().flush();
            }


            proc.outputWriter().write("echo \"EXITPROCESS2097426789297647246298\"");
            proc.outputWriter().newLine();
            proc.outputWriter().flush();
            System.out.println("EXECUTE: "+cmd);
        }catch(Exception ex){
            ex.printStackTrace();
            String b64line = Base64.getEncoder().encodeToString(ex.getMessage().getBytes(StandardCharsets.UTF_8));
            sock.send("CMDRESP::"+b64line);
        }
    }

}
