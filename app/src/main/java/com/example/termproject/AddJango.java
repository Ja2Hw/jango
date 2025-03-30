package com.example.termproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap.CompressFormat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddJango extends AppCompatActivity {
    Uri uri;
    public static final int REQUEST_CODE_INSERT =1000;
    public static final int DEFAULT_GALLERY_REQUEST_CODE=1001;

    private EditText addname;
    private EditText addmemo;
    private Button savebtn;
    private long mJangoId = -1;
    ImageView image;
    private TextView imageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addjango);
        addname = findViewById(R.id.addname);
        addmemo = findViewById(R.id.addmemo);
        savebtn = findViewById(R.id.savebtn);
        ImageButton backbtn = (ImageButton) findViewById(R.id.backbtn);
        image=findViewById(R.id.image);
        imageText=findViewById(R.id.imageText);


        Intent intent = getIntent();
        if(intent!= null){
            mJangoId = intent.getLongExtra("id", -1);
            String title = intent.getStringExtra("title");
            String contents = intent.getStringExtra("contents");

            addname.setText(title);
            addmemo.setText(contents);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2= new Intent();
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent2, DEFAULT_GALLERY_REQUEST_CODE);
            }
        });


        savebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String user=addname.getText().toString();
                String memo=addmemo.getText().toString();
                Drawable drawable = (Drawable) image.getDrawable();
                ContentValues contentValues = new ContentValues();
                contentValues.put(JangoDB.JangoTable.COLUMN_NAME_TITLE, user);
                contentValues.put(JangoDB.JangoTable.COLUMN_NAME_CONTENTS, memo);
                contentValues.put(JangoDB.JangoTable.COLUMN_NAME_IMAGE, getPicture(drawable));
                SQLiteDatabase db= JangoDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

                if(mJangoId == -1) {
                    if (user.length() != 0) {
                        long newRowId = db.insert(JangoDB.JangoTable.TABLE_NAME,
                                null, contentValues);

                        if (newRowId == -1) {
                            Toast.makeText(getApplicationContext(), "저장에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        }  else {
                            Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);

                            finish();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (user.length() != 0) {
                        int count = db.update(JangoDB.JangoTable.TABLE_NAME, contentValues,
                                JangoDB.JangoTable._ID + "=" + mJangoId, null);
                        if (count == 0) {
                            Toast.makeText(getApplicationContext(), "수정에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DEFAULT_GALLERY_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Glide.with(getApplicationContext()).load(data.getData()).override(300, 500).into(image);
                imageText.setVisibility(View.INVISIBLE);
            }
        }
    }

    private byte[] getPicture(Drawable drawable) {
        if(drawable == null) {
            return null;
        }
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }
}
