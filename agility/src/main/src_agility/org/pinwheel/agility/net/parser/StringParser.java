package org.pinwheel.agility.net.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class StringParser extends DataParserAdapter<String> {

    private String result;

    @Override
    public void parse(InputStream inStream) throws Exception {
        result = streamToString(inStream);
        dispatchComplete();
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        dispatchProgress(0, 0);

        result = new String(dataBytes, "UTF-8");

        dispatchProgress(0, result.getBytes().length);

        dispatchComplete();
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void release() {
        super.release();
        result = null;
    }

    protected final String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            long lastTime = 0;
            long currentTime = 0;
            long progress = 0;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

                // progress callback
                progress += line.length();
                currentTime = System.currentTimeMillis();
                if (currentTime - lastTime > 1000) {
                    dispatchProgress(progress, -1);// -1 unknown
                    lastTime = currentTime;
                }
                // end
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
