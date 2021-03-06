package org.pinwheel.agility.net.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class FileParser extends DataParserAdapter<File> {

    private File result;
    private String fileName;

    public FileParser(File fileName) {
        this(fileName.getAbsolutePath());
    }

    public FileParser(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void parse(InputStream inStream) throws Exception {
        result = save(fileName, inStream);
        dispatchComplete();
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        result = save(fileName, dataBytes);
        dispatchComplete();
    }

    @Override
    public File getResult() {
        return result;
    }

    @Override
    public void release() {
        super.release();
        result = null;
        fileName = null;
    }

    protected final File save(String name, InputStream inStream) throws Exception {
        File target_file = null;

        boolean isException = false;
        FileOutputStream fout = null;
        File file = new File(name);
        File path = file.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        } else if (file.exists()) {
            file.delete();
        }
        try {
            long lastTime = 0;
            long currentTime = 0;
            long progress = 0;

            fout = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buf)) != -1) {
                fout.write(buf, 0, len);
                // progress callback
                progress += len;
                currentTime = System.currentTimeMillis();
                if (currentTime - lastTime > 1000) {
                    dispatchProgress(progress, -1);// -1 unknown
                    lastTime = currentTime;
                }
                // end
            }
            target_file = file;
        } catch (Exception e) {
            isException = true;
        } finally {
            if (fout != null) {
                fout.flush();
                fout.close();
                fout = null;
            }
            if (isException) {
                throw new Exception("save stream exception");
            }
        }
        return target_file;
    }

    @Deprecated
    protected final File save(String name, byte[] data) throws Exception {
        File target_file = null;

        boolean isException = false;
        FileOutputStream fout = null;
        File file = new File(name);
        File path = file.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        } else if (file.exists()) {
            file.delete();
        }
        try {
            fout = new FileOutputStream(file);

            // progress callback
            dispatchProgress(0, data.length);

            fout.write(data);

            dispatchProgress(data.length, data.length);
            // end

            target_file = file;
        } catch (Exception e) {
            isException = true;
        } finally {
            if (fout != null) {
                fout.flush();
                fout.close();
                fout = null;
            }
            if (isException) {
                throw new Exception("save byte exception");
            }
        }
        return target_file;
    }

}
