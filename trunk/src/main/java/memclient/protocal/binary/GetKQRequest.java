package memclient.protocal.binary;

public class GetKQRequest extends GetRequest {

  public GetKQRequest(byte[] key) {
    super(key);
  }

  @Override
  public byte baseGetOpcode() {
    return BinaryProtocal.OPCODE_GETKQ;
  }

  @Override
  public boolean baseIsQuiet() {
    return true;
  }
}
