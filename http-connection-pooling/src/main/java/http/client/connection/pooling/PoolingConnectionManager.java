/**
 * 
 */
package http.client.connection.pooling;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

/**
 * @author Ramesh
 *
 */
public class PoolingConnectionManager {

	
	/**
	 * Run with -Djava.util.logging.config.file=http-connection-pooling/src/main/resources/logging.properties
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

		String[] urls = { "http://google.com", "http://google.com", "http://bing.com", "http://apple.com" };

		GetThread[] threads = new GetThread[urls.length];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new GetThread(httpClient, HttpClientContext.create(), new HttpGet(urls[i]));
		}

		for (GetThread thread : threads) {
			thread.start();
		}

		System.out.println("1 => Pooling connection manager stats " + connectionManager.getTotalStats());

		for (GetThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("2 => Pooling connection manager stats " + connectionManager.getTotalStats());

		connectionManager.close();
	}

	static class GetThread extends Thread {

		private final CloseableHttpClient httpClient;
		private final HttpContext context;
		private final HttpGet httpGet;

		/**
		 * @param httpClient
		 * @param context
		 * @param httpGet
		 */
		public GetThread(CloseableHttpClient httpClient, HttpContext context, HttpGet httpGet) {
			this.httpClient = httpClient;
			this.context = context;
			this.httpGet = httpGet;
		}

		public void run() {

			CloseableHttpResponse httpResponse = null;

			try {
				httpResponse = httpClient.execute(httpGet, context);
				try {
					HttpEntity entity = httpResponse.getEntity();
				} finally {
					httpResponse.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
