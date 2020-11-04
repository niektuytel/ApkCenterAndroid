package com.pukkol.apkcenter.data.model.application;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ReviewModel {
    @SerializedName("star")
    private double star;

    @SerializedName("amount")
    private long amount;


    public ReviewModel(double star, int amount) {
        this.star = star;
        this.amount = amount;
    }



    @Override
    public String toString() {
        return "ReviewModel{" +
                "star=" + star +
                ", amount=" + amount +
                '}';
    }

    public String getStar() {
        return String.valueOf(star);
    }

    public void setStar(double star) {
        this.star = star;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @NonNull
    public String getAmountString() {
        String new_amount = "";

        if (amount >= 1000000000)
        {
            amount = (amount / 1000000000);
            new_amount = String.valueOf(amount) + "B";
        }
        else if(amount >= 1000000)
        {
            amount = (amount / 1000000);
            new_amount = String.valueOf(amount) + "M";
        }
        else if(amount >= 1000)
        {
            amount = (amount / 1000);
            new_amount = String.valueOf(amount) + "K";
        }
        else
        {
            new_amount = String.valueOf(amount);
        }

        new_amount += " Reviews";

        return new_amount;
    }
}
