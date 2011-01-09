package memclient.protocal.binary;

public class GetKRequest extends GetRequest {

  public GetKRequest(byte[] key) {
    super(key);
  }

  @Override
  public byte baseGetOpcode() {
    return BinaryProtocal.OPCODE_GETK;
  }
}
