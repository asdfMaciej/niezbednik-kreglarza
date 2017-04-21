package io.maciej01.niezbednikkreglarza;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import com.orm.SugarDb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;


/**
 * Created by Maciej on 2017-04-21.
 */

public class ExportHandler {
    public static String DB_FILEPATH = "/data/data/io.maciej01.niezbednikkreglarza/databases/Sugar.db";
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
    public boolean importDatabase(Context ctx, String dbPath) throws IOException {
        SugarDb myDatabase = new SugarDb(ctx);
        myDatabase.close();
        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            myDatabase.close();
            return true;
        }
        return false;
    }


    public String copyDataBase(Context ctx) throws IOException {
        // Open your local db as the input stream
        FileInputStream myInput = new FileInputStream(new File(DB_FILEPATH));
        // Path to the just created empty db
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/niezbednik");
        dir.mkdir();
        File file = new File(dir, "niezbednikDb.db");
        String retStr = file.getCanonicalPath();

        file.createNewFile();
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(file);
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);

        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        return retStr;
    }
}
