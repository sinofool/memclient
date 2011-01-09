/**
 * 
 */
package memclient.protocal.binary;

import memclient.protocal.Protocal.NoopRequestIF;

public class NoopRequest extends AbstractBinaryRequest implements NoopRequestIF {

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
    return null;
  }

  @Override
  public int baseGetOpaque() {
    return 0;
  }

  @Override
  public byte baseGetOpcode() {
    return BinaryProtocal.OPCODE_NOOP;
  }

  @Override
  public byte[] baseGetValue() {
    return null;
  }

  @Override
  public boolean baseHasExtra() {
    return false;
  }

  @Override
  public boolean baseHasKey() {
    return false;
  }

  @Override
  public boolean baseHasValue() {
    return false;
  }

  @Override
  public boolean baseIsQuiet() {
    return false;
  }
}