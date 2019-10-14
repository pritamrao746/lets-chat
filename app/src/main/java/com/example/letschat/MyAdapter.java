package com.example.letschat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{


    Context context;
    ArrayList<UserProfile> userList;
    String callBody="";


    public MyAdapter(Context context, ArrayList<UserProfile> userList ) {      ///constructor to get contex and user list
        this.context = context;
        this.userList = userList;
    }

    public MyAdapter(Context context, ArrayList<UserProfile> userList ,String callBody) {      ///constructor to get contex and user list
        this.context = context;
        this.userList = userList;
        this.callBody=callBody;



    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.single_user,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(userList.get(position).getUser_name());
        holder.status.setText(userList.get(position).getStatus());
        Log.i("mkg","  binding  "+userList.get(position).getUser_name());


        Picasso.with(context).load(userList.get(position).getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.images).into(holder.profileImage, new Callback() {
            @Override
            public void onSuccess() {

                //if data retrieved via offline don't do anything

            }

            @Override
            public void onError() {

                Picasso.with(context).load(userList.get(position).getThumb_image())
                        .placeholder(R.drawable.images).into(holder.profileImage);


            }
        });


        if(callBody.equals("friendFregment")||callBody.equals("chatFregment")){        ///ifff called from friend fregment




            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   Intent chatIntent =new Intent(context,ChatActivity.class);
                   chatIntent.putExtra("userId",userList.get(position).getUid());
                   context.startActivity(chatIntent);

                }
            });

        }
        else{
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Log.e("i should ","not be here");

                    String id = userList.get(position).getUid();

                    Intent profileIntent = new Intent(context, UserProfilePage.class);
                    profileIntent.putExtra("userId", id);
                    context.startActivity(profileIntent);
                    //In adapter class you don't know the context hence you use context.startActivity
                }

            });


        }




    }





    @Override
    public int getItemCount() {
        return userList.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        TextView name ;
        TextView status;
        CircleImageView profileImage;
        ConstraintLayout parentLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name =(TextView)itemView.findViewById(R.id.singleUserDisplayName);
            status =(TextView)itemView.findViewById(R.id.singleUserStatus);
            profileImage =(CircleImageView)itemView.findViewById(R.id.singleUserDisplaImage);

            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.single_message_linear_layout);

        }
    }
}