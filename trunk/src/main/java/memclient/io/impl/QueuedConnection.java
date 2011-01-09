package memclient.io.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;

import memclient.io.MemConnection;

@Deprecated
public class QueuedConnection implements MemConnection {

  private class WriteThread extends Thread {

    public WriteThread() {
      super("QueuedConnection WriteThread");
    }

    @Override
    public void run() {
      while (true) {
        try {
          byte[] nextRequest = requestQueue.takeFirst();
          realConn.write(nextRequest);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  private class ReadThread extends Thread {
    private byte[] reading = new byte[1024];

    public ReadThread() {
      super("QueuedConnection ReadThread");
    }

    @Override
    public void run() {
      while (true) {
        try {
          int readed = realConn.read(reading, 0, reading.length);
          if (readed > 0) {
            responseQueue.offerLast(Arrays.copyOfRange(reading, 0, readed));
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    }
  }

  private final MemConnection realConn;
  private final LinkedBlockingDeque<byte[]> requestQueue = new LinkedBlockingDeque<byte[]>();
  private final LinkedBlockingDeque<byte[]> responseQueue = new LinkedBlockingDeque<byte[]>();
  private ReadThread read_thread = new ReadThread();
  private WriteThread write_thread = new WriteThread();

  public QueuedConnection(MemConnection realConn) {
    this.realConn = realConn;
  }

  @Override
  public boolean initialize(String host, short port) {
    boolean success = this.realConn.initialize(host, port);
    if (success) {
      read_thread.start();
      write_thread.start();
    }
    return success;
  }

  @Override
  public void flush() throws IOException {
    this.realConn.flush();
  }

  private byte[] currentReading;
  private int currentReadingPos = 0;

  @Override
  public int read(byte[] data, int off, int len) throws IOException,
      InterruptedException {
    if (currentReading == null) {
      currentReading = responseQueue.takeFirst();
      currentReadingPos = 0;
    }

    int current_left_bytes = currentReading.length - currentReadingPos;
    int data_left_bytes = data.length - off;
    int bytes_to_copy = Math.min(len, Math.min(current_left_bytes,
        data_left_bytes));

    // System.out.println("copy from " + current.length + " at " + pos +
    // " to "
    // + data.length + " at " + off + " len=" + bytes_to_copy);
    System.arraycopy(currentReading, currentReadingPos, data, off,
        bytes_to_copy);
    currentReadingPos += bytes_to_copy;
    if (currentReadingPos == currentReading.length) {
      currentReading = null;
    }
    return bytes_to_copy;
  }

  @Override
  public boolean write(byte... request) throws IOException,
      InterruptedException {
    requestQueue.offerLast(request);
    return true;
  }

}
