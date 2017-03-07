package com.zebenzi.utils;

import com.zebenzi.ui.R;

/**
 * Created by soorajpottekat on 26/02/17.
 */

public enum ButtonText
{
    QUOTE_CUSTOMER(R.string.button_card_quote_customer),
    ACCEPT_QUOTE(R.string.button_card_accept_quote),
    DECLINE(R.string.button_card_decline_req),
    START_JOB(R.string.button_card_start_job),
    CANCEL(R.string.button_card_cancel_req);



    private final int id;


    private ButtonText(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }


    public static ButtonText findByName(int id){
        for(ButtonText f : values()){
            if( f.getID() == id){
                return f;
            }
        }
        return null;
    }
}
