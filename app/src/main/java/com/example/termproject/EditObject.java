package com.example.termproject;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.sql.Blob;
import java.util.Calendar;

public class EditObject extends AppCompatActivity {
    public static final int DEFAULT_GALLERY_REQUEST_CODE=1001;
    private long mJangoId = -1;
    private EditText objectname;
    private TextView objectcount;
    private EditText objectmemo;
    private Button objectdeadline;
    private Button deleteBtn;
    private Button editBtn;
    private ImageButton backBtn;

    private Button coolbtn;
    private Button icebtn;
    private Button outbtn;

    private Button coolbackbtn;
    private Button icebackbtn;
    private Button outbackbtn;

    String type="분류 미정";
    ImageView image;
    private TextView imageText;

    private int currnum;

    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editobject);

        objectname = findViewById(R.id.objectname);
        objectcount = findViewById(R.id.objectcount);
        objectmemo = findViewById(R.id.objectmemo);
        objectdeadline = findViewById(R.id.objectdeadline);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        backBtn=findViewById(R.id.backbtn);

        coolbtn = findViewById(R.id.cool);
        icebtn = findViewById(R.id.ice);
        outbtn = findViewById(R.id.out);

        coolbackbtn = findViewById(R.id.coolback);
        icebackbtn = findViewById(R.id.iceback);
        outbackbtn = findViewById(R.id.outback);
        image = findViewById(R.id.image);
        imageText=findViewById(R.id.imageText);

        objectdeadline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar calendar = Calendar.getInstance();

                int pYear = calendar.get(Calendar.YEAR);
                int pMonth = calendar.get(Calendar.MONTH);
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);
//                calender.minData=System.currentTimeMillis();
                datePickerDialog = new DatePickerDialog(EditObject.this, new DatePickerDialog.OnDateSetListener() {
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

        Intent intent = getIntent();
        if(intent!= null){
            mJangoId = intent.getLongExtra("id", -1);
            String title = intent.getStringExtra("title");

            String contents = intent.getStringExtra("memo");
            String type2 = intent.getStringExtra("type");
            String count = intent.getStringExtra("count");

            byte[] arr = getIntent().getByteArrayExtra("image");
            Bitmap imagearr = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            String jangoId = intent.getStringExtra("jangoId");
            String deadline = intent.getStringExtra("deadline");

            String typecold="냉장";
            String typeice="냉동";
            String typeout="실외";
            objectname.setText(title);
            objectcount.setText(count);
            objectmemo.setText(contents);
            objectdeadline.setText(deadline);

            image.setImageBitmap(imagearr);

            if(type2.equals(typecold)){
                coolbackbtn.bringToFront();
                type="냉장";
            }else if(type2.equals(typeice)){
                icebackbtn.bringToFront();
                type="냉동";
            }else if(type2.equals(typeout)){
                outbackbtn.bringToFront();
                type="실외";
            }else{
                coolbtn.bringToFront();
                icebtn.bringToFront();
                outbtn.bringToFront();
            }

        }

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

        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();

        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Drawable drawable = (Drawable) image.getDrawable();
                String user=objectname.getText().toString();
                String memo=objectmemo.getText().toString();
                String cnt=objectcount.getText().toString();
                String dead=objectdeadline.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_TITLE, user);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_CONTENTS, memo);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_COUNT, cnt);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_DEAD, dead);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_TYPE, type);
                contentValues.put(ObjectDB.ObjectTable.COLUMN_NAME_IMAGE, getPicture(drawable));

                SQLiteDatabase db= JangoDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

                if(mJangoId == -1) {
                    if (user.length() != 0) {
                        long newRowId = db.insert(ObjectDB.ObjectTable.TABLE_NAME,
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
                        int count = db.update(ObjectDB.ObjectTable.TABLE_NAME, contentValues,
                                ObjectDB.ObjectTable._ID + "=" + mJangoId, null);
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

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db=JangoDbHelper.getInstance(EditObject.this).getWritableDatabase();

                db.delete(ObjectDB.ObjectTable.TABLE_NAME, ObjectDB.ObjectTable._ID+"="+mJangoId,null);
                setResult(RESULT_OK);
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
