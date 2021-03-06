package com.example.zpp.mydiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_back,btn_delete;
    private TextView tv_title,tv_createtime,tv_content,tv_man;
    private SQLiteDatabase mDatabase;
    private int []idlist;
    private int id;
    private ImageView tv_photos;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDatabase = new DBHelper(this).getWritableDatabase();
        queryTitle();
        initView();
        initEvent();
    }

    public void initView(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_createtime = (TextView) findViewById(R.id.tv_createtime);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_man = (TextView) findViewById(R.id.tv_man);
        tv_photos = findViewById(R.id.tv_photos);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(DetailActivity.this);
        btn_delete = (Button) findViewById(R.id.btn_delete);
    }

    public void initEvent(){
        Bundle b=getIntent().getExtras();
        //获取Bundle的信息
        int pos=b.getInt("id");
        id=idlist[pos];
        System.out.println("id:"+id);
        Cursor cursor= mDatabase.query(DBHelper.TABLE_NAME,DBHelper.TABLE_COLUMNS,"id=?",new String[]{id+""},null,null,null);
        while (cursor != null && cursor.moveToNext()) {

            tv_title.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE)));
            tv_createtime.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CREATETIME)));
            tv_content.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT)));
            tv_man.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MAN)));
            ShowPic(id,tv_photos);
        }
        cursor.close();
        btn_delete.setOnClickListener(DetailActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (view.getId() == R.id.btn_delete) {
            deleteData(id);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    private void queryTitle() {
        Cursor cursor1= mDatabase.rawQuery("select count(2) from "+DBHelper.TABLE_NAME,null);
        cursor1.moveToFirst();
        long count = cursor1.getLong(0);
        int num=(int) count;
        idlist=new int[num];
        cursor1.close();
        Cursor cursor;
        cursor = mDatabase.query(DBHelper.TABLE_NAME,DBHelper.TABLE_COLUMNS,null,null,null,null,null);
        int i=0;
        while (cursor != null && cursor.moveToNext()) {
            idlist[i]=cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
            i+=1;
        }
        cursor.close();
    }

    private void deleteData(int id) {
        mDatabase.delete(DBHelper.TABLE_NAME,"id = ?",new String[]{id+""});
    }

    protected void SavePic(int id,ImageView imageView){
        SharedPreferences sharedPreferences = getSharedPreferences("image_file",MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable)imageView.getDrawable()).getBitmap()
                .compress(Bitmap.CompressFormat.JPEG,50,stream);
        String imageBase64 = new String(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SaveImage"+id,imageBase64);
        editor.commit();
    }
    protected void ShowPic(int id,ImageView imageView){
        SharedPreferences sharedPreferences = getSharedPreferences("image_file",MODE_PRIVATE);

        String imageBase64 = sharedPreferences.getString("SaveImage"+id,"");
        byte[] base64byte = Base64.decode(imageBase64,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(base64byte,0,base64byte.length);
        imageView.setImageBitmap(bitmap);

    }
    private int lastid( SQLiteDatabase db){
        String sql = "select last_insert_rowid() from daily" ;
        Cursor cursor = db.rawQuery(sql, null);
        int a = -1;
        if(cursor.moveToFirst()){
            a = cursor.getInt(0);
        }
        return a;

    }

}
