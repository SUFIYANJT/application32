package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.ItemModel;
import com.example.myapplication.CustomAdapter;

import java.util.ArrayList;
import java.util.List;

public class newmainuser extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmainuser);

        // Create dummy data
        List<ItemModel> itemList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ItemModel itemModel = new ItemModel();
            itemModel.setItemName("item " + i);
            itemList.add(itemModel);
        }

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewuser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pass true for isInspecterReview and false for isPendingActivity
        CustomAdapter customAdapter = new CustomAdapter(itemList, false, false);
        recyclerView.setAdapter(customAdapter);

        // Example: call showMenu() when a button is clicked
        findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

        // Set click listener for MaterialCardView
        findViewById(R.id.cardViewUSER).setOnClickListener(this);
    }

    private void showMenu(View view) {
        try {
            PopupMenu popupMenu = new PopupMenu(newmainuser.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menu_settings) {
                        Toast.makeText(newmainuser.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (item.getItemId() == R.id.menu_logout) {
                        Intent intent = new Intent(newmainuser.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(newmainuser.this, "Logout", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(newmainuser.this, "Error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cardViewUSER) {
            // Open new activity when MaterialCardView is clicked
            Intent intent = new Intent(this, UsercardWindow.class);
            startActivity(intent);
        }
    }
}
