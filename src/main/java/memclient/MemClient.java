package memclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import memclient.io.MemConnection;
import memclient.io.impl.DirectConnection;
import memclient.protocal.Protocal;
import memclient.protocal.binary.AbstractBinaryRequest;
import memclient.protocal.binary.AbstractBinaryResponse;
import memclient.protocal.binary.BinaryProtocal;
import memclient.protocal.binary.GetKQRequest;
import memclient.protocal.binary.GetKResponse;
import memclient.protocal.binary.GetRequest;
import memclient.protocal.binary.GetResponse;
import memclient.protocal.binary.NoopRequest;
import memclient.protocal.binary.NoopResponse;
import memclient.protocal.binary.SetRequest;
import memclient.protocal.binary.SetResponse;

public class MemClient {
  private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(MemClient.class);
  // private MemConnection handler = new DirectConnection();
  // private MemConnection handler = new QueuedConnection(new
  // DirectConnection());
  private MemConnection conn = new DirectConnection();
  private Protocal<AbstractBinaryRequest, AbstractBinaryResponse> protocal = new BinaryProtocal();
  private final String host;
  private final short port;

  public MemClient(String host, short port) {
    this.host = host;
    this.port = port;
  }

  public void initialize() throws IOException {
    if (logger.isInfoEnabled()) {
      logger.info("Initializing MemClient. [host=" + this.host + ", port="
          + this.port + "]");
    }
    this.conn.initialize(this.host, this.port);
    this.protocal.initialize(this.conn);
    if (logger.isInfoEnabled()) {
      logger.info("Initializing MemClient done.");
    }
  }

  public void noop() throws IOException, InterruptedException {
    NoopRequest request = new NoopRequest();
    NoopResponse response = new NoopResponse();
    protocal.request(request);
    protocal.response(response);
    if (logger.isDebugEnabled()) {
      logger.debug("[NOOP] response: " + response.debugHeader());
    }
  }

  public void set(byte[] key, byte[] value) throws IOException,
      InterruptedException {
    SetRequest request = new SetRequest(key, value, 0, 3600);
    SetResponse response = new SetResponse();
    protocal.request(request);
    protocal.response(response);
    if (logger.isDebugEnabled()) {
      logger.debug("[SET] " + new String(key) + " = " + new String(value)
          + " response: " + response.debugHeader());
    }
  }

  public byte[] get(byte[] key) throws IOException, InterruptedException {
    GetRequest request = new GetRequest(key);
    GetResponse response = new GetResponse();
    protocal.request(request);
    protocal.response(response);
    if (logger.isDebugEnabled()) {
      logger.debug("[GET] " + new String(key) + " response: "
          + response.debugHeader());
    }
    return response.getValue();
  }

  public Map<byte[], byte[]> mget(List<byte[]> keys)
      throws InterruptedException, IOException {
    int sizeOfKeys = keys.size();
    for (int reqNum = 0; reqNum < sizeOfKeys; ++reqNum) {
      GetKQRequest request = new GetKQRequest(keys.get(reqNum));
      protocal.request(request);
    }
    NoopRequest noopRequest = new NoopRequest();
    protocal.request(noopRequest);

    Map<byte[], byte[]> result = new HashMap<byte[], byte[]>(sizeOfKeys);
    // We will try to read up to sizeOfKeys+1 times.
    // But exit when NOOP responses.
    for (int resNum = 0; resNum < sizeOfKeys + 1; ++resNum) {
      try {
        GetKResponse response = new GetKResponse();
        protocal.response(response);
        if (BinaryProtocal.OPCODE_NOOP == response.baseGetOpCode()) {
          break;
        }
        byte[] retKey = response.getKey();
        byte[] retValue = response.getValue();
        if (retKey != null && retValue != null) {
          result.put(retKey, retValue);
        }
      } catch (Throwable e) {
        // TODO nicely handle this.
        if (logger.isErrorEnabled()) {
          logger.error("[MGET] failed with handling " + resNum + " key", e);
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("[MGET] " + keys.size() + " requested, " + result.size()
          + " responsed.");
    }
    return result;
  }

  public static void main(String[] args) throws IOException,
      InterruptedException {
    MemClient c = new MemClient("localhost", (short) 11212);
    c.initialize();
    c.noop();

    { // initialize
      for (int i = 0; i < 10; ++i) {
        byte[] key = ("Hello" + i).getBytes();
        byte[] value = ("World" + i).getBytes();
        c.set(key, value);
      }
    }

    { // single get
      byte[] ret = c.get("Hello3".getBytes());
      System.out.println("MemClient get return: " + new String(ret));
    }

    { // multiple get
      List<byte[]> keys = new ArrayList<byte[]>();
      keys.add("Hello".getBytes());
      for (int i = 0; i < 10; ++i) {
        byte[] key = ("Hello" + i).getBytes();
        keys.add(key);
      }
      Map<byte[], byte[]> m = c.mget(keys);
      System.out.println("ret size=" + m.size());
      for (Entry<byte[], byte[]> ret : m.entrySet()) {
        System.out.println("MemClient mget " + new String(ret.getKey()) + " = "
            + new String(ret.getValue()));
      }
    }
  }
}
