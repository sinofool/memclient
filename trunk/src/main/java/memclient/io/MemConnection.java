package memclient.io;

import java.io.IOException;

public interface MemConnection {
  public boolean initialize(String host, short port);

  public int read(final byte[] data, final int off, final int len)
      throws IOException, InterruptedException;

  public boolean write(byte... request) throws IOException,
      InterruptedException;

  public void flush() throws IOException;

}
