package com.zebenzi.ui.supplier;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zebenzi.json.model.job.JobRating;
import com.zebenzi.ui.R;

import java.util.ArrayList;

/**
 * Created by Vaugan.Nayagar on 2016/01/15.
 */
public class SupplierAccountAdapter extends RecyclerView.Adapter<SupplierAccountAdapter.WorkerViewHolder> {

    private ArrayList<JobRating> arrayOfJobReviews;
    private SupplierAccountFragment fragment;

    public SupplierAccountAdapter(ArrayList<JobRating> jobRatingList, SupplierAccountFragment fragment) {
        this.arrayOfJobReviews = jobRatingList;
        this.fragment = fragment;
    }


    public JobRating getJobReviewFromPosition(int position) {
        return arrayOfJobReviews.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayOfJobReviews.size();
    }

    @Override
    public void onBindViewHolder(WorkerViewHolder workerViewHolder, int i) {
        final JobRating jobReview = arrayOfJobReviews.get(i);

        try {
            // Populate the data into the template view using the data object
            workerViewHolder.tvComment.setText(jobReview.getComment());
            workerViewHolder.ratingBar.setRating((float)jobReview.getRating());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public WorkerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_customer_job_comments, viewGroup, false);

        return new WorkerViewHolder(itemView);
    }

    public void clear() {
        arrayOfJobReviews.clear();
    }

    public void addAll(ArrayList<JobRating> jobReviewList) {
        arrayOfJobReviews.addAll(jobReviewList);
    }

    public static class WorkerViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvComment;
        protected RatingBar ratingBar;

        public WorkerViewHolder(View v) {
            super(v);

            // Lookup view for data population
            tvComment = (TextView) v.findViewById(R.id.list_card_job_comments_comment);
            ratingBar = (RatingBar) v.findViewById(R.id.list_card_job_comments_rating_bar);
        }


    }
}