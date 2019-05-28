package com.project.doodle.features;

import net.gjerull.etherpad.client.EPLiteClient;

import static com.project.doodle.Utils.generateSlug;

public class PadFeature {

    private EPLiteClient client;
    private String padId = generateSlug(6);
    private String padUrl = "http://localhost:9001/";
    private static final String apikey = "fb01e499abd828370eaede1c05f1b91bbd941667413f45f6f32a547b1043da76";
    private String titlePoll;

    public PadFeature(String titlePoll) {
        this.client = new EPLiteClient(padUrl, apikey);
        this.client.createPad(padId);
        System.out.println(padId);
        this.titlePoll = titlePoll;
    }

    public void createAuthor(String auth){
        this.client.createAuthor(auth);
    }

    public String getPadUrl(){
        return this.padUrl + "p/"+ this.padId;
    }

    public void deletePad(){
        this.client.deletePad(this.padId);
    }

    public void init() {
        String str = client.getText(padId).get("text").toString();
        final String substring = "This pad goes with the poll : "+this.titlePoll+'\n';
        client.setText(padId, substring);
    }

    @Override
    public String toString(){
        return getPadUrl();
    }
}
