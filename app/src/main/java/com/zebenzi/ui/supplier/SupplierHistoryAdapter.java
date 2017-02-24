package com.zebenzi.ui.supplier;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zebenzi.json.model.job.Job;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;
import com.zebenzi.utils.JobConstants;
import com.zebenzi.utils.TimeFormat;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Vaugan.Nayagar on 2016/01/15.
 */
public class SupplierHistoryAdapter extends RecyclerView.Adapter<SupplierHistoryAdapter.SupplierJobViewHolder> {

    private ArrayList<Job> arrayOfJobs;
    private static final String QUOTE_CUST = "Quote Customer";
    private static final String ACCEPT_QUOTE = "Accept Quote";
    private static final String DECLINE_REQ = "Decline Request";
    private static final String START_JOB = "Start Job";
    private static final String CANCEL = "Cancel";


    public SupplierHistoryAdapter(ArrayList<Job> jobList) {
        this.arrayOfJobs = jobList;
    }


    public Job getJobFromPosition(int position) {
        return arrayOfJobs.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayOfJobs.size();
    }

    @Override
    public void onBindViewHolder(SupplierJobViewHolder supplierJobViewHolder, int i) {
        Job job = arrayOfJobs.get(i);


        //TODO: Should we handle errors in the results? Eg. Null data. Or should the server worry about his?

        try {
            supplierJobViewHolder.tvJobPrice.setText("R" + Integer.toString(job.getQuote().getPrice()));
            supplierJobViewHolder.tvWorkerFirstName.setText(job.getWorker().getFirstName());
            supplierJobViewHolder.tvJobNumber.setText(Integer.toString(job.getJobId()));
            if (job.getStatus() != null) {
                supplierJobViewHolder.tvJobStatus.setText(job.getStatus().getStatusReason());
            }
            supplierJobViewHolder.tvJobDate.setText(TimeFormat.getPrettyDate(job.getQuote().getWorkDate()));
            supplierJobViewHolder.tvJobTime.setText(TimeFormat.getPrettyTime(job.getQuote().getWorkDate()));

            if (job.getQuote().getService() != null) {
                supplierJobViewHolder.tvJobServiceName.setText(job.getQuote().getService().getServiceName());
            }
            if (supplierJobViewHolder.rbJobRatingBar != null) {
                if (job.getRating() != null) {
                    supplierJobViewHolder.rbJobRatingBar.setVisibility(View.VISIBLE);
                    float rating = job.getRating().getRating();
                    supplierJobViewHolder.rbJobRatingBar.setRating(rating);
                } else {
                    supplierJobViewHolder.rbJobRatingBar.setVisibility(View.INVISIBLE);
                }
            }
            System.out.println("job.getStatus().getStatusId()" + job.getStatus().getStatusId());
            switch (JobConstants.getEnum(job.getStatus().getStatusId())) {
                case NEW_JOB:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case AWAITING_EST_RES_CUST:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case EST_DECLINED_CUST:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case EST_ACCEPT_SUP:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case AWAITING_QUO_RES_CUST:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case QUO_ACCEPT_CUS:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case QUO_DECLINED_CUST:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_IN_PROGRESS:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_FIN_AWAITING_RAT:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_FIN:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_CAN_CUST_EST:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_CAN_CUST_QUO:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_CAN_CUST_JOB_IN_PROG:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_CAN_SUP_EST:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_CAN_SUP_QUO:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                case JOB_CAN_SUP_JOB_IN_PROGRESS:
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
                default:
                    // adding this since the job is are starting from 1000
                    supplierJobViewHolder.firstButton.setText(QUOTE_CUST);
                    supplierJobViewHolder.secondButton.setText(CANCEL);
                    break;
            }

            Picasso.with(MainActivity.getAppContext()).load(job.getWorker().getImageUrl()).into(supplierJobViewHolder.img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SupplierJobViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_supplier_job_history, viewGroup, false);

        return new SupplierJobViewHolder(itemView);
    }

    public void clear() {
        arrayOfJobs.clear();
    }

    public void addAll(ArrayList<Job> jobList) {
        arrayOfJobs.addAll(removeDirtyJobs(jobList));
    }

    //Handle bad job data coming from server
    private ArrayList<Job> removeDirtyJobs(ArrayList<Job> jobList) {

        for (Iterator<Job> iterator = jobList.iterator(); iterator.hasNext(); ) {
            Job job = iterator.next();
            if ((job.getWorker() == null) ||
                    (job.getStatus() == null) ||
                    (job.getUser() == null) ||
                    (job.getQuote() == null)) {
                //delete this job from the display as it has corrupted data.
                iterator.remove();
            }
        }
        return jobList;
    }

    public static class SupplierJobViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvJobPrice;
        protected TextView tvWorkerFirstName;
        protected TextView tvJobNumber;
        protected TextView tvJobStatus;
        protected TextView tvJobDate;
        protected TextView tvJobTime;
        protected TextView tvRating;
        protected TextView tvJobServiceName;
        protected RatingBar rbJobRatingBar;
        protected ImageView img;
        protected Button firstButton;
        protected Button secondButton;

        private Job mJob;

        public SupplierJobViewHolder(View v) {
            super(v);

            // Lookup view for data population
            tvJobPrice = (TextView) v.findViewById(R.id.supplier_card_job_history_price);
            tvWorkerFirstName = (TextView) v.findViewById(R.id.supplier_card_job_history_worker_first_name);
            tvJobNumber = (TextView) v.findViewById(R.id.supplier_card_job_history_job_number);
            tvJobStatus = (TextView) v.findViewById(R.id.supplier_card_job_history_job_status);
            tvJobDate = (TextView) v.findViewById(R.id.supplier_card_job_history_job_date);
            tvJobTime = (TextView) v.findViewById(R.id.supplier_card_job_history_job_time);
            tvJobServiceName = (TextView) v.findViewById(R.id.supplier_card_job_history_service_name);
            img = (ImageView) v.findViewById(R.id.supplier_card_worker_image);
            firstButton = (Button) v.findViewById(R.id.supplier_card_job_history_firstButton);
            secondButton = (Button) v.findViewById(R.id.supplier_card_job_history_secondButton);
        }

    }
}