package cwo.pakidermo.dgom.com.mx.cwo.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by beto on 22/01/18.
 */

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static boolean videoExists(VideoContent vc, Context context){
        String path = getVideoPath(vc,context);
        File file = new File(path);
        return file.exists();
    }


    /**
     * Obtiene el directorio donde se almacenan los videos
     * @param vc
     * @param context
     * @return
     */
    public static String getVideoDirPath(VideoContent vc, Context context){
        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + vc.getUiid() + ".zip";
        String path =
                context.getFilesDir().getAbsolutePath() +
                        File.separatorChar +
                        "videos";
        return path;
    }

    public static String getVideoPath(VideoContent vc, Context context){
          //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + vc.getUiid() + ".zip";
        String path =
                context.getFilesDir().getAbsolutePath() +
                        File.separatorChar +
                        "videos" +
                        File.separatorChar +
                        vc.getUiid() +
                        ".mp4";
          return path;
    }

    public static String getVideoPathCipher(VideoContent vc){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + vc.getUiid() + ".cif";
        return path;
    }

    public static String getVideoPathUnCipher(VideoContent vc){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + vc.getUiid() + ".mp4";
        return path;
    }

    public static void deleteVideoFile(VideoContent vc, Context context){
        if(videoExists(vc,context)){
            File f = new File(getVideoPath(vc,context));
            f.delete();
        }
    }

/*
    public static void cipherVideoFile(VideoContent vc) throws Exception {
        File fileOrigen = new File(getVideoPath(vc));

        byte[] byteFile =  readFile(fileOrigen);
        byte[] yourKey = generateKey("password");
        byte[] filesBytes = encodeFile(yourKey, byteFile);

        File file = new File(getVideoPathCipher(vc));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(filesBytes);
        bos.flush();
        bos.close();

        //Borra el origen
        fileOrigen.delete();
    }


    public static void uncipherVideoFile(VideoContent vc) throws Exception {
        File file = new File(getVideoPathCipher(vc) );
        byte[] byteFile =  readFile(file);
        byte[] yourKey = generateKey("password");
        byte[] decodeFile = decodeFile(yourKey, byteFile);

        file = new File(getVideoPathUnCipher(vc) );

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(decodeFile);
        bos.flush();
        bos.close();
    }


    public static void deleteUncepherVideoFile(VideoContent vc){
        File file = new File(getVideoPathUnCipher(vc) );
        file.delete();
    }


    private static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    private static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }
*/
    private static byte[] generateKey(String password) throws Exception{
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

     static final class CryptoProvider extends Provider {
        /**
         * Creates a Provider and puts parameters
         */
        public CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG",
                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }
}
