/**
 * 
 */
package memclient.protocal.binary;

import java.io.IOException;

import memclient.io.MemConnection;
import memclient.protocal.Protocal.ResponseIF;

public abstract class AbstractBinaryResponse implements ResponseIF {
  private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
      .getLog(AbstractBinaryResponse.class);
  protected byte[] header = null;
  protected byte[] extra = null;
  protected byte[] key = null;
  protected byte[] value = null;
  private static ThreadLocal<byte[]> headerLocal = new ThreadLocal<byte[]>();

  @Override
  public boolean read(MemConnection conn) throws IOException,
      InterruptedException {
    header = headerLocal.get();
    if (header == null) {
      header = new byte[24];
      headerLocal.set(header);
    }
    int bytesRead = 0;
    while (bytesRead < 24) {
      bytesRead += conn.read(header, bytesRead, 24 - bytesRead);
    }
    int bodyLength = baseGetBodyLength();
    if (logger.isTraceEnabled()) {
      logger.trace("binary protocal response body length is " + bodyLength);
    }

    if (bodyLength > 0) {
      int extraLength = baseGetExtraLength();
      if (extraLength > 0) {
        this.extra = new byte[extraLength];
        bytesRead = 0;
        while (bytesRead < extraLength) {
          bytesRead += conn.read(extra, 0, extraLength - bytesRead);
        }
        if (logger.isTraceEnabled()) {
          logger.trace("binary protocal response header length  is "
              + this.header.length);
        }
      }
      int keyLength = baseGetKeyLength();
      if (keyLength > 0) {
        this.key = new byte[keyLength];
        bytesRead = 0;
        while (bytesRead < keyLength) {
          bytesRead += conn.read(key, 0, keyLength - bytesRead);
        }
        if (logger.isTraceEnabled()) {
          logger.trace("binary protocal response key is "
              + new String(this.key));
        }
      }
      int valueLength = bodyLength - extraLength - keyLength;
      if (valueLength > 0) {
        this.value = new byte[valueLength];
        bytesRead = 0;
        while (bytesRead < valueLength) {
          bytesRead += conn.read(value, 0, valueLength - bytesRead);
        }
        if (logger.isTraceEnabled()) {
          logger.trace("binary protocal response value is "
              + new String(this.value));
        }
      }
    }
    return true;
  }

  public byte baseGetMagic() {
    return this.header[0];
  }

  public byte baseGetOpCode() {
    return this.header[1];
  }

  public short baseGetKeyLength() {
    return (short) (this.header[2] << 8 | this.header[3]);
  }

  public byte baseGetExtraLength() {
    return this.header[4];
  }

  public byte baseGetDataType() {
    return this.header[5];
  }

  public short baseGetStatus() {
    return (short) (this.header[6] << 8 | this.header[7]);
  }

  public int baseGetBodyLength() {
    return (this.header[8] << 24) | (this.header[9] << 16)
        | (this.header[10] << 8) | this.header[11];
  }

  public int baseGetOpaque() {
    return (this.header[12] << 24) | (this.header[13] << 16)
        | (this.header[14] << 8) | this.header[15];
  }

  public long baseGetCAS() {
    return (this.header[16] << 56) | (this.header[17] << 48)
        | (this.header[18] << 40) | (this.header[19] << 32)
        | (this.header[20] << 24) | (this.header[21] << 16)
        | (this.header[22] << 8) | this.header[23];
  }

  public String debugHeader() {
    StringBuffer str = new StringBuffer();
    for (int index = 0; index < this.header.length; ++index) {
      if (index % 4 == 0) {
        str.append("\n");
        str.append(this.header[index]);
      } else {
        str.append("\t");
        str.append(this.header[index]);
      }
    }
    return (str.toString());
  }
}