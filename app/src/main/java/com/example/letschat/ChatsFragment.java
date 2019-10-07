package com.example.letschat;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private DatabaseReference mRootRef;
    private DatabaseReference mChatRef;
    private String mCurrentUser;
    private ArrayList<UserProfile> mFriendsList;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView=inflater.inflate(R.layout.fragment_chats, container, false);

        mCurrentUser= FirebaseAuth.getInstance().getUid();
        mRootRef= FirebaseDatabase.getInstance().getReference();
        mChatRef=mRootRef.child("chats").child(mCurrentUser);

        mChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    String chatUser=d.getKey();
                    String chatUid=dataSnapshot.child(chatUser).getValue().toString();

                    Log.i("chatId",chatUid);
                    Log.i("chatUserIsHere",chatUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // Inflate the layout for this fragment
        return mView;
    }

}
