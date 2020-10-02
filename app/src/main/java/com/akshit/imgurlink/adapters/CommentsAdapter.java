package com.akshit.imgurlink.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.imgurlink.R;
import com.akshit.imgurlink.models.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView commentTextView;

        public MyViewHolder(@NonNull RelativeLayout mainView) {
            super(mainView);
            commentTextView = mainView.findViewById(R.id.commentTextView);
        }
    }

    private List<Comment> comments;

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commentTextView.setText(comment.comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
