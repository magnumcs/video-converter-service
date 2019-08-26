package com.portfolio.magnum.utils;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    private FileUtil() {
    }

    public static File getFileFromBytes(byte[] bytes, String extension) {
        File source = new File("source"+extension);

        try (FileOutputStream fos = new FileOutputStream(source)) {
            fos.write(bytes);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return source;
    }

}
