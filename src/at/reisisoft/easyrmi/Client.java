package at.reisisoft.easyrmi;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
	/**
	 *
	 * @param bindName
	 *            The <b>unique</b> name of this Object ober <b> all</b> RMI
	 *            objects
	 * @param host
	 *            host for the remote registry
	 * @param remoteObjectClass
	 *            The class of the desired object
	 * @return A stub, which is a RMI connection to the object
	 * @throws RemoteException
	 *             When the underlayuing system throws a {@link RemoteException}
	 * @throws NotBoundException
	 *             When no object is bound to the bindname
	 */
	public static <R extends Remote> R getClient(String bindName, String host,
			Class<R> remoteObjectClass) throws RemoteException,
			NotBoundException {
		final Registry registry = LocateRegistry.getRegistry(host);
		return (R) registry.lookup(bindName);

	}

}
