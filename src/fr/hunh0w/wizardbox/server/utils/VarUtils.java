package fr.hunh0w.wizardbox.server.utils;

public class VarUtils {

    public static int getInt(String str){
        try{
            int i = Integer.parseInt(str);
            return i;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

}
