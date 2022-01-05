package fr.hunh0w.wizardbox.server.objects;

import fr.hunh0w.wizardbox.Main;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class WizardCTF {

    public static ArrayList<WizardCTF> CTFs = new ArrayList<>();

    private int id;
    private String start_command;
    private String name;
    private String flag;
    private Process proc;

    public WizardCTF(int id, String start_command, String name, String flag) {
        this.id = id;
        this.start_command = start_command;
        this.name = name;
        this.flag = flag;
        CTFs.add(this);
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

    public String exec(String cmd, WebSocket sock){
        try{
            proc = Runtime.getRuntime().exec("cmd /c "+cmd);

            String data = "";
            proc.getInputStream().transferTo(((WebSocketClient)sock).getSocket().getOutputStream());
            proc.waitFor();

            /*
            BufferedReader BR = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while((line = BR.readLine()) != null){
                if(data.isEmpty()) data = line+"\r\n";
                else data += line+"\r\n";
            }
            BR.close();

            data = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
            */
            return data;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
