package at.reisisoft.easyrmi;

/**
 * @author Florian Reisinger
 * A simple RMI helper class for the server side
 */
import java.io.Closeable;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server<E extends Remote> implements Closeable {

	private final E stub;
	private final Registry r;
	private final String bindName;

	private Server(final E stub, final Registry r, final String bindName) {
		this.stub = stub;
		this.r = r;
		this.bindName = bindName;
	}

	/**
	 *
	 * @param remoteObject
	 *            The object, which should be accessible via RMI
	 * @param bindName
	 *            The <b>unique</b> name of this Object ober <b> all</b> RMI
	 *            objects
	 * @return A {@link Server} object, which allows you to start and stop
	 *         (close) the server
	 * @throws RemoteException
	 *             When the underlayuing system throws a {@link RemoteException}
	 */
	public static <R extends Remote> Server<R> getServer(final R remoteObject,
			final String bindName) throws RemoteException {

		final R stub = (R) UnicastRemoteObject.exportObject(remoteObject, 0);
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry();
			try {
				registry.lookup("test");
			} catch (final NoSuchObjectException | NotBoundException e) {
				// Ignore
			}
		} catch (final RemoteException re) {
			registry = LocateRegistry.createRegistry(1099);
		}
		return new Server<R>(stub, registry, bindName);
	}

	@Override
	public void close() throws IOException {
		try {
			r.unbind(bindName);
		} catch (final NotBoundException e) {
			return; // Ignore Exception -> already closed
		}
	}

	public void start() throws RemoteException {
		try {
			r.bind(bindName, stub);
		} catch (final AlreadyBoundException e) {

			try {
				r.unbind(bindName);
			} catch (final NotBoundException e1) {
				throw new RemoteException(e.getMessage(), e);
			}

			start();
		}
	}

}
