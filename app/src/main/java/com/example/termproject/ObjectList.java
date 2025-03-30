package com.example.termproject;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Blob;

public class ObjectList extends AppCompatActivity {
    ListView listView=null;
    public static final int REQUEST_CODE_INSERT =1001;
    public ObjectAdapter mAdapter;
    TextView text_dealine;
    TextView text_count;
    TextView text_object;
    TextView text_memo;
    Button addObjectBtn;

    private EditText addname;
    private EditText addmemo;
    private Button editBtn;
    private long mJangoId = -1;
    private Button deleteBtn;
    private EditText searchObject;
    ImageView image;
    private TextView noobject;


    private Button coolbtn;
    private Button icebtn;
    private Button outbtn;
    private Button allbtn;

    private Button coolbackbtn;
    private Button icebackbtn;
    private Button outbackbtn;
    private Button allbackbtn;


    String sort;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.objectlist);

        addname = findViewById(R.id.addname);
        addmemo = findViewById(R.id.addmemo);
        editBtn = findViewById(R.id.editBtn);

        noobject=findViewById(R.id.noobject);
        ImageButton backbtn = (ImageButton) findViewById(R.id.backbtn);
        deleteBtn =(Button)findViewById(R.id.deleteBtn);
        Intent intent = getIntent();
        if(intent!= null){
            mJangoId = intent.getLongExtra("id", -1);
            title = intent.getStringExtra("title");
            String contents = intent.getStringExtra("contents");

            addname.setText(title);
            addmemo.setText(contents);
        }

        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String user=addname.getText().toString();
                String memo=addmemo.getText().toString();

                ContentValues contentValues = new ContentValues();
                ContentValues contentValues2 = new ContentValues();
                contentValues.put(JangoDB.JangoTable.COLUMN_NAME_TITLE, user);
                contentValues2.put(ObjectDB.ObjectTable.COLUMN_JANGO_NAME, user);
                contentValues.put(JangoDB.JangoTable.COLUMN_NAME_CONTENTS, memo);

                SQLiteDatabase db= JangoDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

                if(mJangoId == -1){
                    long newRowId = db.insert(JangoDB.JangoTable.TABLE_NAME,
                            null, contentValues);

                    if(newRowId == -1){
                        Toast.makeText(getApplicationContext(), "저장에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                    } else if(user.length()==0){
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        finish();
                    }
                }else{
                    int count =db.update(JangoDB.JangoTable.TABLE_NAME, contentValues,
                            JangoDB.JangoTable._ID+"="+mJangoId, null);
                    int count2 =db.update(ObjectDB.ObjectTable.TABLE_NAME, contentValues2,
                            ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId, null);
                    if(count==0) {
                        Toast.makeText(getApplicationContext(), "수정에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                    }else if(user.length()==0){
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
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

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db=JangoDbHelper.getInstance(ObjectList.this).getWritableDatabase();

                db.delete(JangoDB.JangoTable.TABLE_NAME, JangoDB.JangoTable._ID+"="+mJangoId,null);
                db.delete(ObjectDB.ObjectTable.TABLE_NAME, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId,null);
                setResult(RESULT_OK);
                finish();
            }
        });

        coolbtn = findViewById(R.id.cool);
        icebtn = findViewById(R.id.ice);
        outbtn = findViewById(R.id.out);
        allbtn=findViewById(R.id.all);
        coolbackbtn = findViewById(R.id.coolback);
        icebackbtn = findViewById(R.id.iceback);
        outbackbtn = findViewById(R.id.outback);
        allbackbtn = findViewById(R.id.allback);

        allbackbtn.bringToFront();


        addObjectBtn= findViewById(R.id.addObjectBtn);
        LayoutInflater inflater2 = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view2 = inflater2.inflate(R.layout.objectcustom, null);

        text_object= view2.findViewById(R.id.text_object);
        text_count= view2.findViewById(R.id.text_count);
        text_dealine= view2.findViewById(R.id.text_deadline);
        text_memo=view2.findViewById(R.id.text_memo);
        addObjectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent2 = new Intent(getApplicationContext(), Second.class);
                intent2.putExtra("jangoID", mJangoId);
                intent2.putExtra("JangoName", title);
                startActivityForResult(intent2, REQUEST_CODE_INSERT);

            }
        });

        listView = findViewById(R.id.Object_list);
        Cursor cursor=getObjectCursor();
        mAdapter= new ObjectAdapter(this, cursor);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //리스트뷰 버튼 클릭하면 수정 화면으로 이동
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ObjectList.this, EditObject.class);

                Cursor cursor=(Cursor) mAdapter.getItem(position);

                String objectTitle=cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_TITLE));
                String objectMemo=cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_CONTENTS));
                String count=cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_COUNT));
                String deadline=cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_DEAD));
                String type=cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_TYPE));

                byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_IMAGE));

                intent.putExtra("id", id);
                intent.putExtra("title", objectTitle);
                intent.putExtra("memo", objectMemo);
                intent.putExtra("type", type);
                intent.putExtra("count", count);
                intent.putExtra("image", b);
                intent.putExtra("deadline", deadline);

                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        // 정렬 스피너 선언
        Spinner spn = (Spinner) findViewById(R.id.spn);
        ArrayAdapter spn_adapter = ArrayAdapter.createFromResource(this, R.array.my_array, R.layout.spinnerview);
        spn_adapter.setDropDownViewResource(R.layout.spinnerview);
        spn.setAdapter(spn_adapter);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinnerview, null);

        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //정렬 스피너
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){ //아이디 값(생성된 순서) 순으로 내림차순 정렬
                    sort=ObjectDB.ObjectTable._ID + " DESC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    ObjectDB.ObjectTable._ID + " DESC")));

                }else if(position == 1){ //아이디 값(생성된 순서) 순으로 오름차순 정렬
                    sort=ObjectDB.ObjectTable._ID + " ASC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    ObjectDB.ObjectTable._ID + " ASC")));

                }else if(position == 2){ //이름 순으로 오름차순 정렬
                    sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " ASC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " ASC")));

                }else if(position ==3){ //이름 순으로 내림차순 정렬
                    sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " DESC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " DESC")));

                }else if(position ==4) { //유통기한 순으로 오름차순 정렬
                    sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " ASC";
                    mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId, null, null, null,
                                    ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " ASC")));

                }else if(position ==5) { //유통기한 순으로 내림차순 정렬
                    sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " DESC";
                    mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId, null, null, null,
                                    ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " DESC")));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //항목 있는지 확인하고 정렬 스피너와 문구 가시, 비가시 상태 변경
        if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                .query(ObjectDB.ObjectTable.TABLE_NAME,
                        null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                        ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
            noobject.setVisibility(View.INVISIBLE);
            spn.setVisibility(View.VISIBLE);
        }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                .query(ObjectDB.ObjectTable.TABLE_NAME,
                        null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                        ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
            noobject.setVisibility(View.VISIBLE);
            spn.setVisibility(View.INVISIBLE);
        }


        allbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {
                    allbackbtn.bringToFront();
                    coolbtn.bringToFront();
                    icebtn.bringToFront();
                    outbtn.bringToFront();
                    allbtn.setClickable(false);
                    coolbtn.setClickable(true);
                    icebtn.setClickable(true);
                    outbtn.setClickable(true);

                    spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            if(position == 0){
                                sort=ObjectDB.ObjectTable._ID + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId ,null,
                                                null,null, sort)));

                            }else if(position == 1){
                                sort=ObjectDB.ObjectTable._ID + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId ,null,
                                                null,null, sort)));

                            }else if(position == 2){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId ,null,
                                                null,null, sort)));

                            }else if(position ==3){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,
                                                null,null, sort)));
