package com.zebenzi.ui.supplier.Listeners;

import android.view.MotionEvent;
import android.view.View;

import com.zebenzi.ui.R;

/**
 * Created by soorajpottekat on 26/02/17.
 */

public class ListenerProvider
{
    private static final ListenerProvider provider = new ListenerProvider();
    private ListenerProvider()
    {
    }
    public static ListenerProvider getInstance()
    {
        return provider;
    }

    public View.OnTouchListener getListener(int id)
    {
        switch (id)
        {
            case R.string.button_card_quote_customer:
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE)
                        {
                            // to do send server request
                        }

                        System.out.println(" Inside OCL Quote Customer");
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                };
            case R.string.button_card_accept_quote:
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE)
                        {
                            // to do send server request
                        }
                        System.out.println(" Inside OCL Accept Quote");
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                };
            case R.string.button_card_decline_req:
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE)
                        {
                            // to do send server request
                        }
                        System.out.println(" Inside OCL decline req");
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                };
            case R.string.button_card_start_job:
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE)
                        {
                            // to do send server request
                        }
                        System.out.println(" Inside OCL start Job");
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                };
            case R.string.button_card_cancel_req:
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE)
                        {
                            // to do send server request
                        }
                        System.out.println(" Inside OCL cancel req");
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                };
            default:
                return new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                };
        }
    }
}
