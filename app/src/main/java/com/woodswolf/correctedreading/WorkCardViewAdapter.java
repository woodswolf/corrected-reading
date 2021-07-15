package com.woodswolf.correctedreading;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;

public class WorkCardViewAdapter extends RecyclerView.Adapter<WorkCardViewAdapter.ViewHolder> {
    private Work[] works;
    private Listener listener;

    private int text_heading_size_sp;
    private int text_subheading_size_sp;
    private int text_normal_size_sp;

    interface Listener {
        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }

    }

    public WorkCardViewAdapter(Work[] works) {
        this.works = works;
    }

    @Override
    public int getItemCount() {
        return works.length;
    }

    public void setWorks(Work[] works) {
        this.works = works;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setTextSizes(int text_heading_size_sp, int text_subheading_size_sp, int text_normal_size_sp) {
        this.text_heading_size_sp = text_heading_size_sp;
        this.text_subheading_size_sp = text_subheading_size_sp;
        this.text_normal_size_sp = text_normal_size_sp;
    }

    @NonNull
    @Override
    public WorkCardViewAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_work, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkCardViewAdapter.ViewHolder holder,
                                 final int position) {
        final CardView cardView = holder.cardView;
        Resources resources = MainActivity.getRes();

        String workTitle = resources.getString(works[position].getWorkTitleId());
        String workAuthor = resources.getString(works[position].getAuthorId());
        String workDescription = resources.getString(works[position].getDescriptionId());
        int workChapterCount = works[position].getChapterCount();
        int workWordCount = works[position].getWorkWordCount();

        ImageView imageView = cardView.findViewById(R.id.card_cover_art);
        Drawable drawable =
                ContextCompat.getDrawable(cardView.getContext(), works[position].getCoverArtId());
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(works[position].toString());

        TextView title = cardView.findViewById(R.id.card_title);
        title.setText(workTitle);
        title.setTextSize(text_heading_size_sp);
        TextView author = cardView.findViewById(R.id.card_author);
        author.setText("by " + workAuthor);
        author.setTextSize(text_subheading_size_sp);
        
        String chapter_s = " chapters — ";
        String word_s = " words";
        if (workChapterCount == 1) {
            chapter_s = " chapter — ";
        }
        if (workWordCount == 1) {
            word_s = " word";
        }
        TextView stats = cardView.findViewById(R.id.card_stats);
        stats.setText(NumberFormat.getIntegerInstance().format(workChapterCount) + chapter_s +
                NumberFormat.getIntegerInstance().format(workWordCount) + word_s);
        stats.setTextSize(text_normal_size_sp);

        TextView description = cardView.findViewById(R.id.card_description);
        description.setText(workDescription);
        description.setTextSize(text_normal_size_sp);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
    }

}