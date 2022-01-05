package fr.hunh0w.wizardbox.server.protocol;

import java.io.Serializable;

public class WizardMessage implements Serializable {

    private static final long serialVersionUID = -3871598759338521948L;

    private byte[] data;

    public WizardMessage(byte[] data){
        this.data = data;
    }

    public byte[] getData(){
        return this.data;
    }

}
