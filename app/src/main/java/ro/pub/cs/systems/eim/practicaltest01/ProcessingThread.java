package ro.pub.cs.systems.eim.practicaltest01;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import java.util.Date;

public class ProcessingThread extends Thread {

    private final Context context;
    private final int sum;
    private boolean running;

    public ProcessingThread(Context conte, int sum) {
        this.context = conte;
        this.sum = sum;
        running = true;
    }

    @Override
    public void run() {
        Log.d(Constants.PROCESSING_THREAD_TAG, "Thread has started! PID: " + Process.myPid() + " TID: " + Process.myTid());
        while (running) {
            sleep();
            sendMessage();
        }
    }

    private void sendMessage() {
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_ACTION);
        intent.putExtra(Constants.BROADCAST_RECEIVER_EXTRA, new Date(System.currentTimeMillis()) + " " + sum);
        context.sendBroadcast(intent);
    }

    private void sleep() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopThread() {
        running = false;
    }
}
