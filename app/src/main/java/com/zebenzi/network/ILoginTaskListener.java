package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */


public interface ILoginTaskListener <T>
{
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    public void onLoginTaskComplete(T result);

    /**
     * Invoked when the AsyncTask has its execution cancelled.
     */
    public void onLoginTaskCancelled();

}
