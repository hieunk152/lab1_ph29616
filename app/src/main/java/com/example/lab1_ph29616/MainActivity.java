package com.example.lab1_ph29616;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lab1_ph29616.Adapter.CartAdapter;
import com.example.lab1_ph29616.DAO.CartDao;
import com.example.lab1_ph29616.DAO.ProductDao;
import com.example.lab1_ph29616.DTO.CatDTO;
import com.example.lab1_ph29616.DTO.Product;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CartDao catDAO;
    ArrayList<CatDTO> listCat;
    ListView lvCat;
    Button btnAdd, btnUpdate, btnDelete, btnProduct;
    EditText edCatName;
    CatDTO objCurrentCat = null;
    CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Anh xa view
        lvCat = findViewById(R.id.lv_cat);
        btnAdd = findViewById(R.id.btn_add);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnProduct = findViewById(R.id.btn_pass_product_activity);
        edCatName = findViewById(R.id.ed_catname);
        // Tao doi tuong
        catDAO = new CartDao(this);
        listCat = catDAO.getList();
        ArrayList<Integer> id_categoryFromTbProduct = catDAO.getAllProductCategoryIds();
        reload();

        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Product_Activity.class);
                startActivity(intent);
            }
        });
        // ghi vao CSDL
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String catName = edCatName.getText().toString();
                if (catName.length() < 3) {
                    Toast.makeText(MainActivity.this, "Tên cần nhập ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }
                CatDTO objCat = new CatDTO();
                objCat.setName(catName);
                //ghi vao CSDL
                int res = catDAO.AddRow(objCat);
                //cap nhat ds
                if (res > 0) {
                    listCat.clear();
                    listCat.addAll(catDAO.getList());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi thêm , hãy kiểm tra trùng lặp", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lvCat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                edCatName.setText(listCat.get(position).getName());
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objCurrentCat = listCat.get(position);

                        //lay du lieu
                        String catName = edCatName.getText().toString();
                        //validate
                        objCurrentCat.setName(catName);
                        //ghi vao csdl
                        boolean res = catDAO.updateRow(objCurrentCat.getId(), objCurrentCat.getName());
                        if (res) {
                            listCat.clear();
                            listCat.addAll(catDAO.getList());
                            adapter.notifyDataSetChanged();
                            edCatName.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, "Lỗi không sửa được , có thể trùng dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Xóa mục")
                                .setMessage("Bạn có chắc chắn muốn xóa mục này không?")
                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        boolean found = false;
                                        for (int id_cat:id_categoryFromTbProduct) {
                                            Log.d("zzz", "id_tb_cat: "+listCat.get(position).getId() +" and "+ "id_tb_product "+id_cat);
                                            if(listCat.get(position).getId() == id_cat){
                                                Toast.makeText(MainActivity.this, "Danh muc đang thuộc một sản phẩm, Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                                                found = true;
                                                break;
                                            }
                                        }
                                        if(!found){
                                            boolean res = catDAO.deleteRow(listCat.get(position).getId());
                                            if (res) {
                                                listCat.remove(position);
                                                adapter.notifyDataSetChanged();
                                                edCatName.setText("");
                                            } else {
                                                Toast.makeText(MainActivity.this, "Lỗi không xóa được, có thể trùng dữ liệu", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                })
                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert) // Thêm icon nếu cần
                                .show();
                    }
                });
                return true;
            }
        });
    }
    public void reload(){
        adapter = new CartAdapter(this, listCat);
        lvCat.setAdapter(adapter);
    }
}