/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */
         
// MESSAGE GPS_ACCURACY PACKING
package com.MAVLink.ardupilotmega;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.ardupilotmega.CRC;
import java.nio.ByteBuffer;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
* Accuracy statistics for GPS lock
*/
public class msg_gps_accuracy_test{

public static final int MAVLINK_MSG_ID_GPS_ACCURACY = 225;
public static final int MAVLINK_MSG_LENGTH = 22;
private static final long serialVersionUID = MAVLINK_MSG_ID_GPS_ACCURACY;

private Parser parser = new Parser();

public CRC generateCRC(byte[] packet){
    CRC crc = new CRC();
    for (int i = 1; i < packet.length - 2; i++) {
        crc.update_checksum(packet[i] & 0xFF);
    }
    crc.finish_checksum(MAVLINK_MSG_ID_GPS_ACCURACY);
    return crc;
}

public byte[] generateTestPacket(){
    ByteBuffer payload = ByteBuffer.allocate(6 + MAVLINK_MSG_LENGTH + 2);
    payload.put((byte)MAVLinkPacket.MAVLINK_STX); //stx
    payload.put((byte)MAVLINK_MSG_LENGTH); //len
    payload.put((byte)0); //seq
    payload.put((byte)255); //sysid
    payload.put((byte)190); //comp id
    payload.put((byte)MAVLINK_MSG_ID_GPS_ACCURACY); //msg id
    payload.putFloat((float)17.0); //h_acc
    payload.putFloat((float)45.0); //s_acc
    payload.putFloat((float)73.0); //h_vel_filt
    payload.putFloat((float)101.0); //v_vel_filt
    payload.putFloat((float)129.0); //p_drift
    payload.put((byte)65); //instance
    payload.put((byte)132); //ekf_check_mask
    
    CRC crc = generateCRC(payload.array());
    payload.put((byte)crc.getLSB());
    payload.put((byte)crc.getMSB());
    return payload.array();
}

@Test
public void test(){
    byte[] packet = generateTestPacket();
    for(int i = 0; i < packet.length - 1; i++){
        parser.mavlink_parse_char(packet[i] & 0xFF);
    }
    MAVLinkPacket m = parser.mavlink_parse_char(packet[packet.length - 1] & 0xFF);
    byte[] processedPacket = m.encodePacket();
    assertArrayEquals("msg_gps_accuracy", processedPacket, packet);
}
}
        