//
                            }else if(position ==4) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " ASC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId, null,
                                                null, null, sort)));
                            }else if(position ==5) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " DESC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId, null,
                                                null, null, sort)));
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                        noobject.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                        noobject.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }


                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                                    sort)));



                    searchObject = findViewById(R.id.searchObject);
                    searchObject.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {

                            String filterText=edit.toString();
                            if(filterText.length()>0){

                                String b[]={filterText+"%"};

                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, sort)));
                                if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }



                            }else{
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId ,null,null,null,
                                                sort)));

                                if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId,null,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId,null,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }
                    });
                }
                return true;
            }
        });


        coolbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {

                    allbtn.bringToFront();
                    coolbackbtn.bringToFront();
                    icebtn.bringToFront();
                    outbtn.bringToFront();
                    allbtn.setClickable(true);
                    coolbtn.setClickable(false);
                    icebtn.setClickable(true);
                    outbtn.setClickable(true);


                    spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String c[]={"냉장%"};
                            if(position == 0){
                                sort=ObjectDB.ObjectTable._ID + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position == 1){
                                sort=ObjectDB.ObjectTable._ID + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position == 2){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position ==3){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position ==4) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " ASC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null, null, sort)));
                            }else if(position ==5) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " DESC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null, null, sort)));
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });





                    String a[]={"냉장%"};
                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                        noobject.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                        noobject.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }

                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, sort)));

                    searchObject = findViewById(R.id.searchObject);
