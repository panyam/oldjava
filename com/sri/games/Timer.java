package com.sri.games;

import java.awt.*;
import java.util.*;

public class Timer extends LED implements Runnable {
    long inter;  // interval bw change in milliseconds
    long stopval;
    long dec;
	protected boolean threadRunning = false;
	protected boolean threadSuspended = true;
	protected boolean threadStopped = false;
    Thread ourThread = null;

    public Timer(Color c,String num,int digits,
                    long inter,long stopval,long dec,int just,boolean zfill) {
        super(c,num,digits,just,zfill);
        this.inter = inter;
        this.stopval = stopval;
        this.dec = dec;
    }

		/**
		 * Start the timer.
		 */
    public synchronized void start()
	{ 
			// if the thread is running then
			// wait for it to die...
		if (threadRunning)
		{
			stop();
		}
		threadStopped = false;
		threadRunning = true;
		threadSuspended = false;
		ourThread = new Thread(this);
		ourThread.start();
	} 
	
    public synchronized void stop ()
	{
		if (threadRunning)
		{
			threadStopped = true;
			notify();
				// wait for the thread to stop..
			/*while (threadRunning)
			{
				try
				{
					wait(1);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}*/
		}
	}

    public synchronized void pause()
    {
		if (threadRunning && !threadSuspended)
		{
			threadSuspended = true;
		}
    }

    public synchronized void resume()
    {
		if (threadRunning)
		{
			if (threadSuspended) 
			{
				threadSuspended = false;
				notify();
			}
		} else
		{
				// should we start the thread if not running???
			start();
		}
    }

    public void setInterval(long inter) { this.inter = inter; }
    public void setDecrement(long dec) { this.dec = dec; }
    public void setStopVal(long stopval) { this.stopval = stopval; }
    public void restart(long inter,long dec,long stopval) {
        stop(); 
        setInterval(inter); 
        setDecrement(dec); 
        setStopVal(stopval);
    }
    public void restart() { 
        stop(); 
        setVal(0);
    }
    public void restart(long num) { 
        stop(); 
        setVal(num); 
    }
    public void restart (long num,long inter,long dec,long stopval) {
        stop(); 
        restart(inter,dec,stopval); 
        setVal(num);
    }
	
		/**
		 * Main thread method.
		 */
    public void run() {
loop:
        while (threadRunning && number != stopval)
		{
			while (threadRunning && threadSuspended && !threadStopped)
			{
				try
				{
					wait();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			if (!threadRunning || threadStopped) break loop;
			
            changeBy(dec);
            try { Thread.sleep(inter);}
            catch (InterruptedException e) {}
        }
		//if (threadStopped) notifyAll(); 
		threadRunning = threadSuspended = false;
		threadStopped = true;
    }

    public void changeBy(long dec) {
        number += dec;
        num = number + "";
        paint(getGraphics());
    }
}
