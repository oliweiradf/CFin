package br.com.oliweira.cfin;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class BackupActivity extends AppCompatActivity {

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions( activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_backup);

        verifyStoragePermissions(this);

        //creating a new folder for the database to be backuped to
        File direct = new File(Environment.getExternalStorageDirectory() + "/backupapp");

        if(!direct.exists())
        {
            if(direct.mkdir())
            {
                //directory is created;
            }
        }
    }

    //importa database
    public void importaDB(View view) {
        // TODO Auto-generated method stub
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "PackageName"
                        + "//databases//" + "DatabaseName";
                String backupDBPath  = "/BackupFolder/DatabaseName";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

    //exporta database
    public void exportaDB(View view) {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "br.com.oliweira.cfin"
                        + "//databases//" + "db_cfin";
                String backupDBPath  = "/backupapp/db_cfin_backup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
