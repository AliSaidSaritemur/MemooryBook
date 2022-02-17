package com.alisaidsaritemur.animalsomething;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.alisaidsaritemur.animalsomething.databinding.ActivityAnimalDetailsBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AnimalDetails extends AppCompatActivity {
    SQLiteDatabase database;
    private ActivityAnimalDetailsBinding binding;
    Bitmap selectedImage;
    ActivityResultLauncher<Intent> imageActivityResult;
    ActivityResultLauncher<String> permissonActivityResult;
    Animal animal;
    int animalId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();

        openAnimal();

    }
public void openAnimal(){

        Intent intent = getIntent();
        if(intent.getStringExtra("konum").matches("adaptor")){
            database = this.openOrCreateDatabase("Animals", MODE_PRIVATE, null);
          animalId=intent.getIntExtra("animal",16);
            binding.button2.setVisibility(View.INVISIBLE);
            binding.button2.setClickable(false);
            binding.delete.setVisibility(View.VISIBLE);
            binding.delete.setClickable(true);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM animals WHERE id = ?", new String[] {String.valueOf(animalId)});
                int nameIx = cursor.getColumnIndex("name");
                int explanationIx = cursor.getColumnIndex("explanation");
                int imageIx = cursor.getColumnIndex("image");
                while (cursor.moveToNext()) {

                    binding.name.setText(cursor.getString(nameIx));
                    binding.expalanationText.setText(cursor.getString(explanationIx));
                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView2.setImageBitmap(bitmap);

                }
                    cursor.close();

            }
            catch (Exception e) {
                e.printStackTrace();
            }



        }
}
    public void changeImage(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Snackbar.make(view, "You should give permision aboutgallery things", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Give permisson", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissonActivityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                permissonActivityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }


        } else {
            Intent intentToGallery =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageActivityResult.launch(intentToGallery);
        }

    }


public void registerLauncher(){

imageActivityResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
    @Override
    public void onActivityResult(ActivityResult result) {

        if(result.getResultCode()==RESULT_OK){

            Intent intentFromResult =result.getData();
            if(intentFromResult !=null){
                Uri imageData =intentFromResult.getData();
                try {
if(Build.VERSION.SDK_INT >= 28){

    ImageDecoder.Source sourceAnimal =ImageDecoder.createSource(getContentResolver(),imageData);
    selectedImage =ImageDecoder.decodeBitmap(sourceAnimal);
    binding.imageView2.setImageBitmap(selectedImage);
}
                  else{
                      selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),imageData);
    binding.imageView2.setImageBitmap(selectedImage);
}
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
});




        permissonActivityResult =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){

                    Intent intentToGallery =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             imageActivityResult.launch(intentToGallery);
                }

                else{

                    Toast.makeText(AnimalDetails.this, "Why ???????", Toast.LENGTH_LONG).show();
                }
            }
        });
}


public void save(View view){

        String name =binding.name.getText().toString();
        String explanation=binding.expalanationText.getText().toString();
    Bitmap smallImage =makeSmallerBitmap(selectedImage,500);

    ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
    smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
    byte[] bytesImage =outputStream.toByteArray();

    try {

        database = this.openOrCreateDatabase("Animals", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS animals(id INTEGER PRIMARY KEY,name VARCHAR,explanation VARCHAR,image BLOB)");
        String sqlString = "INSERT INTO animals (name,  explanation, image) VALUES (?, ?, ?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1, name);
        sqLiteStatement.bindString(2, explanation);
        sqLiteStatement.bindBlob(3, bytesImage);
        sqLiteStatement.execute();



    } catch (Exception e) {
        e.printStackTrace();
    }


    Intent toMAin =new Intent(this,MainActivity.class);
    toMAin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(toMAin);

}

public Bitmap makeSmallerBitmap (Bitmap image,int maximumSize){

        int widhtImage =image.getWidth();
        int heightImage=image.getHeight();
        float  ratio=(float)widhtImage / (float)heightImage;


if(widhtImage>=heightImage){

     heightImage = (int)(maximumSize /  ratio);
    return image.createScaledBitmap(image,maximumSize,heightImage,true);
}

    else{
        widhtImage = (int)(maximumSize *  ratio);

    return image.createScaledBitmap(image,widhtImage,maximumSize,true);
    }





}

public void delete(View view){

        AlertDialog.Builder alert =new AlertDialog.Builder(AnimalDetails.this);
alert.setTitle("Delete");
alert.setMessage("Do you want to delete this souvenir ?");
alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        database = openOrCreateDatabase("Animals", MODE_PRIVATE, null);
        try { String sqlString = "DELETE FROM animals WHERE id =?";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindDouble(1, animalId);
            sqLiteStatement.execute();

        }

        catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(AnimalDetails.this, "Souvenir was deleted :/", Toast.LENGTH_SHORT).show();
        Intent toMAin =new Intent(AnimalDetails.this,MainActivity.class);
        toMAin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMAin);
    }
});

    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    });

alert.show();

}


}