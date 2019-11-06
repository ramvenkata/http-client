/**
 * 
 */
package http.client.connection.pooling;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

/**
 * @author Ramesh
 *
 */
public class SimpleConnectionManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		HttpClientContext httpContext = HttpClientContext.create();

		// create http client connection manager instance
		HttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(); // Maintains only one
																							// connection at a time
		// create route
		HttpRoute route = new HttpRoute(new HttpHost("google.com"));

		// create connection
		ConnectionRequest connRequest = connManager.requestConnection(route, httpContext);

		HttpClientConnection connection = null;

		try {
			
			connection = connRequest.get(10, TimeUnit.SECONDS);
			
			System.out.println("HttpClientConnectionManager#HttpClientConnection => " + connection);
			System.out.println("HttpClientConnectionManager#HttpRoute => " + route);

			if (!connection.isOpen()) {
				// establish connection based on route info
				connManager.connect(connection, route, 1000, httpContext);
				
				System.out.println("HttpClientConnectionManager#HttpClientConnection => " + connection);
				System.out.println("HttpClientConnectionManager#HttpRoute => " + route);

				// mark route as complete
				connManager.routeComplete(connection, route, httpContext);
				
				System.out.println("HttpClientConnectionManager#HttpClientConnection => " + connection);
			}

		} catch (ConnectionPoolTimeoutException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connManager.releaseConnection(connection, null, 10, TimeUnit.SECONDS);
			System.out.println("HttpClientConnectionManager#HttpClientConnection => " + connection);
		}

	}

}
