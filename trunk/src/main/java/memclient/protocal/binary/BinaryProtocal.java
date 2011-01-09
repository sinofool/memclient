package memclient.protocal.binary;

import java.io.IOException;

import memclient.io.MemConnection;
import memclient.protocal.Protocal;

public class BinaryProtocal implements
    Protocal<AbstractBinaryRequest, AbstractBinaryResponse> {

  public static final byte MAGIC_REQUEST = (byte) 0x80;
  public static final byte MAGIC_RESPONSE = (byte) 0x81;

  public static final short STATUS_NO_ERROR = 0x0000;
  public static final short STATUS_KEY_NOT_FOUND = 0x0001;
  public static final short STATUS_KEY_EXISTS = 0x0002;
  public static final short STATUS_VALUE_TOO_LARGE = 0x0003;
  public static final short STATUS_INVALID_ARGUMENTS = 0x0004;
  public static final short STATUS_ITEM_NOT_STORED = 0x0005;
  public static final short STATUS_INCR_DECR_ON_NON_NUMERIC_VALUE = 0x0006;
  public static final short STATUS_UNKNOWN_COMMAND = 0x0081;
  public static final short STATUS_OUT_OF_MEMORY = 0x0082;

  public static final byte OPCODE_GET = 0x00;
  public static final byte OPCODE_SET = 0x01;
  public static final byte OPCODE_ADD = 0x02;
  public static final byte OPCODE_REPLACE = 0x03;
  public static final byte OPCODE_DELETE = 0x04;
  public static final byte OPCODE_INCREMENT = 0x05;
  public static final byte OPCODE_DECREMENT = 0x06;
  public static final byte OPCODE_QUIT = 0x07;
  public static final byte OPCODE_FLUSH = 0x08;
  public static final byte OPCODE_GETQ = 0x09;
  public static final byte OPCODE_NOOP = 0x0A;
  public static final byte OPCODE_VERSION = 0x0B;
  public static final byte OPCODE_GETK = 0x0C;
  public static final byte OPCODE_GETKQ = 0x0D;
  public static final byte OPCODE_APPEND = 0x0E;
  public static final byte OPCODE_PREPEND = 0x0F;
  public static final byte OPCODE_STAT = 0x10;
  public static final byte OPCODE_SETQ = 0x11;
  public static final byte OPCODE_ADDQ = 0x12;
  public static final byte OPCODE_REPLACEQ = 0x13;
  public static final byte OPCODE_DELETEQ = 0x14;
  public static final byte OPCODE_INCREMENTQ = 0x15;
  public static final byte OPCODE_DECREMENTQ = 0x16;
  public static final byte OPCODE_QUITQ = 0x17;
  public static final byte OPCODE_FLUSHQ = 0x18;
  public static final byte OPCODE_APPENDQ = 0x19;
  public static final byte OPCODE_PREPENDQ = 0x1A;

  public static final byte DATA_TYPE_RAW = 0x00;
  private MemConnection conn;

  @Override
  public boolean request(AbstractBinaryRequest req)
      throws InterruptedException, IOException {
    return req.write(conn);
  }

  @Override
  public boolean response(AbstractBinaryResponse res) throws IOException,
      InterruptedException {
    return res.read(conn);
  }

  @Override
  public boolean initialize(MemConnection conn) {
    this.conn = conn;
    return true;
  }

}
