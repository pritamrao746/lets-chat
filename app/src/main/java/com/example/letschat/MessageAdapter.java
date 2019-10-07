package com.example.letschat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> mMessageList;
    String  mCurrentUser;




    public MessageAdapter(List<Message> mMessageList) {
        this.mMessageList = mMessageList;
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
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.message.setText(mMessageList.get(position).getMessage());
      //  holder.message.setGravity();


        if(mMessageList.get(position).getSender().equals(mCurrentUser)){   ///current user's message
            holder.message.setBackgroundColor(Color.WHITE);
            holder.message.setTextColor(Color.BLACK);
            holder.messageLinearLayout.setGravity(5);




        }
        else{                                                               ///other peoples mesage
            holder.message.setBackgroundResource(R.drawable.message_bachground_shape);
            holder.message.setTextColor(Color.WHITE);
            holder.messageLinearLayout.setGravity(3);

        }




    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        LinearLayout messageLinearLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message=(itemView).findViewById(R.id.single_message_text_view);
            messageLinearLayout=(itemView).findViewById(R.id.single_message_linear_layout);




        }
    }

}
