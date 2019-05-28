package com.project.doodle.models;

import net.gjerull.etherpad.client.EPLiteClient;


public class PadFeature {


        EPLiteClient client;
        String padId;
        String padUrl;

        private static final String apikey = "fa8cce291d03acaf1dce7d137f73ce60aa2eeebdec77be42bcb8461d0e4278ea";

        public PadFeature(String padUrl, String apikey, String padId) {
            client = new EPLiteClient(padUrl, apikey);
            this.padId = padId;
            this.padUrl = padUrl;
            client.createPad(padId);
        }


        public void createAuthor(String auth){
            this.client.createAuthor(auth);
        }




        public static void main(String[] args) {
            PadFeature pad = new PadFeature("http://localhost:9001/", apikey, "erty");
            pad.addUser("Anne");
            pad.addUser("moi");
            pad.addUser("toi");
            System.out.println( pad.getPadUrl());
            pad.createAuthor("moityu");

        }

        public String getPadUrl(){
            return this.padUrl + "p/"+ this.padId;
        }

        public void addUser(String user) {
            String str = client.getText(padId).get("text").toString();
            final String substring = "Give your ideas here :\n";
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

        public void deletePad(String padId){
            this.client.deletePad(padId);
        }


}
