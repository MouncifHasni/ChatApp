package in.tvac.akshaye.lapitchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupeFragment extends Fragment {

    private View v;
    private DatabaseReference user_groupeRef;
    FirebaseAuth mAuth;
    private RecyclerView mlistgroupe;
    private String mCurrentUser;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_groupe_fragment, container, false);

        mlistgroupe = v.findViewById(R.id.groupe_list);
        mlistgroupe.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        user_groupeRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser).child("mygroupes");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mlistgroupe.setHasFixedSize(true);
        mlistgroupe.setLayoutManager(linearLayoutManager);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Groupes, GroupeFragment.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Groupes, GroupeFragment.UsersViewHolder>(

                Groupes.class,
                R.layout.groupe_single_layout,
                GroupeFragment.UsersViewHolder.class,
                user_groupeRef
        ) {
            @Override
            protected void populateViewHolder(GroupeFragment.UsersViewHolder usersViewHolder, Groupes groupes, int position) {

                final String grp_id = getRef(position).getKey();


                usersViewHolder.setDisplayName(groupes.getName());
                usersViewHolder.setUserImage(groupes.getImage(), getContext());


                /*usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(GroupeFragment.this, JoinGroupe.class);
                        profileIntent.putExtra("groupe_id", grp_id);
                        startActivity(profileIntent);
                    }
                });*/

            }
        };

        mlistgroupe.setAdapter(firebaseRecyclerAdapter);
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
