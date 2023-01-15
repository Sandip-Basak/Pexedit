package com.example.pexedit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final int gallery_request_code = 200;
    private final int camera_request_code = 100;
    private final int camera_permission_code = 1001;
    ImageView upload, upload_cam, upload_gal;
    Button edit;
    Uri GalleryImage;
    Bitmap CameraImage;
    Dialog dialog;
    public static Uri finalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog=new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        upload_cam=dialog.findViewById(R.id.upload_cam);
        upload_gal=dialog.findViewById(R.id.upload_gal);

        upload=findViewById(R.id.upload);
        edit=findViewById(R.id.edit);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalImage==null) {
                    dialog.show();
                }
                else{
                    Intent activity = new Intent(MainActivity.this, FinalActivity.class);
                    startActivity(activity);
                }
            }
        });

        upload_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, camera_permission_code);
                    }
                    else{
                        getCameraImage();
                    }
                }
            }
        });

        upload_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGalleryImage();
            }
        });

    }
    void getGalleryImage(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), gallery_request_code);

    }

    void getCameraImage(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, camera_request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == gallery_request_code){
                GalleryImage = data.getData();
                if(GalleryImage != null){
                    upload.setImageURI(GalleryImage);
                    finalImage=GalleryImage;
                    dialog.dismiss();
                }
            }
            else if(requestCode == camera_request_code){
                CameraImage = (Bitmap) data.getExtras().get("data");
                if(CameraImage != null){
                    upload.setImageBitmap(CameraImage);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), CameraImage, "PexeditCapture", "Image");
                    finalImage = Uri.parse(path);
                    dialog.dismiss();
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == camera_permission_code){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCameraImage();
            }
            else{
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}