/**
 * 
 */
package memclient.protocal.binary;

import java.io.DataOutputStream;
import java.io.IOException;

import memclient.io.MemConnection;
import memclient.io.impl.DirectConnection;
import memclient.protocal.Protocal.RequestIF;

public abstract class AbstractBinaryRequest implements RequestIF {

  public abstract byte baseGetOpcode();

  public abstract byte[] baseGetKey();

  public abstract byte[] baseGetExtra();

  public abstract byte[] baseGetValue();

  public abstract int baseGetOpaque();

  public abstract long baseGetCas();

  public abstract boolean baseIsQuiet();

  public boolean baseHasExtra() {
    return baseGetExtra() != null;
  }

  public boolean baseHasKey() {
    return baseGetKey() != null;
  }

  public boolean baseHasValue() {
    return baseGetValue() != null;
  }

  private byte[] RESERVED_BYTES = new byte[] { 0x00, 0x00 };

  @Override
  public boolean write(MemConnection conn) throws InterruptedException,
      IOException {
    short keylength = baseHasKey() ? (short) baseGetKey().length : 0;
    byte extralength = baseHasExtra() ? (byte) baseGetExtra().length : 0;
    int totalLength = keylength + extralength
        + (baseHasValue() ? baseGetValue().length : 0);
    if (conn instanceof DirectConnection) {
      DirectConnection direct = (DirectConnection) conn;
      DataOutputStream s = direct.getOutputStream();
      s.writeByte(BinaryProtocal.MAGIC_REQUEST);
      s.writeByte(baseGetOpcode());
      s.writeShort(keylength);
      s.writeByte(extralength);
      s.writeByte(BinaryProtocal.DATA_TYPE_RAW);
      s.write(RESERVED_BYTES);
      s.writeInt(totalLength);
      s.writeInt(baseGetOpaque());
      s.writeLong(baseGetCas());
      if (baseHasExtra()) {
        s.write(baseGetExtra());
      }
      if (baseHasKey()) {
        s.write(baseGetKey());
      }
      if (baseHasValue()) {
        s.write(baseGetValue());
      }
    } else {
      conn.write(BinaryProtocal.MAGIC_REQUEST);
      conn.write(baseGetOpcode());
      conn.write((byte) (keylength >> 4), (byte) (keylength & 0x00FF));
      conn.write(extralength);
      conn.write(BinaryProtocal.DATA_TYPE_RAW);
      conn.write(RESERVED_BYTES);
      conn.write((byte) (totalLength >> 24 & 0x000000FF),
          (byte) (totalLength >> 16 & 0x000000FF),
          (byte) (totalLength >> 8 & 0x000000FF),
          (byte) (totalLength & 0x000000FF));
      conn.write((byte) (baseGetOpaque() >> 24 & 0x000000FF),
          (byte) (baseGetOpaque() >> 16 & 0x000000FF),
          (byte) (baseGetOpaque() >> 8 & 0x000000FF),
          (byte) (baseGetOpaque() & 0x000000FF));
      conn.write((byte) (baseGetCas() >> 56 & 0x00000000000000FF),
          (byte) (baseGetCas() >> 48 & 0x00000000000000FF),
          (byte) (baseGetCas() >> 40 & 0x00000000000000FF),
          (byte) (baseGetCas() >> 32 & 0x00000000000000FF),
          (byte) (baseGetCas() >> 24 & 0x00000000000000FF),
          (byte) (baseGetCas() >> 16 & 0x00000000000000FF),
          (byte) (baseGetCas() >> 8 & 0x00000000000000FF),
          (byte) (baseGetCas() & 0x00000000000000FF));

      if (baseHasExtra()) {
        conn.write(baseGetExtra());
      }
      if (baseHasKey()) {
        conn.write(baseGetKey());
      }
      if (baseHasValue()) {
        conn.write(baseGetValue());
      }
    }
    if (!baseIsQuiet()) {
      conn.flush();
    }
    return true;
  }
}