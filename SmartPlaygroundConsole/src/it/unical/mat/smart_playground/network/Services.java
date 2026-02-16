/**
 * 
 */
package it.unical.mat.smart_playground.network;

/**
 * @author Agostino
 *
 */
public class Services
{
	public static final short DISCOVERY_PORT = 4000;
	public static final short EVENT_SOCKET_PORT = 4001;
	public static final short SENSOR_DATA_SOCKET_PORT = 4002;
	
	public static final byte[] DISCOVERY_REQUEST_CODE = {0x01};
	public static final int DISCOVERY_RESPONSE_LENGTH = 1;
}
