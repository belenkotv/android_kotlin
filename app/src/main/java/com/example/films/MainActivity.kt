package com.example.films

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.films.R

object Object {
    var data: Data = Data("aaa", "bbb").copy(prop1 = "ccc")
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            button.setText("Clicked")
        }
        var data: Data = Data("123", "456");
        var text: TextView = findViewById(R.id.text)
        text.setText(data.toString() + "\n" + Object.data.toString())
    }
}