package com.example.letschat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {


    Context context;
    ArrayList<ChatUserProfie> userList;

    private DatabaseReference mUserRef;



    public ChatAdapter(Context context, ArrayList<ChatUserProfie> userList) {
        this.context = context;
        this.userList = userList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mUserRef= FirebaseDatabase.getInstance().getReference().child("users");

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.single_user,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        String chatUser=userList.get(position).getChat_id();
        String lastMessage=userList.get(position).getLast_message();

        holder.name.setText(chatUser);
        holder.lastMessage.setText(lastMessage);

        /*mUserRef.child(chatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.get(position).setThumb_image("");
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        */


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        TextView name ;
        TextView lastMessage;
        TextView LastMessageTime;

        CircleImageView profileImage;
        ConstraintLayout parentLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name =(TextView)itemView.findViewById(R.id.singleUserDisplayName);
            lastMessage =(TextView)itemView.findViewById(R.id.singleUserStatus);
            profileImage =(CircleImageView)itemView.findViewById(R.id.singleUserDisplaImage);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.single_message_linear_layout);

        }
    }



}
