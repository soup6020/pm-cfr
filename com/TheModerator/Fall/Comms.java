/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.json.simple.JSONObject
 *  org.json.simple.parser.JSONParser
 */
package com.TheModerator.Fall;

import com.TheModerator.Fall.Fall;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Comms {
    private Fall master;
    Logger log = Logger.getLogger("Minecraft");

    public Comms(Fall plugin) {
        this.master = plugin;
    }

    public void TranslateChat(Player p, String message, String lang) {
        TranslateChat senddata = new TranslateChat(this.master, p.getName(), message, lang);
        Thread t = new Thread(senddata);
        t.start();
    }

    public class TranslateChat
    implements Runnable {
        private Fall master;
        public String playerName;
        public String ChatMsg;
        public String language;
        Logger log = Logger.getLogger("Minecraft");

        public TranslateChat(Fall master, String playerName, String ChatMsg, String language) {
            this.master = master;
            this.playerName = playerName;
            this.ChatMsg = ChatMsg;
            this.language = language;
        }

        @Override
        public void run() {
            this.doWork();
        }

        public boolean doWork() {
            block6: {
                JSONObject jsonObject;
                BufferedReader in;
                block5: {
                    String target = "NOT SET";
                    String line = "";
                    try {
                        target = "http://roestudios.co.uk/translate/translator.php?app=Punishmental&ver=" + this.master.getDescription().getVersion() + "&to=" + this.language + "&text=" + URLEncoder.encode(this.ChatMsg, "UTF-8");
                        URL url = new URL(target);
                        InputStreamReader isr = new InputStreamReader(url.openStream());
                        in = new BufferedReader(isr);
                        JSONParser parser = new JSONParser();
                        while (line == "" || line == " " || line.isEmpty() || line == null) {
                            line = in.readLine();
                        }
                        Object obj = parser.parse(line);
                        jsonObject = (JSONObject)obj;
                        if (!jsonObject.get((Object)"status").toString().equalsIgnoreCase("SUCCESS")) break block5;
                        this.master.getServer().getPlayer(this.playerName).chat(String.valueOf(jsonObject.get((Object)"translation").toString()) + "</string>");
                        in.close();
                        return true;
                    }
                    catch (Throwable t) {
                        this.log.warning("Failed to translate... Server Response (If any): ");
                        this.log.warning(line);
                        this.log.warning("Stack Trace: ");
                        t.printStackTrace();
                        return false;
                    }
                }
                if (!jsonObject.get((Object)"status").toString().equalsIgnoreCase("ERROR")) break block6;
                this.master.getServer().getPlayer(this.playerName).chat(String.valueOf(jsonObject.get((Object)"errorReason").toString()) + "</string>");
                in.close();
                return true;
            }
            this.master.getServer().getPlayer(this.playerName).chat("*throws up on carpet*</string>");
            return true;
        }
    }
}

