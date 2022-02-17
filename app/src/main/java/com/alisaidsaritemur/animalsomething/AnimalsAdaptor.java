package com.alisaidsaritemur.animalsomething;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alisaidsaritemur.animalsomething.databinding.RecyleCowBinding;

import java.util.ArrayList;

public class AnimalsAdaptor extends RecyclerView.Adapter<AnimalsAdaptor.AnimalHolder>{
ArrayList<Animal> animals;

    public AnimalsAdaptor(ArrayList<Animal> animals) {
        this.animals = animals;
    }


    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyleCowBinding binding =RecyleCowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new AnimalHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalHolder holder, int position) {


        holder.binding.imageView.setImageBitmap(animals.get(position).image);
        holder.binding.animalName.setText(animals.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(holder.itemView.getContext(),AnimalDetails.class);
            intent.putExtra("animal",animals.get(position).id);
                intent.putExtra("konum","adaptor");
            holder.itemView.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return animals.size();
    }


    public class AnimalHolder extends RecyclerView.ViewHolder {
        private  RecyleCowBinding binding;
        public AnimalHolder(RecyleCowBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
        }
    }
}
