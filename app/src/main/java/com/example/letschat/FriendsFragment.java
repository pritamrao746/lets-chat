package com.example.letschat;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    public FriendsFragment() {
        // Required empty public constructor
    }

    private MyAdapter adapter;
    private RecyclerView mFreindRecyclerView;
    private View mView;
    private DatabaseReference mFriendDatabaseRef;
    private DatabaseReference mFriendProfileRef;
    private ArrayList<UserProfile> mFriendsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_friends, container, false);

        String currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUser);
        mFriendProfileRef = FirebaseDatabase.getInstance().getReference().child("users");




        mFreindRecyclerView = mView.findViewById(R.id.friends_fregment_recycler_view);
        mFreindRecyclerView.setHasFixedSize(true);
        mFreindRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        mFriendsList =new ArrayList<UserProfile>();

       mFriendDatabaseRef.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

               String friendUser = dataSnapshot.getKey();
               DatabaseReference singleFriend =mFriendProfileRef.child(friendUser);
               singleFriend.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       UserProfile p = dataSnapshot.getValue(UserProfile.class);
                       mFriendsList.add(p);
                       Log.i("MSG",p.getUser_name());




                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });


           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });




        adapter = new MyAdapter(getContext(),mFriendsList,"friendFregment");
        mFreindRecyclerView.setAdapter(adapter);







        return mView;

    }





}