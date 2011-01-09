package memclient.io.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import memclient.io.MemConnection;

@Deprecated
public class NIOConnection implements MemConnection {

  private SocketChannel channel;
  private Selector selector;

  @Override
  public boolean initialize(String host, short port) {
    boolean success = false;
    InetSocketAddress address = new InetSocketAddress(host, port);
    try {
      channel = SocketChannel.open();
      channel.configureBlocking(false);
      channel.connect(address);
      selector = Selector.open();
      channel.register(selector, SelectionKey.OP_CONNECT);
      selector.select();
      channel.finishConnect();

      channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      success = true;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return success;
  }

  @Override
  public void flush() throws IOException {
    channel.socket().getOutputStream().flush();
  }

  @Override
  public synchronized int read(byte[] data, int off, int len)
      throws IOException, InterruptedException {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    buffer.position(off);
    buffer.limit(off + len);
    int bytesRead = 0;
    selector.select();
    for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator
        .hasNext();) {
      SelectionKey key = iterator.next();
      if (key.isValid() && key.isReadable()) {
        bytesRead = channel.read(buffer);
        iterator.remove();
      }
    }
    return bytesRead;
  }

  @Override
  public synchronized boolean write(byte... request) throws IOException,
      InterruptedException {
    selector.select();
    for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator
        .hasNext();) {
      SelectionKey key = iterator.next();
      if (key.isValid() && key.isWritable()) {
        channel.write(ByteBuffer.wrap(request));
        iterator.remove();
      }
    }
    return true;
  }

}
