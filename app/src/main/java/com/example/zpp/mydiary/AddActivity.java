package com.example.zpp.mydiary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.BaseMenuPresenter;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button btn_sure, btn_cancel,btn_photos;
    private EditText et_title, et_content,et_man;
    private ImageView et_photos;
    private SQLiteDatabase mDatabase;
    private int[] idlist;
    private int id;
    private String title, createtime, content, dateStr,man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mDatabase = new DBHelper(this).getWritableDatabase();
        initView();
        initEvent();
    }

    public void initView() {
        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);
        et_man = (EditText) findViewById(R.id.et_man);
        et_photos = (ImageView)findViewById(R.id.et_photos);
        btn_photos = (Button) findViewById(R.id.btn_photos);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
    }

    public void initEvent() {
        btn_photos.setOnClickListener(AddActivity.this);
        btn_sure.setOnClickListener(AddActivity.this);
        btn_cancel.setOnClickListener(AddActivity.this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_photos) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            startActivityForResult(intent, 0x1);
        }
        if (view.getId() == R.id.btn_sure) {
            Toast toast = Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG);
            toast.show();
            Date date = new Date();
            date.getTime();
            dateStr = sdf.format(date);
            title = et_title.getText().toString();
            content = et_content.getText().toString();
            man = et_man.getText().toString();

            insertData(title, dateStr, content,man);
            //跳转
            startActivity(new Intent(this, MainActivity.class));
            //跳转完以后关闭本界面
            finish();
        }
        if (view.getId() == R.id.btn_cancel) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void insertData(String title, String createtime, String content,String man) {

        ContentValues contentValues = new ContentValues();

        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        contentValues.put("title", title);
        contentValues.put("createtime", createtime);
        contentValues.put("content", content);
        contentValues.put("man", man);
        mDatabase.insertWithOnConflict(DBHelper.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        SavePic(lastid(mDatabase),et_photos);
    }

    private void queryTitle() {
        Cursor cursor1 = mDatabase.rawQuery("select count(2) from " + DBHelper.TABLE_NAME, null);
        cursor1.moveToFirst();
        long count = cursor1.getLong(0);
        int num = (int) count;
        idlist = new int[num];
        cursor1.close();
        Cursor cursor;
        cursor = mDatabase.query(DBHelper.TABLE_NAME, DBHelper.TABLE_COLUMNS, null, null, null, null, null);
        int i = 0;
        while (cursor != null && cursor.moveToNext()) {
            idlist[i] = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
            i += 1;
        }
        id=idlist[num-1];
        cursor.close();
    }
/*
    //插入图片操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }

    //加载图片
    private void showImage(String imagePath){
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ((ImageView)findViewById(R.id.image)).setImageBitmap(bm);
    }
    */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                et_photos.setImageURI(data.getData());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
