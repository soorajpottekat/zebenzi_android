package com.zebenzi.json.model.job;

import com.zebenzi.json.model.quote.Quote;
import com.zebenzi.json.model.user.User;

import java.io.Serializable;

/**
 * Created by Vaugan.Nayagar on 2015/11/15.
 */
public class JobRating implements Serializable {
    int ratingId;
    int rating;
    String comment;

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
