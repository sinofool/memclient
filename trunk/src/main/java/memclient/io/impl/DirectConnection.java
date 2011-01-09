package memclient.io.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import memclient.io.MemConnection;

public class DirectConnection implements MemConnection {
  private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(DirectConnection.class);
  private Socket sock;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  @Override
  public boolean initialize(String host, short port) {
    boolean success = false;
    try {
      this.sock = new Socket(host, port);
      this.sock.setReceiveBufferSize(1024 * 1025);
      this.sock.setTcpNoDelay(true);
      this.sock.setKeepAlive(true);
      this.sock.setSendBufferSize(1024 * 1025);

      this.inputStream = new DataInputStream(new BufferedInputStream(sock
          .getInputStream()));
      this.outputStream = new DataOutputStream(new BufferedOutputStream(sock
          .getOutputStream()));
      success = true;
    } catch (IOException e) {
      logger.fatal("Failed to init DirectConnection", e);
    }
    return success;
  }

  @Override
  public void flush() throws IOException {
    outputStream.flush();
  }

  @Override
  public int read(byte[] data, int off, int len) throws IOException,
      InterruptedException {
    if (logger.isTraceEnabled()) {
      logger.trace("read to " + data.length + "bytes array, off=" + off
          + ", len=" + len);
    }
    return inputStream.read(data, off, len);
  }

  @Override
  public boolean write(byte... request) throws IOException,
      InterruptedException {
    if (logger.isTraceEnabled()) {
      logger.trace("write " + request.length + " bytes");
    }
    outputStream.write(request);
    return true;
  }

  public DataInputStream getInputStream() {
    if (logger.isTraceEnabled()) {
      logger.trace("An raw DataInputStream is returned. "
          + "Futher read operations are not tracabled.");
    }
    return inputStream;
  }

  public DataOutputStream getOutputStream() {
    if (logger.isTraceEnabled()) {
      logger.trace("An raw DataOutputStream is returned. "
          + "Futher write operations are not tracabled.");
    }
    return outputStream;
  }

}
