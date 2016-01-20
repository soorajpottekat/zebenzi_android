package com.zebenzi.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zebenzi.json.model.user.User;
import com.zebenzi.users.Customer;

import java.util.ArrayList;

/**
 * Created by Vaugan.Nayagar on 2016/01/15.
 */
public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.WorkerViewHolder> {

    private ArrayList<User> arrayOfWorkers;
    private QuoteFragment fragment;

    public QuoteAdapter(ArrayList<User> workerList, QuoteFragment fragment) {
        this.arrayOfWorkers = workerList;
        this.fragment = fragment;
    }


    public User getWorkerFromPosition(int position) {
        return arrayOfWorkers.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayOfWorkers.size();
    }

    @Override
    public void onBindViewHolder(WorkerViewHolder workerViewHolder, int i) {
        final User worker = arrayOfWorkers.get(i);

        //TODO: Should we handle errors in the results? Eg. Null data. Or should the server worry about his?
        try {
            // Populate the data into the template view using the data object
            workerViewHolder.tvFirstName.setText(worker.getFirstName());
            workerViewHolder.tvLastName.setText(worker.getLastName());
            workerViewHolder.tvRating.setText(Float.toString(worker.getAverageRating()));
            Picasso.with(MainActivity.getAppContext()).load(worker.getImageUrl()).into(workerViewHolder.img);

            // onClick Listener for view
            workerViewHolder.hireButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (Customer.getInstance().getToken() != null) {
                            fragment.hireWorker(Customer.getInstance().getLastQuote().getQuoteId(), worker.getId());
                        } else {
                            Toast.makeText(MainActivity.getAppContext(), "You need to be logged in to hire a worker", Toast.LENGTH_LONG).show();
                            System.out.println("Cannot hire worker if not logged in.");
                        }
                }
            });
            // onClick Listener for view
            workerViewHolder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.fragmentListener.changeFragment(FragmentsLookup.WORKER_PROFILE, worker);
                    Toast.makeText(v.getContext(), "Worker IMG Pressed. Worker = " + worker.getFirstName(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public WorkerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_card_available_workers, viewGroup, false);

        return new WorkerViewHolder(itemView);
    }

    public void clear() {
        arrayOfWorkers.clear();
    }

    public void addAll(ArrayList<User> workerList) {
        arrayOfWorkers.addAll(workerList);
    }

    public static class WorkerViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvFirstName;
        protected TextView tvLastName;
        protected TextView tvRating;
        protected ImageView img;
        protected Button hireButton;

        private User mWorker;

        public WorkerViewHolder(View v) {
            super(v);

            // Lookup view for data population
            tvFirstName = (TextView) v.findViewById(R.id.list_card_avail_workers_first_name);
            tvLastName = (TextView) v.findViewById(R.id.list_card_avail_workers_last_name);
            tvRating = (TextView) v.findViewById(R.id.list_card_avail_workers_rating);
            img = (ImageView) v.findViewById(R.id.list_card_avail_workers_image);
            hireButton = (Button) v.findViewById(R.id.list_card_avail_workers_hire_button);
        }


    }
}