/**
 * 
 */
package memclient.protocal.binary;

import memclient.protocal.Protocal.GetKResponseIF;

public class GetKResponse extends GetResponse implements GetKResponseIF {

  @Override
  public byte[] getKey() {
    return key;
  }
}