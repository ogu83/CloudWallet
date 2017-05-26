package com.watchmen.cloudwallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final ActionBar.LayoutParams TV_LAYOUT_PARAMS = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    FilenameFilter directoryFilter = new FilenameFilter() {
        File f;

        public boolean accept(File dir, String name) {
            f = new File(dir.getAbsolutePath() + "/" + name);
            return f.isDirectory();
        }
    };
    FilenameFilter walletFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".wlt") || name.endsWith(".WLT"));
        }
    };

    private ArrayList<String> files = new ArrayList<>();
    private ArrayList<String> fileNames = new ArrayList<>();
    private File root = Environment.getExternalStorageDirectory();

    private void findFiles(File directory) {
        String subDirs[] = directory.list(directoryFilter);
        for (String dir : subDirs) {
            String fs = directory.getAbsolutePath() + "/" + dir;
            File f = new File(fs);
            findFiles(f);
        }
        String wltFiles[] = directory.list(walletFilter);
        for (String wlt : wltFiles) {
            String wltString = directory.getAbsolutePath() + "/" + wlt;
            files.add(wltString);
            fileNames.add(wlt);
        }
    }

    private byte[] readFileBytes(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private void decryptFile(String path, String password) {
        byte[] bytes = readFileBytes(path);
        try {
            String decoded = AESHelper.Decrypt(password, bytes);
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("VM", decoded);
            startActivity(intent);
        } catch (Exception ex) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Decrypt Error")
                    .setMessage("Decryption failed. Probably password is incorrect.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findFiles(root);
        Log.d("FILES COUNT", Integer.toString(files.size()));

        final ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, fileNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;

                final String itemTitle = (String) listView.getItemAtPosition(position);
                final String itemValue = files.get(itemPosition);

                final EditText txtPassword = new EditText(MainActivity.this);
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                txtPassword.setHint("Password");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Enter Password")
                        .setMessage("Password for " + itemTitle)
                        .setView(txtPassword)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String password = txtPassword.getText().toString();
                                decryptFile(itemValue, password);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });
    }
}