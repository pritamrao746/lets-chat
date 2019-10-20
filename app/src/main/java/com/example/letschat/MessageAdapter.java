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
import android.widget.LinearLayout;
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
        String seenStatus;
        if(mMessageList.get(position).getSeen().equals("true")) {
            seenStatus = "**";
        } else {
            seenStatus="*";
        }


        holder.messageTime.setTextColor(Color.BLACK);
        holder.shareTime.setTextColor(Color.BLACK);


        if (mMessageList.get(position).getSender().equals(mCurrentUser)) {   ///current user's message

            holder.messageLinearLayout.setGravity(5);   ////right messsage
            //holder.messageLayout.setBackgroundColor(Color.WHITE);
            holder.messageLayout.setBackgroundResource(R.drawable.message_background_white);
            holder.message.setTextColor(Color.BLACK);
            holder.message.setText(message );


        } else {
            holder.messageLinearLayout.setGravity(3);                ///other peoples mesage
            holder.messageLayout.setBackgroundResource(R.drawable.message_bachground_shape);
            holder.message.setTextColor(Color.WHITE);
            holder.message.setText(message );


        }

        if (mMessageList.get(position).getType().equals("text")) {



           // holder.message.setText(message + "\n"+seenStatus+"  " + messageTime);
            holder.shareLayout.setVisibility(View.GONE);
            holder.messageLayout.setVisibility(View.VISIBLE);
            holder.messageTime.setText(messageTime);
            if(seenStatus.equals("**"))
                holder.messageSeenStatus.setBackgroundResource(R.drawable.doubletick);
            else
                holder.messageSeenStatus.setBackgroundResource(R.drawable.singletick);


        }
        else if(mMessageList.get(position).getType().equals("image")){
            holder.shareLayout.setVisibility(View.VISIBLE);
            holder.messageLayout.setVisibility(View.GONE);

            holder.shareTime.setText(messageTime);
            Picasso.with(context).load(message)
                    .placeholder(R.drawable.images).into(holder.messageImage);


            if(seenStatus.equals("**"))
                holder.shareSeenStatus.setBackgroundResource(R.drawable.doubletick);
            else
                holder.shareSeenStatus.setBackgroundResource(R.drawable.singletick);


            holder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,ShowImage.class);
                    intent.putExtra("imageUrl",message);
                    context.startActivity(intent);
                }
            });

        }

        else if (mMessageList.get(position).getType().equals("location")) {

            holder.messageLayout.setVisibility(View.GONE);
            holder.shareLayout.setVisibility(View.VISIBLE);
            holder.messageImage.setImageResource(R.drawable.map);
            holder.shareTime.setText(messageTime);


            if(seenStatus.equals("**"))
                holder.shareSeenStatus.setBackgroundResource(R.drawable.doubletick);
            else
                holder.shareSeenStatus.setBackgroundResource(R.drawable.singletick);

            holder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLocation(message);

                }
            });

        }
        else {                          /////for pdf and doc file
            holder.shareLayout.setVisibility(View.VISIBLE);
            holder.messageLayout.setVisibility(View.GONE);

            holder.messageImage.setImageResource(R.drawable.pdf);
            holder.shareTime.setText(messageTime);


            if(seenStatus.equals("**"))
                holder.shareSeenStatus.setBackgroundResource(R.drawable.doubletick);
            else
                holder.shareSeenStatus.setBackgroundResource(R.drawable.singletick);

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


        //pritam
        private LinearLayout messageLayout;
        private LinearLayout shareLayout;
        private TextView shareTime,messageTime;
        private ImageView shareSeenStatus,messageSeenStatus;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            v=itemView;
            message=(itemView).findViewById(R.id.single_message_text_view);
            messageLinearLayout=(itemView).findViewById(R.id.single_message_linear_layout);
            messageImage=(itemView).findViewById(R.id.single_message_image);

            //pritam
            messageLayout=(itemView).findViewById(R.id.message_layout);
            shareLayout=(itemView).findViewById(R.id.share_layout);

            shareTime=(itemView).findViewById(R.id.share_time);
            messageTime=(itemView).findViewById(R.id.message_time);

            shareSeenStatus =(itemView).findViewById(R.id.share_seen_status);
            messageSeenStatus=(itemView).findViewById(R.id.message_seen_status);



        }
    }

}
