package rpf5573.simpli.co.kr.deepmind.application.mainFields.timer;

/**
 * Created by mac88 on 2017. 9. 2..
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by replay556 on 16. 10. 2..
 */
public abstract class CountUpTimer {

  private final long interval;
  private final long startTime;
  private long base;

  public CountUpTimer(long startTime , long interval) {
    this.interval = interval;
    this.startTime = startTime;
  }

  public void start() {
    base = SystemClock.elapsedRealtime();
    handler.sendMessage(handler.obtainMessage(MSG));
  }

  public void stop() {
    handler.removeMessages(MSG);
  }

  public void reset() {
    synchronized (this) {
      base = SystemClock.elapsedRealtime();
    }
  }

  abstract public void onTick(long elapsedTime);

  private static final int MSG = 1;

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      synchronized (CountUpTimer.this) {
        long elapsedTime = SystemClock.elapsedRealtime() - base;
        onTick(elapsedTime+startTime);
        sendMessageDelayed(obtainMessage(MSG), interval);
      }
    }
  };
}
