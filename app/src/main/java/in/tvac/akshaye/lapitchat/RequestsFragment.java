package in.tvac.akshaye.lapitchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.text.PrecomputedTextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.PrecomputedText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View RequestfragmentView;
    private RecyclerView myRequestsList;
    private DatabaseReference chatRequestRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private DatabaseReference mRootRef;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        RequestfragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        myRequestsList = RequestfragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        myRequestsList.setHasFixedSize(true);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));



        return RequestfragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users, RequestsFragment.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, RequestsFragment.UsersViewHolder>(

                Users.class,
                R.layout.single_request,
                RequestsFragment.UsersViewHolder.class,
                chatRequestRef.child(currentUserId)
        ) {
            @Override
            protected void populateViewHolder(final RequestsFragment.UsersViewHolder usersViewHolder, final Users users, final int position) {

                final String list_user_id = getRef(position).getKey();

                final DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();
                            if(type.equals("received")){
                                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("image")) {
                                            final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                            final String requestUserstatus = dataSnapshot.child("status").getValue().toString();
                                            final String requestUserimage = dataSnapshot.child("image").getValue().toString();

                                            usersViewHolder.setDisplayName(requestUserName);
                                            usersViewHolder.setUserStatus(requestUserstatus);
                                            usersViewHolder.setUserImage(requestUserimage, getContext());
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }else if(type.equals("sent")){
                                usersViewHolder.mView.getLayoutParams().height = 0;
                                //getRef(position).removeValue();
                                usersViewHolder.itemView.setVisibility(View.GONE);
                                //usersViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                usersViewHolder.btnRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map unfriendMap = new HashMap();

                        unfriendMap.put("Friend_req/" + currentUserId + "/" + list_user_id, null);
                        unfriendMap.put("Friend_req/" + list_user_id + "/" + currentUserId, null);

                        mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){
                                    String error = databaseError.getMessage();

                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

                usersViewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                        Map friendsMap = new HashMap();
                        friendsMap.put("Friends/" + currentUserId + "/" + list_user_id + "/date", currentDate);
                        friendsMap.put("Friends/" + list_user_id + "/"  + currentUserId + "/date", currentDate);

                        friendsMap.put("Friend_req/" + currentUserId + "/" + list_user_id, null);
                        friendsMap.put("Friend_req/" + list_user_id + "/" + currentUserId, null);

                        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if(databaseError != null){

                                    String error = databaseError.getMessage();

                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });



            }
        };


        myRequestsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button btnAccept,btnRefuse;


        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            btnAccept = itemView.findViewById(R.id.request_accept_btn);
            btnRefuse = itemView.findViewById(R.id.request_refuse_btn);

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name1);
            userNameView.setText(name);

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status1);
            userStatusView.setText(status);


        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image1);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }




    }


}
