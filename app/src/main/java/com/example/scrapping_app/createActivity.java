package com.example.scrapping_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.scrapping_app.Screen.Models.ProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class createActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    StorageReference mStorage;
    StorageTask uploadTask;
    Uri imageUri;
    FloatingActionButton addProductBtn;
    ImageView backBtn;
    // Dialog Components
    Dialog loaddialog;
    CircleImageView image;

    GridView gridView;
    ImageButton imageadd;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    TextInputLayout pName, pDescription, pPrice;
    TextInputEditText pNameEditText, pDescriptionEditText, pPriceEditText;
    Button cancelBtn, saveChangesBtn;
    TextView imageErrTextView, title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
//        userId = preferences.getString("userId",null);
        mStorage = FirebaseStorage.getInstance().getReference();
        addProductBtn=findViewById(R.id.addProductBtn);
        backBtn=findViewById(R.id.backBtn);
        gridView=findViewById(R.id.gridView);

//        MainActivity.myRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    String roleCheck = snapshot.child("role").getValue().toString().trim();
//                    if(roleCheck.equals("user")){
//                        addProductBtn.setVisibility(View.GONE);
//                    } else if(roleCheck.equals("admin")){
//                        addProductBtn.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

       backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {createActivity.super.onBackPressed();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productForm("add","");
            }
        });
        MainActivity.myRef.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        ProductModel model = new ProductModel(ds.getKey(),
                                ds.child("pName").getValue().toString(),
                                ds.child("pPrice").getValue().toString(),
                                ds.child("pImage").getValue().toString(),
                                ds.child("pDesc").getValue().toString(),
                                ds.child("status").getValue().toString()
                        );
                        datalist.add(model);
                    }
                    Collections.reverse(datalist);
                    MyAdapter adapter = new MyAdapter(createActivity.this,datalist);
                    gridView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void productForm(String purpose, String productId){
        loaddialog = new Dialog(createActivity.this);
        loaddialog.setContentView(R.layout.dialog_add_product);
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        loaddialog.getWindow().setGravity(Gravity.CENTER);
        loaddialog.setCancelable(false);
        loaddialog.setCanceledOnTouchOutside(false);
        image = loaddialog.findViewById(R.id.image);
        imageadd = loaddialog.findViewById(R.id.imageadd);
        pName = loaddialog.findViewById(R.id.pName);
        pDescription = loaddialog.findViewById(R.id.pDescription);
        pPrice = loaddialog.findViewById(R.id.pPrice);
        pNameEditText = loaddialog.findViewById(R.id.pNameEditText);
        pDescriptionEditText = loaddialog.findViewById(R.id.pDescriptionEditText);
        pPriceEditText = loaddialog.findViewById(R.id.pPriceEditText);
        cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
        saveChangesBtn = loaddialog.findViewById(R.id.saveChangesBtn);
        imageErrTextView = loaddialog.findViewById(R.id.imageErrTextView);
        title = loaddialog.findViewById(R.id.title);
        imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 420);
            }
        });
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(createActivity.this, "Image Upload In Process!!!", Toast.LENGTH_SHORT).show();
                } else {
                    if(purpose.equals("add")){
                        validation("false",purpose, productId);
                    } else if(purpose.equals("edit")){
                        validation("true",purpose, productId);
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaddialog.dismiss();
            }
        });
        pNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pnameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pdescValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ppriceValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        if(purpose.equals("edit")){
            title.setText("Edit Product");
            MainActivity.myRef.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Glide.with(createActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(image);
                        pNameEditText.setText(snapshot.child("pName").getValue().toString().trim());
                        pDescriptionEditText.setText(snapshot.child("pDesc").getValue().toString().trim());
                        pPriceEditText.setText(snapshot.child("pPrice").getValue().toString().trim());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        loaddialog.show();
    }

    public boolean pnameValidation(){
        String name = pNameEditText.getText().toString().trim();
        if(name.isEmpty()){
            pName.setError("Name is Required!!!");
            return false;
        } else if(!Pattern.compile("^[a-zA-Z\\s]*$").matcher(name).matches()){
            pName.setError("Name In Only Text!!!");
            return false;
        } else {
            pName.setError(null);
            return true;
        }
    }
    public boolean pdescValidation(){
        String name = pDescriptionEditText.getText().toString().trim();
        if(name.isEmpty()){
            pDescription.setError("Description is Required!!!");
            return false;
        } else {
            pDescription.setError(null);
            return true;
        }
    }
    public boolean ppriceValidation(){
        String contact = pPriceEditText.getText().toString().trim();
        if(contact.isEmpty()){
            pPrice.setError("Price is Required!!!");
            return false;
        } else {
            pPrice.setError(null);
            return true;
        }
    }
    public boolean imageValidation(){
        if(imageUri == null){
            imageErrTextView.setText("Product Image is Required!!!");
            imageErrTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            imageErrTextView.setText("");
            imageErrTextView.setVisibility(View.GONE);
            return true;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 420 && resultCode == RESULT_OK){
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void validation(String imageStatus, String purpose, String productId) {
        boolean imageErr = false, pnameErr = false, pdescErr = false, ppriceErr = false;
        pnameErr = pnameValidation();
        pdescErr = pdescValidation();
        ppriceErr = ppriceValidation();
        if(imageStatus.equals("true")){
            imageErr = true;
        } else {
            imageErr = imageValidation();
        }
        if((pnameErr && pdescErr && ppriceErr && imageErr) == true){
            product(purpose, productId);
        }
    }
    private void product(String purpose, String productId) {
        if(MainActivity.connectionCheck(createActivity.this)){
            if(imageUri != null){
                Dialog loading = new Dialog(createActivity.this);
                loading.setContentView(R.layout.dialog_loading);
                loading.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loading.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                loading.getWindow().setGravity(Gravity.CENTER);
                loading.setCancelable(false);
                loading.setCanceledOnTouchOutside(false);
                TextView message = loading.findViewById(R.id.message);
                if(purpose.equals("edit")){
                    message.setText("Modifying...");
                } else {
                    message.setText("Creating...");
                }
                loading.show();
                uploadTask = mStorage.child("Images/"+System.currentTimeMillis()+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                loading.dismiss();
                                String photoLink = uri.toString();

                                Dialog alertdialog = new Dialog(createActivity.this);
                                alertdialog.setContentView(R.layout.dialog_success);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView message = alertdialog.findViewById(R.id.message);
                                alertdialog.show();

                                if(purpose.equals("add")){
                                    HashMap<String, String> mydata = new HashMap<String, String>();
                                    mydata.put("pImage", "" + photoLink);
                                    mydata.put("pName", pNameEditText.getText().toString().trim());
                                    mydata.put("pDesc", pDescriptionEditText.getText().toString().trim());

                                    mydata.put("pPrice", pPriceEditText.getText().toString().trim());

                                    mydata.put("status", "1");
                                    MainActivity.myRef.child("Products").push().setValue(mydata);
                                    message.setText("Product Added Successfully!!!");
                                } else if(purpose.equals("edit")){
                                    MainActivity.myRef.child("Products").child(productId).child("pImage").setValue(photoLink);
                                    MainActivity.myRef.child("Products").child(productId).child("pName").setValue(pNameEditText.getText().toString().trim());
                                    MainActivity.myRef.child("Products").child(productId).child("pDesc").setValue(pDescriptionEditText.getText().toString().trim());
                                    MainActivity.myRef.child("Products").child(productId).child("pPrice").setValue(pPriceEditText.getText().toString().trim());
                                    message.setText("Product Edit Successfully!!!");

                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                        loaddialog.dismiss();
                                    }
                                },2000);

                            }
                        });
                    }
                });
            } else {
                Dialog alertdialog = new Dialog(createActivity.this);
                alertdialog.setContentView(R.layout.dialog_success);
                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                alertdialog.getWindow().setGravity(Gravity.CENTER);
                alertdialog.setCancelable(false);
                alertdialog.setCanceledOnTouchOutside(false);
                TextView message = alertdialog.findViewById(R.id.message);
                message.setText("Product Edit Successfully!!!");
                alertdialog.show();

                if(purpose.equals("edit")){
                    MainActivity.myRef.child("Products").child(productId).child("pName").setValue(pNameEditText.getText().toString().trim());
                    MainActivity.myRef.child("Products").child(productId).child("pDesc").setValue(pDescriptionEditText.getText().toString().trim());
                    MainActivity.myRef.child("Products").child(productId).child("pPrice").setValue(pPriceEditText.getText().toString().trim());
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertdialog.dismiss();
                        loaddialog.dismiss();
                    }
                },2000);
            }
        }
    }

    public class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<ProductModel> data;

        public MyAdapter(Context context, ArrayList<ProductModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            // Count of Adapter
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // Declare Product Layout
            View productItem = LayoutInflater.from(context).inflate(R.layout.product_listview,null);
            ImageView pImage, wishlistBtn, editBtn, deleteBtn;
            TextView pDiscount, pName, pRating, pStock, pPrice, pPriceOff;
            LinearLayout options, item;
            pImage = productItem.findViewById(R.id.pImage);
            wishlistBtn = productItem.findViewById(R.id.wishlistBtn);
            editBtn = productItem.findViewById(R.id.editBtn);
            deleteBtn = productItem.findViewById(R.id.deleteBtn);
            pName = productItem.findViewById(R.id.pName);
            pRating = productItem.findViewById(R.id.pRating);
            pPrice = productItem.findViewById(R.id.pPrice);
            pPriceOff = productItem.findViewById(R.id.pPriceOff);
            options = productItem.findViewById(R.id.options);
            item = productItem.findViewById(R.id.item);


            pName.setText(data.get(i).getpName());
            pPriceOff.setText("$"+data.get(i).getpPrice());
            Glide.with(context).load(data.get(i).getpImage()).into(pImage);

            double discount = Double.parseDouble("0")/100;
            double calcDiscount = Double.parseDouble(data.get(i).getpPrice()) * discount;
            double totalPrice = Double.parseDouble(data.get(i).getpPrice()) - calcDiscount;
            pPrice.setText("$"+Math.round(totalPrice));

            MainActivity.myRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String roleCheck = snapshot.child("role").getValue().toString().trim();
                        if(roleCheck.equals("user")){
                            options.setVisibility(View.GONE);
                        } else if(roleCheck.equals("admin")){
                            options.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog loaddialog = new Dialog(context);
                    loaddialog.setContentView(R.layout.dialog_confirm);
                    loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loaddialog.getWindow().setGravity(Gravity.CENTER);
                    loaddialog.setCancelable(false);
                    loaddialog.setCanceledOnTouchOutside(false);
                    Button cancelBtn, yesBtn;
                    yesBtn = loaddialog.findViewById(R.id.yesBtn);
                    cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loaddialog.dismiss();
                        }
                    });
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(MainActivity.connectionCheck(context)){
                                loaddialog.dismiss();
                                MainActivity.myRef.child("Products").child(data.get(i).getId()).removeValue();
                            }
                        }
                    });

                    loaddialog.show();
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productForm("edit",""+data.get(i).getId());
                }
            });

            return productItem;
        }
    }
}
