package in.tvac.akshaye.lapitchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllGroupes extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_groupes);

        mToolbar = (Toolbar) findViewById(R.id.allgrp_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Groupes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Groupes");

        mLayoutManager = new LinearLayoutManager(this);

        //Avoire
        mUsersList = (RecyclerView) findViewById(R.id.allgrp_list);
        mUsersList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Groupes, AllGroupes.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Groupes, AllGroupes.UsersViewHolder>(

                Groupes.class,
                R.layout.groupe_single_layout,
                AllGroupes.UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(AllGroupes.UsersViewHolder usersViewHolder, Groupes groupes, int position) {

                final String grp_id = getRef(position).getKey();


                usersViewHolder.setDisplayName(groupes.getName());
                usersViewHolder.setUserImage(groupes.getImage(), getApplicationContext());


                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(AllGroupes.this, JoinGroupe.class);
                        profileIntent.putExtra("groupe_id", grp_id);
                        startActivity(profileIntent);

                    }
                });

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.grp_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.grp_single_image);
            if(thumb_image.equals("default")) {
                Picasso.with(ctx).load(R.drawable.default_avatar).into(userImageView);
            }else{
                Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
            }
        }


    }
}
