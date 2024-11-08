package com.example.lab1_ph29616;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1_ph29616.Adapter.ProductAdapter;
import com.example.lab1_ph29616.DAO.CartDao;
import com.example.lab1_ph29616.DAO.ProductDao;
import com.example.lab1_ph29616.DTO.CatDTO;
import com.example.lab1_ph29616.DTO.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Product_Activity extends AppCompatActivity implements ProductAdapter.AdapterCallback {
    ProductDao daoPro;
    CartDao cartDao;
    ArrayList<Product> listPro;
    ArrayList<CatDTO> listCat;
    RecyclerView recyclePro;
    FloatingActionButton btnAddFloating;
    ProductAdapter productAdapter;
    String taskAdd = "AddPro";
    String taskUpdate = "UpdatePro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclePro = findViewById(R.id.productRecyclerView);
        btnAddFloating = findViewById(R.id.fab);
        daoPro = new ProductDao(this);
        cartDao = new CartDao(this);
        listPro = daoPro.getListProduct();
        listCat = cartDao.getList();
        reload(listPro);

        //Thiết lập callback để adapter có thể gọi sự kiê khi button được click
        productAdapter.setCallback(this);

        btnAddFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductInputDialog(taskAdd,0);
            }
        });
    }
    public void reload(ArrayList<Product> list){
        productAdapter = new ProductAdapter(this,list);
        recyclePro.setAdapter(productAdapter);
    }
    public void showProductInputDialog(String task, int position) {
        // Tạo Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate layout tùy chỉnh vào Dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_data_product, null);

        // Tìm các thành phần trong layout
        final EditText etProductName = dialogView.findViewById(R.id.etProductName);
        final EditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        final Spinner spinnerProductCategory = dialogView.findViewById(R.id.spinnerProductCategory);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        // Tạo Adapter cho Spinner
        ArrayAdapter<CatDTO> catAdapter = new ArrayAdapter<CatDTO>(this, android.R.layout.simple_spinner_item, listCat) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                // Hiển thị tên của CatDTO
                view.setText(listCat.get(position).getName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                // Hiển thị tên của CatDTO
                view.setText(listCat.get(position).getName());
                return view;
            }
        };
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductCategory.setAdapter(catAdapter);

        // Thiết lập View cho Dialog
        builder.setView(dialogView);

        // Tạo AlertDialog
        final AlertDialog dialog = builder.create();
        Product productToEdit = listPro.get(position);

        // fill dữ liệu khi update
        if (task.equals(taskUpdate)) {
            // Lấy sản phẩm theo vị trí
            etProductName.setText(productToEdit.getName());
            etProductPrice.setText(String.valueOf(productToEdit.getPrice()));

            // Tìm id_cat từ sản phẩm
            int productCategoryId = productToEdit.getId_cat();

            // Tìm vị trí tương ứng của id_cat trong listCat
            int categoryPosition = -1;
            for (int i = 0; i < listCat.size(); i++) {
                if (listCat.get(i).getId() == productCategoryId) {
                    categoryPosition = i;
                    break;
                }
            }

            // Nếu tìm thấy id_cat, chọn mục trong Spinner
            if (categoryPosition != -1) {
                spinnerProductCategory.setSelection(categoryPosition);
            }
        }
        // Sự kiện khi nhấn nút "Hủy"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Đóng dialog
            }
        });

        // Sự kiện khi nhấn nút "Xác nhận"
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatDTO obj_catSpin = (CatDTO) spinnerProductCategory.getSelectedItem();
                String productName = etProductName.getText().toString();
                String productPriceStr = etProductPrice.getText().toString();

                if (!productName.isEmpty() && !productPriceStr.isEmpty()) {
                    // Chuyển giá trị giá sản phẩm từ String sang double
                    double productPrice = Double.parseDouble(productPriceStr);
                    Product productTaskinDialog = new Product(productPrice, productName, obj_catSpin.getId());

                    if (task.equals(taskAdd)) {
                        // Thêm sản phẩm mới
                        int res = daoPro.AddProduct(productTaskinDialog);
                        if (res > 0) {
                            listPro.add(productTaskinDialog);
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Product_Activity.this, "Lỗi thêm, hãy kiểm tra trùng lặp", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } else if (task.equals(taskUpdate)) {
                        // Cập nhật sản phẩm
                        boolean res = daoPro.updateProduct(productToEdit.getId(),productTaskinDialog);
                        if (res) {
                            listPro.clear();
                            listPro.addAll(daoPro.getListProduct());
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Product_Activity.this, "Lỗi sửa, hãy kiểm tra trùng lặp", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                } else {
                    // Nếu thiếu thông tin, có thể hiển thị thông báo lỗi
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Hiển thị Dialog
        dialog.show();
    }


    @Override
    public void onEditButtonClick(int position) {
        showProductInputDialog(taskUpdate,position);
    }
}
