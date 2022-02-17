package com.alisaidsaritemur.animalsomething;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Animal implements Serializable
{
    int id;

    public Animal(int id, String name, String explanation, Bitmap image) {
        this.id = id;
        this.name = name;
        this.explanation = explanation;
        this.image = image;
    }

    String name;
    String explanation;
    Bitmap image;

}
