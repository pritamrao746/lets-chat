package com.example.letschat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {


    Context context;
    ArrayList<ChatUserProfie> userList;

    private DatabaseReference mUserRef;
    String mCurrentUser;



    public ChatAdapter(Context context, ArrayList<ChatUserProfie> userList) {

        this.context = context;
        this.userList = userList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mUserRef= FirebaseDatabase.getInstance().getReference().child("users");
        mCurrentUser= FirebaseAuth.getInstance().getUid();

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.single_user,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final String chatUser=userList.get(position).getUserId();       //opossing user
        String lastMessage=userList.get(position).getLast_message();


        holder.lastMessage.setText(lastMessage);


     mUserRef.child(chatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileImageUri=dataSnapshot.child("thumb_image").getValue().toString();
                String name=dataSnapshot.child("user_name").getValue().toString();
                Picasso.with(context).load(profileImageUri)
                        .placeholder(R.drawable.images).into(holder.profileImage);
                holder.name.setText(name);
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profileIntent = new Intent(context, UserProfilePage.class);
                profileIntent.putExtra("userId", chatUser);
                context.startActivity(profileIntent);

            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chatIntent =new Intent(context,ChatActivity.class);
                chatIntent.putExtra("userId",chatUser);
                context.startActivity(chatIntent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
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
