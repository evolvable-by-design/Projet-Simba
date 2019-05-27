package com.project.doodle.models;

import net.gjerull.etherpad.client.EPLiteClient;

import java.util.List;
import java.util.Map;

public class PadFeature {


        EPLiteClient client;
        String padId;
        String padUrl;

        public PadFeature(String padUrl, String apikey, String padId) {
            client = new EPLiteClient(padUrl, apikey);
            this.padId = padId;
            this.padUrl = padUrl;
            client.createPad(padId);

            checkPad();
        }

        public static void main(String[] args) {
            PadFeature pad = new PadFeature("http://localhost:9001/", "00d4bfe69d0cb2bb7c3a3491500d31a89d39335a4c18cc89670b3c6f849c901c", "testPad1 ");
            pad.addUser("Anne");
            pad.addUser("moi");
            pad.addUser("toi");
        }

        public void addUser(String user) {
            String str = client.getText(padId).get("text").toString();
            final String substring = "Liste des participants:\n";
            int index = str.indexOf(substring);
            if (index == -1) {
                str = substring;
                index = 0;
            }
            index += substring.length();
            String begin = str.substring(0, index);
            String end = str.substring(index);
            str = begin + user + "\n" + end;
            client.setText(padId, str);
        }

        public void setTitle(String title) {
            //TODO
        }

        private void checkPad() {
            Map result = client.listAllPads();
            List padIds = (List) result.get("padIDs");
            boolean exist = padIds.contains(padId);
            if(!exist) {
                client.createPad(padId);
            }

        }
}
