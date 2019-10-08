package com.example.letschat;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
public class RequestFragment extends Fragment {


    private DatabaseReference mRootref ;
    private DatabaseReference mReqestRef;
    private DatabaseReference mUserRef;
    private RecyclerView mReqestRecyclerView;
    private MyAdapter adapter;
    private ArrayList<UserProfile> mReqestUserList;
    private String mCurrentUser;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View mView =inflater.inflate(R.layout.fragment_request, container, false);

        mCurrentUser= FirebaseAuth.getInstance().getUid();
        mRootref= FirebaseDatabase.getInstance().getReference();
        mReqestRef =mRootref.child("friend_requests").child(mCurrentUser);
        mUserRef=mRootref.child("users");


        mReqestUserList=new ArrayList<UserProfile>();

        mReqestRecyclerView =(mView).findViewById(R.id.request_fregment_recycler_view);
        mReqestRecyclerView.setHasFixedSize(true);
        mReqestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new MyAdapter(getContext(),mReqestUserList,"reqestFregment");
        mReqestRecyclerView.setAdapter(adapter);



        mReqestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d: dataSnapshot.getChildren()){

                    if((d.child("request_type").getValue()).equals("received")){

                        String reQuestUser=d.getKey();
                        mUserRef.child(reQuestUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserProfile  p= dataSnapshot.getValue(UserProfile.class);
                                mReqestUserList.add(p);
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




                    }

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        return mView;
    }

}
