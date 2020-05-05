/**
 * 
 */
package it.unical.mat.smart_table_tennis_app.net;

import java.net.InetAddress;

/**
 * @author Agostino
 *
 */
public class NetService
{
	private final byte[] code;
	private final InetAddress server_address;
	
	public NetService( final byte[] code, final InetAddress server_address )
	{
		this.code=code.clone();
		this.server_address=server_address;
	}
	public byte[] getCode()
	{
		return code.clone();
	}
	public InetAddress getServerAddress()
	{
		return server_address;
	}
}
