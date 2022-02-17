package com.alisaidsaritemur.animalsomething;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.alisaidsaritemur.animalsomething.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase database;
    ArrayList<Animal> animals ;
    private ActivityMainBinding binding;;
    Animal animal;
    AnimalsAdaptor animalsAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        animals =new ArrayList();

try {
         database = this.openOrCreateDatabase("Animals",MODE_PRIVATE,null);
    database.execSQL("CREATE TABLE IF NOT EXISTS animals(id INTEGER PRIMARY KEY,name VARCHAR,explanation VARCHAR,image INTEGER)");
        Cursor cursor =database.rawQuery("SELECT * FROM animals",null);
        int nameIx = cursor.getColumnIndex("name");
        int explanationIx = cursor.getColumnIndex("explanation");
        int imageIx = cursor.getColumnIndex("image");
    int idIx = cursor.getColumnIndex("id");
        String name;
        String explanation;
        byte[] bytesImage;
int id;
        while(cursor.moveToNext())
        { System.out.println(explanationIx);
            name=cursor.getString(nameIx);
            explanation=cursor.getString(explanationIx);
            id=cursor.getInt(idIx);
            bytesImage=cursor.getBlob(imageIx);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytesImage,0, bytesImage.length);
            animal =new Animal(id,name,explanation,bitmap);

            animals.add(animal);
        }
        cursor.close();}

catch (Exception e){

    e.printStackTrace();
}
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        animalsAdaptor = new AnimalsAdaptor(animals);
        binding.recyclerView.setAdapter(animalsAdaptor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.animal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addImage) {

            Intent toDetail =new Intent(this,AnimalDetails.class);
            toDetail.putExtra("konum","main");
            startActivity(toDetail);

        }
        return super.onOptionsItemSelected(item);
    }
}