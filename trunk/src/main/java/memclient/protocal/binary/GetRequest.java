/**
 * 
 */
package memclient.protocal.binary;

import memclient.protocal.Protocal.GetRequestIF;

public class GetRequest extends AbstractBinaryRequest implements GetRequestIF {

  private byte[] key;

  public GetRequest(byte[] key) {
    this.key = key;
  }

  @Override
  public byte[] getKey() {
    return baseGetKey();
  }

  @Override
  public long baseGetCas() {
    return 0;
  }

  @Override
  public byte[] baseGetExtra() {
    return null;
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
    return BinaryProtocal.OPCODE_GET;
  }

  @Override
  public byte[] baseGetValue() {
    return null;
  }

  @Override
  public boolean baseIsQuiet() {
    return false;
  }

}