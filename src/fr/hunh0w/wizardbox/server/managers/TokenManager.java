package fr.hunh0w.wizardbox.server.managers;

import java.util.ArrayList;

public class TokenManager {

    private static ArrayList<String> tokens = new ArrayList<>();

    public static void addToken(String token){
        tokens.add(token);
    }

    public static void removeToken(String token){
        while(tokens.contains(token))
            tokens.remove(token);
    }

    public static boolean tokenExists(String token){
        for(String tokn : tokens)
            if(tokn.equals(token)) return true;
        return false;
    }

    public static int size(){
        return tokens.size();
    }

}
