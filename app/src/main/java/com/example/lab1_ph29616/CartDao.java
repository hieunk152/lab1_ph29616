package com.example.lab1_ph29616;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



import java.util.ArrayList;

public class CartDao  {
    MyDBHelper dbHelper;
    SQLiteDatabase db;

    public CartDao(Context context){
        dbHelper = new MyDBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // ham them du lieu
    public int AddRow(CatDTO objCat){
        ContentValues v = new ContentValues();
        v.put("name" , objCat.getName());
        int kq = (int) db.insert("tb_cat" , null , v);
        return kq;
    }

    // Phương thức để cập nhật một danh mục theo id
    public boolean updateRow(int id, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        int rowsAffected = db.update("tb_cat", contentValues, "id = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0; // Trả về true nếu thành công
    }

    // Phương thức để xóa một danh mục theo id
    public boolean deleteRow(int id) {
        int rowsAffected = db.delete("tb_cat", "id = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0; // Trả về true nếu thành công
    }


    // ham lay danh sach
    public ArrayList<CatDTO> getList(){
        ArrayList<CatDTO> listCat = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM tb_cat ORDER BY id ASC" , null);
        if (c != null && c.getCount()>0){
            c.moveToFirst();
            //duyet vong lap
            //thu tu id 0 , name 1
            do {
                CatDTO objCat = new CatDTO();
                objCat.setId(c.getInt(0));
                objCat.setName(c.getString(1));
                listCat.add(objCat);
            }while (c.moveToNext());
        }else {
            Log.d("zzzzzz", "CatDAO::getList: Khong lay duoc du lieu ");
        }
        return listCat;
    }
    // Phương thức để lấy một danh mục theo id
    public CatDTO getCat(int id) {
        Cursor cursor = db.query("tb_cat", new String[]{"id", "name"}, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int categoryId = cursor.getInt(1);
            String categoryName = cursor.getString(2);
            cursor.close();
            db.close();
            return new CatDTO(categoryId, categoryName);
        }

        // Đóng cursor và database
        if (cursor != null) cursor.close();
        db.close();
        return null; // Trả về null nếu không tìm thấy
    }
}
