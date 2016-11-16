package com.rawalinfocom.rcontact.helper;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Monal on 15/11/16.
 * <p>
 * A Utility class to manage all File Related Operations
 */

public class FileUtils {

    private Bitmap bitmap;
    private File rContactDir;

    public FileUtils(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean saveImageToDirectory() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            rContactDir = new File(root + "/RContact");

            if (!rContactDir.exists()) {
                rContactDir.mkdirs();
            }

            String name = new Date().getTime() + ".png";
            rContactDir = new File(rContactDir, name);
            FileOutputStream out = new FileOutputStream(rContactDir);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            /*Bitmap myBitmap = BitmapFactory.decodeFile(myDir.getAbsolutePath());
            Log.i("file absolute path: ", myDir.getAbsolutePath());*/
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public File getrContactDir() {
        return rContactDir;
    }
}
