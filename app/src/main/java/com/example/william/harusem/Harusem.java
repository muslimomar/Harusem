package com.example.william.harusem;

import android.app.Application;

import com.example.william.harusem.util.Extras;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import static com.example.william.harusem.util.Extras.CONNECTION_STATUS;

/**
 * Created by william on 5/29/2018.
 */

public class Harusem extends Application {

    private DatabaseReference mCurrentUserDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {

            mCurrentUserDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("users").child(mAuth.getCurrentUser().getUid());

            mCurrentUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {
                        mCurrentUserDatabase.child(CONNECTION_STATUS).onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
