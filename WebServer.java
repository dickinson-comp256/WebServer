import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * A super simple web server.
 */
public class WebServer {
	private static int PORT = 8085;

	public static void main(String[] args) throws IOException {

		// Open a server socket to listen for incoming requests on the PORT.
		ServerSocket inSocket = new ServerSocket(PORT);
		System.out.println("Server is running.");

		// This loop processes each request that is made.
		// One request is processed per iteration of the loop.
		boolean done = false;
		while (!done) {

			// Wait here for a request from a browser, and
			// establish a connection to the network socket
			System.out.println("Waiting for a request.");
			Socket requestConnection = inSocket.accept();

			// Get the input and output streams for the connection.
			// We will use these to read the request that was received
			// and to send the response back.
			InputStream requestStream = requestConnection.getInputStream();
			OutputStream responseStream = requestConnection.getOutputStream();

			// Check that the request has text in it, 
			// If so read it, generate a response, and send it.
			Scanner requestReader = new Scanner(requestStream);
			if (requestReader.hasNextLine()) {
				// Read the request and print it to the console.
				String requestText = requestReader.nextLine();
				System.out.println("Request:\t" + requestText);

				// Generate the response to the request.
				String responseText = "The server is <strong>up</strong>!";
				byte[] responseBytes = responseText.getBytes("UTF-8");
				
				// Send the response back through the socket to the browser.
				// Note: The sendHTTPResponse function is defined below and it
				// handles the details of sending a properly formed HTTP response.
				sendHTTPResponse(responseStream, "200 OK", "text/html", responseBytes);
			}

			// Close the Scanner and the socket, which also closes streams.
			requestReader.close();
			requestConnection.close();
		}

		// Close the server socket.
		// Note: We don't get here because the server runs forever.
		//       but if we don't put this here the compiler complains.
		inSocket.close();
	}

	/**
	 * Send an HTTP response back to the requester.
	 * 
	 * @param responseStream
	 *            the stream to which to write the response.
	 * @param status
	 *            the HTTP status for the response. The HTTP status codes are
	 *            defined by:
	 *            https://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-26#section-6
	 * @param contentType
	 *            the type of content that is contained in the response. The types
	 *            of content are registered at:
	 *            https://www.iana.org/assignments/media-types/media-types.xhtml
	 * @param content
	 *            the bytes representing the content. For text the bytes must
	 *            already be UTF encoded. For binary types (e.g. images) they are
	 *            simply the raw bytes from the image file.
	 * @throws IOException
	 */
	private static void sendHTTPResponse(OutputStream responseStream, String status, String contentType, byte[] content)
			throws IOException {
		responseStream.write(("HTTP/1.1 " + status + "\r\n").getBytes("UTF-8"));
		responseStream.write(("Content-Type: " + contentType + "\r\n").getBytes("UTF-8"));
		responseStream.write(("Content-Length: " + content.length + "\r\n").getBytes("UTF-8"));
		responseStream.write(("Connection: close\r\n").getBytes("UTF-8"));
		responseStream.write(("\r\n").getBytes("UTF-8")); // blank line after header.

		// Only send content if there is some.
		if (content != null && content.length > 0) {
			responseStream.write(content);
		}
	}
}
