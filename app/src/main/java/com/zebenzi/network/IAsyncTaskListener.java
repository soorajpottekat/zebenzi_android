package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */


public interface IAsyncTaskListener<T>
{
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    public void onAsyncTaskComplete(T result, boolean networkError);

    /**
     * Invoked when the AsyncTask has its execution cancelled.
     */
    public void onAsyncTaskCancelled();

}
