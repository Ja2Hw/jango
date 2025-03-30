package com.example.termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListView listView=null;
    public static final int REQUEST_CODE_INSERT =1000;
    public JangoAdapter mAdapter;
    LinearLayout addlayout;
    TextView titleText;
    TextView contentText;
    EditText search;
    ImageView image;
    String sort;

    private final String DEFAULT = "DEFAULT";
    long now;
    Date currDate;
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
    JangoDbHelper mydb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //데이터 없을 때의 첫 화면 요소들 선언
        addlayout = (LinearLayout) findViewById(R.id.addlayout);
        Button addJango = (Button) findViewById(R.id.addJango);

        //레이아웃 안에 다른 레이아웃을 가져오는 inflate, 가져온 후에 텍스트뷰와 이미지 추가할 수 있게 선언
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_custom, null);
        titleText = (TextView) view.findViewById(R.id.text_title);
        contentText = (TextView) view.findViewById(R.id.text_content);
        image=view.findViewById(R.id.image);

        //검색창 가시 상태로 설정
        search=findViewById(R.id.search);
        addlayout.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);

        //검색 실패했을 때 나오는 안내 문구 비가시 상태로 먼저 설정
        TextView nosearch = findViewById(R.id.nosearch);
        nosearch.setVisibility(View.INVISIBLE);

        //냉장고 추가하기 버튼 이벤트, AddJango 클래스로 인텐트
        addJango.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), AddJango.class);
                startActivityForResult(intent, REQUEST_CODE_INSERT);

            }
        });

        //+ 모양 버튼 이벤트, AddJango 클래스로 인텐트
        ImageButton addBtn = (ImageButton) findViewById(R.id.addbtn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), AddJango.class);
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });


        //스피너 선언, 스피너 레이아웃 가져오기
        Spinner spn = (Spinner) findViewById(R.id.spn);
        ArrayAdapter spn_adapter = ArrayAdapter.createFromResource(this, R.array.my_array2, R.layout.spinnerview);
        spn_adapter.setDropDownViewResource(R.layout.spinnerview);
        spn.setAdapter(spn_adapter);
        LayoutInflater inflater1 = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater1.inflate(R.layout.spinnerview, null);

        //스피너 클릭 이벤트
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){ //아이디 값(생성된 순서) 순으로 내림차순 정렬
                    sort= JangoDB.JangoTable._ID + " DESC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null ,null,null,null,
                                    JangoDB.JangoTable._ID + " DESC")));
                }else if(position == 1){ //아이디 값(생성된 순서) 순으로 오름차순 정렬
                    sort=JangoDB.JangoTable._ID + " ASC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null  ,null,null,null,
                                    JangoDB.JangoTable._ID + " ASC")));
                }else if(position == 2){ //이름 순으로 오름차순 정렬
                    sort=JangoDB.JangoTable.COLUMN_NAME_TITLE + " ASC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null  ,null,null,null,
                                    JangoDB.JangoTable.COLUMN_NAME_TITLE + " ASC")));
                }else if(position ==3){ //이름 순으로 내림차순 정렬
                    sort=JangoDB.JangoTable.COLUMN_NAME_TITLE + " DESC";
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null ,null,null,null,
                                    JangoDB.JangoTable.COLUMN_NAME_TITLE + " DESC")));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //냉장고 리스트뷰와 커서, 어댑터 선언
        listView = findViewById(R.id.Jango_list);
        Cursor cursor=getJangoCursor();
        mAdapter= new JangoAdapter(this, cursor);
        listView.setAdapter(mAdapter);

        //검색창 선언, 검색창 이벤트
        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {

                String filterText=edit.toString();
                if(filterText.length()>0){
                    String a[]={filterText+"%"};
                    //냉장고 데이터베이스 읽기
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, JangoDB.JangoTable.COLUMN_NAME_TITLE+" LIKE ?", a,
                                    null,null, sort)));
                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, JangoDB.JangoTable.COLUMN_NAME_TITLE+" LIKE ?", a,
                                    null,null, sort).getCount()==0){ //발견된 것이 없을 떄
                        nosearch.setVisibility(View.VISIBLE); //검색 실패 안내 가시
                        spn.setVisibility(View.INVISIBLE); //스피너 비가시
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, JangoDB.JangoTable.COLUMN_NAME_TITLE+" LIKE ?", a,
                                    null,null, sort).getCount()!=0){ //발견된 것이 있을 때
                        nosearch.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }
                }else{
                    mAdapter.swapCursor(( JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null  ,null,null,null,
                                    sort)));
                    if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null, null,
                                    null,null, sort).getCount()==0){
                        nosearch.setVisibility(View.VISIBLE);
                        spn.setVisibility(View.INVISIBLE);
                    }else if(JangoDbHelper.getInstance(getApplicationContext()).getReadableDatabase()
                            .query(JangoDB.JangoTable.TABLE_NAME,
                                    null, null, null,
                                    null,null, sort).getCount()!=0){
                        nosearch.setVisibility(View.INVISIBLE);
                        spn.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        if(mAdapter.getCount() != 0){ //추가된 항목이 있을 경우 기본 화면 비가시, 검색창과 스피너 가시
            addlayout.setVisibility(View.INVISIBLE);
            search.setVisibility(View.VISIBLE);
            spn.setVisibility(View.VISIBLE);

        } else{ //기본 화면 가시, 검색창과 스피너 비가시
            addlayout.setVisibility(View.VISIBLE);
            search.setVisibility(View.INVISIBLE);
            spn.setVisibility(View.INVISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ObjectList.class);

                Cursor cursor = (Cursor) mAdapter.getItem(position);

                String title=cursor.getString(cursor.getColumnIndexOrThrow(JangoDB.JangoTable.COLUMN_NAME_TITLE));
                String contents=cursor.getString(cursor.getColumnIndexOrThrow(JangoDB.JangoTable.COLUMN_NAME_CONTENTS));
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);
                startActivityForResult(intent, REQUEST_CODE_INSERT);

            }
        });


        mydb = new JangoDbHelper(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
    }

    void createNotificationChannel(String channelId, String channelName, int importance){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    void createNotification(String channelId, int id, String title, String text, Intent intent){
        Bitmap LargeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.jangoicon4);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.jangoiconsmall)
                .setLargeIcon(LargeIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.setBigContentTitle(title);
        style.bigText(text);

        builder.setStyle(style);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    void destroyNotification(int id){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void notifi(View view) {
        ArrayList listOver = new ArrayList();
        ArrayList listYet = new ArrayList();
        now = System.currentTimeMillis();
        currDate = new Date(now);
        String getTime = simpleDate.format(currDate);
        mydb.notice(getTime, listOver, listYet);
        String OverText = String.join("", listOver);
        String YetText = String.join("", listYet);

        String allText = OverText + System.lineSeparator() + YetText;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        createNotification(DEFAULT, 1, "Jango: 유통기한 안내", allText, intent);
    }



    private Cursor getJangoCursor(){
        JangoDbHelper dbHelper = JangoDbHelper.getInstance(this);
        return dbHelper.getReadableDatabase()
                .query(JangoDB.JangoTable.TABLE_NAME,
                        null, null   ,null,null,null, JangoDB.JangoTable._ID + " DESC");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INSERT && resultCode ==RESULT_OK){
            mAdapter.swapCursor((getJangoCursor()));

            Spinner spn=findViewById(R.id.spn);
            if(mAdapter.getCount() != 0){
                addlayout.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                spn.setVisibility(View.VISIBLE);
            } else{
                addlayout.setVisibility(View.VISIBLE);
                search.setVisibility(View.INVISIBLE);
                spn.setVisibility(View.INVISIBLE);
            }
        }
    }
    private static class JangoAdapter extends CursorAdapter {
        public JangoAdapter(Context context, Cursor c) {
            super(context, c, true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context)
                    .inflate(R.layout.item_custom, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = view.findViewById(R.id.text_title);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(JangoDB.JangoTable.COLUMN_NAME_TITLE)));
            TextView contentText = view.findViewById(R.id.text_content);
            contentText.setText(cursor.getString(cursor.getColumnIndexOrThrow(JangoDB.JangoTable.COLUMN_NAME_CONTENTS)));
            ImageView image = view.findViewById(R.id.image);
            byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow(JangoDB.JangoTable.COLUMN_NAME_IMAGE));
            Drawable image2 = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
            image.setImageDrawable(image2);
        }
    }


}