/**
 * 
 */
package memclient.protocal.binary;

import memclient.protocal.Protocal.GetResponseIF;

public class GetResponse extends AbstractBinaryResponse implements
    GetResponseIF {

  @Override
  public byte[] getFlags() {
    if (extra != null && extra.length == 4) {
      return extra;
    }
    return null;
  }

  @Override
  public byte[] getValue() {
    return value;
  }
}