package in.tvac.akshaye.lapitchat;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinGroupe extends AppCompatActivity {

    DatabaseReference mGroupeDatabase;
    CircleImageView grpImg;
    TextView grpName;
    Button joinGroupeBtn;
    String btnText;
    String quite = "Quitter le groupe";
    String join = "Join groupe";

    DatabaseReference groupeUserRef,chercheGroupe;
    FirebaseAuth mAuth;
    private String myid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_groupe);

        final String grp_id = getIntent().getStringExtra("groupe_id");

        mGroupeDatabase = FirebaseDatabase.getInstance().getReference().child("Groupes").child(grp_id);
        grpImg = findViewById(R.id.joingrp_image);
        grpName = findViewById(R.id.joingrp_displayName);
        joinGroupeBtn = findViewById(R.id.joingrp_btn);
        mAuth = FirebaseAuth.getInstance();
        myid = mAuth.getCurrentUser().getUid();
        final HashMap<String, String> groupeMap = new HashMap<>();

        groupeUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(myid).child("mygroupes").child(grp_id);
        chercheGroupe = FirebaseDatabase.getInstance().getReference().child("Users").child(myid).child("mygroupes");

        chercheGroupe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(grp_id)) {
                     btnText = "Quitter le groupe".toUpperCase();
                     joinGroupeBtn.setText(btnText);

                }else{
                    joinGroupeBtn.setText(join.toUpperCase());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mGroupeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String myimg = dataSnapshot.child("image").getValue().toString();
                String myname = dataSnapshot.child("name").getValue().toString();

                groupeMap.put("name", myname);
                groupeMap.put("image", myimg);

                grpName.setText(myname);
                if(myimg.equals("default")) {
                    Picasso.with(getApplicationContext()).load(R.drawable.default_avatar).into(grpImg);
                }else{
                    Picasso.with(getApplicationContext()).load(myimg).placeholder(R.drawable.default_avatar).into(grpImg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        joinGroupeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!joinGroupeBtn.getText().equals(quite.toUpperCase())) {
                    groupeUserRef.setValue(groupeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent mainIntent = new Intent(JoinGroupe.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                                Toast.makeText(JoinGroupe.this, "Vous étes membre du groupe maintenant!!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(JoinGroupe.this, "Erreur!!,Essayer une autre fois!!", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                }else{
                    groupeUserRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent mainIntent = new Intent(JoinGroupe.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();

                                Toast.makeText(JoinGroupe.this,"Vous avez quitter le groupe",Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(JoinGroupe.this,"Erreur!!,Essayer une autre fois!!",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });


    }
}
