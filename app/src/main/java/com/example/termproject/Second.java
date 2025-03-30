package com.example.termproject;


import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class Second extends AppCompatActivity {
    public static final int DEFAULT_GALLERY_REQUEST_CODE=1001;
    private ImageButton backbtn;
    private EditText objectname;
    private EditText objectmemo;
    private TextView objectcount;
    private Button objectdeadline;
    private Button savebtn;
    private Button coolbtn;
    private Button icebtn;
    private Button outbtn;
    private int currnum;


    ImageView image;
    private TextView imageText;
    private long mObjectId=-1;
    String type="분류 미정";

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        savebtn =(Button)findViewById(R.id.savebtn);

        backbtn = (ImageButton) findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        objectname=findViewById(R.id.objectname);

        objectmemo=findViewById(R.id.objectmemo);
        objectcount=findViewById(R.id.objectcount);
        objectdeadline=findViewById(R.id.objectdeadline);

        imageText=findViewById(R.id.imageText);
        coolbtn = findViewById(R.id.cool);
        icebtn = findViewById(R.id.ice);
        outbtn = findViewById(R.id.out);
        Button coolbackbtn = findViewById(R.id.coolback);
        Button icebackbtn = findViewById(R.id.iceback);
        Button outbackbtn = findViewById(R.id.outback);


        objectdeadline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar calendar = Calendar.getInstance();

                int pYear = calendar.get(Calendar.YEAR);
                int pMonth = calendar.get(Calendar.MONTH);
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(Second.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int mon, int day) {
                        mon = mon + 1;
                        String NewDate = year + "/" + mon + "/" + day;
                        objectdeadline.setText(NewDate);
                    }
                }, pYear, pMonth, pDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });



        coolbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {
                    type="";
                    coolbackbtn.bringToFront();
                    icebtn.bringToFront();
                    outbtn.bringToFront();
                    coolbtn.setClickable(false);
                    icebtn.setClickable(true);
                    outbtn.setClickable(true);

                    type=coolbtn.getText().toString();
                }
                return true;
            }
        });
        icebtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {
                    type="";
                    coolbtn.bringToFront();
                    icebackbtn.bringToFront();
                    outbtn.bringToFront();
                    coolbtn.setClickable(true);
                    icebtn.setClickable(false);
                    outbtn.setClickable(true);
                    type=icebtn.getText().toString();
                }
                return true;
            }
        });
        outbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {
                    type="";
                    coolbtn.bringToFront();
                    icebtn.bringToFront();
                    outbackbtn.bringToFront();
                    coolbtn.setClickable(true);
                    icebtn.setClickable(true);
                    outbtn.setClickable(false);
                    type=outbtn.getText().toString();
                }
                return true;
            }
        });


        Intent secondIntent = getIntent();
        secondIntent.getIntExtra("번호", 0);

        Intent intent = getIntent();

        image=findViewById(R.id.image);
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
                Drawable drawable = (Drawable) image.getDrawable();
                String user=objectname.getText().toString();
                String memo=objectmemo.getText().toString();
                String Ocount=objectcount.getText().toString();
                String deadline=objectdeadline.getText().toString();
                Long ID=intent.getLongExtra("jangoID", 0);
                String JangoName=intent.getStringExtra("JangoName");


                ContentValues contentValues = new ContentValues();
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_TITLE, user);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_CONTENTS, memo);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_COUNT, Ocount);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_DEAD, deadline);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_JANGO_ID, ID);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_IMAGE, getPicture(drawable));
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_TYPE, type);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_JANGO_NAME, JangoName);

                SQLiteDatabase db= JangoDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

                if(mObjectId == -1){
                    if(user.length()!=0) {
                        long newRowId = db.insert(ObjectDB.ObjectTable.TABLE_NAME,
                                null, contentValues);

                        if (newRowId == -1) {
                            Toast.makeText(getApplicationContext(), "저장에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        }  else{
                            Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);

                            finish();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(user.length()!=0) {
                        int count = db.update(ObjectDB.ObjectTable.TABLE_NAME, contentValues,
                                ObjectDB.ObjectTable._ID + "=" + mObjectId, null);
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public void plusNum(View view){
        currnum = Integer.parseInt(objectcount.getText().toString());
        currnum = currnum+1;
        objectcount.setText(String.valueOf(currnum));
    }

    public void minusNum(View view){
        currnum = Integer.parseInt(objectcount.getText().toString());
        if (currnum > 0) {
            currnum = currnum-1;
        }
        else{
            Toast.makeText(this.getApplicationContext(),"0보다 적은 수는 저장할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
        objectcount.setText(String.valueOf(currnum));
    }
}