//                    searchObject.setText("");
                    searchObject.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {

                            String filterText=edit.toString();
                            if(filterText.length()>0){

                                String b[]={"냉장%", filterText+"%"};

                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, sort)));

                                if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0) {
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);

                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0) {
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                sort)));
                                if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()!=0) {
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }
                    });
                }
                return true;
            }
        });



        icebtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {
                    allbtn.bringToFront();
                    coolbtn.bringToFront();
                    icebackbtn.bringToFront();
                    outbtn.bringToFront();
                    allbtn.setClickable(true);
                    coolbtn.setClickable(true);
                    icebtn.setClickable(false);
                    outbtn.setClickable(true);


                    spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String c[]={"냉동%"};
                            if(position == 0){
                                sort=ObjectDB.ObjectTable._ID + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position == 1){
                                sort=ObjectDB.ObjectTable._ID + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position == 2){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position ==3){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position ==4) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " ASC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId+ " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null, null,  sort)));
                            }else if(position ==5) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " DESC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId+ " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null, null, sort)));
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                    String a[]={"냉동%"};

                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                        noobject.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                        noobject.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }

                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, sort)));

                    searchObject = findViewById(R.id.searchObject);
//                    searchObject.setText("");
                    searchObject.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {

                            String filterText=edit.toString();
                            if(filterText.length()>0){

                                String b[]={"냉동%", filterText+"%"};

                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, sort)));

                                if( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                sort)));

                                if(  JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }
                    });
                }
                return true;
            }
        });



        outbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int status = event.getAction();
                if(status == MotionEvent.ACTION_UP) {
                    allbtn.bringToFront();
                    coolbtn.bringToFront();
                    icebtn.bringToFront();
                    outbackbtn.bringToFront();
                    allbtn.setClickable(true);
                    coolbtn.setClickable(true);
                    icebtn.setClickable(true);
                    outbtn.setClickable(false);

                    spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String c[]={"실외%"};
                            if(position == 0){
                                sort=ObjectDB.ObjectTable._ID + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position == 1){
                                sort=ObjectDB.ObjectTable._ID + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position == 2){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " ASC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position ==3){
                                sort=ObjectDB.ObjectTable.COLUMN_NAME_TITLE + " DESC";
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null,null, sort)));
                            }else if(position ==4) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " ASC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null, null, sort)));
                            }else if(position ==5) {
                                sort = ObjectDB.ObjectTable.COLUMN_NAME_DEAD + " DESC";
                                mAdapter.swapCursor((JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID + "=" + mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ? ", c,
                                                null, null, sort)));
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                    String a[]={"실외%"};

                    if( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                        noobject.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                        noobject.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }

                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?", a,
                                    null,null, sort)));

                    searchObject = findViewById(R.id.searchObject);
                    searchObject.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable edit) {

                            String filterText=edit.toString();
                            if(filterText.length()>0){

                                String b[]={"실외%", filterText+"%"};

                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, sort)));

                                if( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?" + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", b,
                                                null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                sort)));

                                if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                                    noobject.setVisibility(View.INVISIBLE);
                                    spn.setVisibility(View.VISIBLE);
                                }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                                        .query(ObjectDB.ObjectTable.TABLE_NAME,
                                                null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TYPE+" LIKE ?"  ,a,null,null,
                                                ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                                    noobject.setVisibility(View.VISIBLE);
                                    spn.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }
                    });
                }
                return true;
            }
        });


        searchObject = findViewById(R.id.searchObject);


        searchObject.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {

                String filterText=edit.toString();
                if(filterText.length()>0){

                    String a[]={filterText+"%"};

                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                        noobject.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", a,
                                    null,null, ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                        noobject.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }

                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId + " AND "+ObjectDB.ObjectTable.COLUMN_NAME_TITLE+" LIKE ?", a,
                                    null,null, sort)));
                }else{

                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId   ,null,null,null,
                                    ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                        noobject.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId   ,null,null,null,
                                    ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                        noobject.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(ObjectDB.ObjectTable.TABLE_NAME,
                                    null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId   ,null,null,null,
                                    sort)));

                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }



    private Cursor getObjectCursor(){
        JangoDbHelper dbHelper = JangoDbHelper.getInstance(this);

        return dbHelper.getReadableDatabase()
                .query(ObjectDB.ObjectTable.TABLE_NAME,
                        null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId, null, null,null, ObjectDB.ObjectTable._ID + " DESC");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INSERT && resultCode ==RESULT_OK){
            mAdapter.swapCursor((getObjectCursor()));
            Spinner spn=findViewById(R.id.spn);
            if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                    .query(ObjectDB.ObjectTable.TABLE_NAME,
                            null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                            ObjectDB.ObjectTable._ID + " DESC").getCount()!=0){
                noobject.setVisibility(View.INVISIBLE);
                spn.setVisibility(View.VISIBLE);
            }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                    .query(ObjectDB.ObjectTable.TABLE_NAME,
                            null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId  ,null,null,null,
                            ObjectDB.ObjectTable._ID + " DESC").getCount()==0){
                noobject.setVisibility(View.VISIBLE);
                spn.setVisibility(View.INVISIBLE);
            }
            allbackbtn.bringToFront();
            coolbtn.bringToFront();
            icebtn.bringToFront();
            outbtn.bringToFront();
            mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                    .query(ObjectDB.ObjectTable.TABLE_NAME,
                            null, ObjectDB.ObjectTable.COLUMN_JANGO_ID+"="+mJangoId   ,null,null,null,
                            ObjectDB.ObjectTable._ID + " DESC")));
        }
    }
    private static class ObjectAdapter extends CursorAdapter {


        public ObjectAdapter(Context context, Cursor c) {
            super(context, c, true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context)
                    .inflate(R.layout.objectcustom, parent, false);

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = view.findViewById(R.id.text_object);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_TITLE)));
            TextView objectdeadline = view.findViewById(R.id.text_deadline);
            objectdeadline.setText(cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_DEAD)));
            TextView ojbectCount = view.findViewById(R.id.text_count);
            ojbectCount.setText(cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_COUNT)));
            TextView ojbectMemo = view.findViewById(R.id.text_memo);
            ojbectMemo.setText(cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_CONTENTS)));
            TextView objectType = view.findViewById(R.id.text_type);
            objectType.setText(cursor.getString(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_TYPE)));
            ImageView image = view.findViewById(R.id.image);
            byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow(ObjectDB.ObjectTable.COLUMN_NAME_IMAGE));
            Drawable image2 = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
            image.setImageDrawable(image2);
        }
    }
}