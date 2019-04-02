package cn.edu.sustc.recoder.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadMusicFile {
    private String musicUrl;
    byte[] buffer;
    public ReadMusicFile(String path){
        musicUrl = path;
    }

    
    public static byte[] read(String path){
        byte[] buf = null;
        File file = new File(path);
        try {
            InputStream fis = new FileInputStream(file);
            buf = new byte[(int)file.length()];
            fis.read(buf, 0, buf.length);
            fis.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }


}
