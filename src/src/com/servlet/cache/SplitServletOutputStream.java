package com.servlet.cache;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class SplitServletOutputStream extends ServletOutputStream {
	OutputStream captureStream = null;
	OutputStream passThroughStream = null;

	/**
	 * Constructs a split output stream that both captures and passes through
	 * the servlet response.
	 * 
	 * @param captureStream
	 *            The stream that will be used to capture the data.
	 * @param passThroughStream
	 *            The pass-through <code>ServletOutputStream</code> that will
	 *            write the response to the client as originally intended.
	 */
	public SplitServletOutputStream(OutputStream captureStream,
			OutputStream passThroughStream) {
		this.captureStream = captureStream;
		this.passThroughStream = passThroughStream;
	}

	/**
	 * Writes the incoming data to both the output streams.
	 * 
	 * @param value
	 *            The int data to write.
	 * @throws IOException
	 */
	public void write(int value) throws IOException {
		captureStream.write(value);
		passThroughStream.write(value);
	}

	/**
	 * Writes the incoming data to both the output streams.
	 * 
	 * @param value
	 *            The bytes to write to the streams.
	 * @throws IOException
	 */
	public void write(byte[] value) throws IOException {
		captureStream.write(value);
		passThroughStream.write(value);
	}

	/**
	 * Writes the incoming data to both the output streams.
	 * 
	 * @param b
	 *            The bytes to write out to the streams.
	 * @param off
	 *            The offset into the byte data where writing should begin.
	 * @param len
	 *            The number of bytes to write.
	 * @throws IOException
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		captureStream.write(b, off, len);
		passThroughStream.write(b, off, len);
	}

	/**
	 * Flushes both the output streams.
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException {
		super.flush();
		captureStream.flush(); // why not?
		passThroughStream.flush();
	}

	/**
	 * Closes both the output streams.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		super.close();
		captureStream.close();
		passThroughStream.close();
	}
}