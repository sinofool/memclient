/**
 * 
 */
package memclient.protocal.binary;

import memclient.protocal.Protocal.SetRequestIF;

public class SetRequest extends AbstractBinaryRequest implements SetRequestIF {
  private byte[] key;
  private byte[] value;
  private int flag;
  private int expire;

  public SetRequest(byte[] key, byte[] value, int flag, int expire) {
    this.key = key;
    this.value = value;
    this.flag = flag;
    this.expire = expire;
  }

  @Override
  public int getExpire() {
    byte[] extra = baseGetExtra();
    return (extra[4] << 12) | (extra[5] << 8) | (extra[6] << 4) | extra[7];
  }

  @Override
  public int getFlags() {
    byte[] extra = baseGetExtra();
    return (extra[0] << 12) | (extra[1] << 8) | (extra[2] << 4) | extra[3];
  }

  @Override
  public byte[] getKey() {
    return baseGetKey();
  }

  @Override
  public byte[] getValue() {
    return baseGetValue();
  }

  @Override
  public long baseGetCas() {
    return 0;
  }

  @Override
  public byte[] baseGetExtra() {
    return new byte[] { (byte) (flag >> 24), (byte) (flag >> 16),
        (byte) (flag >> 8), (byte) (flag & 0x000000FF), (byte) (expire >> 24),
        (byte) (expire >> 16), (byte) (expire >> 8),
        (byte) (expire & 0x000000FF) };
  }

  @Override
  public byte[] baseGetKey() {
    return key;
  }

  @Override
  public int baseGetOpaque() {
    return 0;
  }

  @Override
  public byte baseGetOpcode() {
    return BinaryProtocal.OPCODE_SET;
  }

  @Override
  public byte[] baseGetValue() {
    return value;
  }

  @Override
  public boolean baseHasExtra() {
    return true;
  }

  @Override
  public boolean baseHasKey() {
    return true;
  }

  @Override
  public boolean baseHasValue() {
    return true;
  }

  @Override
  public boolean baseIsQuiet() {
    return false;
  }

}