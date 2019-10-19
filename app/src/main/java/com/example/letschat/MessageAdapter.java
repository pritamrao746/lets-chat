package com.example.letschat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> mMessageList;
    Context context;
    String  mCurrentUser;




    public MessageAdapter(Context context,List<Message> mMessageList) {
        this.mMessageList = mMessageList;
        this.context= context;

    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mCurrentUser =FirebaseAuth.getInstance().getCurrentUser().getUid();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        final String message = mMessageList.get(position).getMessage();

        String messageTime = TimeAgo.hhMM(mMessageList.get(position).getTime());

        String seenStatus = mMessageList.get(position).getSeen();


        if (mMessageList.get(position).getSender().equals(mCurrentUser)) {   ///current user's message

            holder.messageLinearLayout.setGravity(5);   ////right messsage
            holder.message.setBackgroundColor(Color.WHITE);
            holder.message.setTextColor(Color.BLACK);

        } else {
            holder.messageLinearLayout.setGravity(3);                ///other peoples mesage
            int color = ContextCompat.getColor(context,R.color.colorAccent);
            holder.message.setBackgroundColor(color);
            holder.message.setTextColor(Color.WHITE);
        }

        if (mMessageList.get(position).getType().equals("text")) {



            holder.message.setText(message + "\n"+seenStatus+"  " + messageTime);
            holder.messageImage.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);


        }
        else if(mMessageList.get(position).getType().equals("image")){
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.INVISIBLE);

            Picasso.with(context).load(message)
                    .placeholder(R.drawable.images).into(holder.messageImage);

        }

        else if (mMessageList.get(position).getType().equals("location")) {

            holder.messageImage.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.INVISIBLE);
            holder.messageImage.setImageResource(R.drawable.mapdefault);

            holder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLocation(message);

                }
            });

        }
        else {                          /////for pdf and doc file
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.INVISIBLE);
            holder.messageImage.setImageResource(R.drawable.pdf);
            holder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse(message));

                    context.startActivity(webIntent);
                    Log.i("onclick", message);

                }
            });

        }
    }





    void showLocation(String message){


        Intent intent = new Intent(context,ShareLocationActivity.class);
        intent.putExtra("location",message);
        context.startActivity(intent);
        

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView message;
        RelativeLayout messageLinearLayout;
        ImageView messageImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            v=itemView;
            message=(itemView).findViewById(R.id.single_message_text_view);
            messageLinearLayout=(itemView).findViewById(R.id.single_message_linear_layout);
            messageImage=(itemView).findViewById(R.id.single_message_image);





        }
    }

}
