/*
 * Decompiled with CFR 0.151.
 */
package com.TheModerator.Fall;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ErrorReporter
extends Thread {
    String reportURL = "software.roestudios.co.uk/Punishmental/Reporting/ErrorReport.php";
    String content;

    public ErrorReporter(String contenta) {
        this.content = contenta;
    }

    @Override
    public void run() {
        URL url = null;
        URLConnection urlConn = null;
        try {
            String str;
            url = new URL(this.reportURL);
            urlConn = url.openConnection();
            urlConn.setReadTimeout(3000);
            urlConn.setConnectTimeout(3000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream());
            printout.writeBytes(this.content);
            printout.flush();
            printout.close();
            DataInputStream input = new DataInputStream(urlConn.getInputStream());
            while ((str = input.readLine()) != null) {
            }
            input.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

