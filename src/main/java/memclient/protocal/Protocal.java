package memclient.protocal;

import java.io.IOException;

import memclient.io.MemConnection;

public interface Protocal<REQUEST, RESPONSE> {

  public boolean initialize(MemConnection handler);

  public boolean request(REQUEST req) throws InterruptedException, IOException;

  public boolean response(RESPONSE res) throws InterruptedException,
      IOException;

  public interface RequestIF {
    public boolean write(MemConnection handler) throws InterruptedException,
        IOException;
  }

  public interface ResponseIF {
    public boolean read(MemConnection handler) throws InterruptedException,
        IOException;
  }

  public interface GetRequestIF {
    public byte[] getKey();
  }

  public interface GetResponseIF {
    public byte[] getFlags();

    public byte[] getValue();
  }

  public interface GetKResponseIF extends GetResponseIF {
    public byte[] getKey();
  }

  public interface NoopRequestIF {
  }

  public interface NoopResponseIF {

  }

  public interface SetRequestIF {

    public int getFlags();

    public int getExpire();

    public byte[] getKey();

    public byte[] getValue();
  }

  public interface SetResponseIF {
  }

}
