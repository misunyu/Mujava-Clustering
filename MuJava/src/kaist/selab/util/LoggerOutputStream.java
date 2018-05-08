package kaist.selab.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Standard Output을 Logger로 보내기 위한 Stream.
 * 
 * @author swkim
 * 
 */
public class LoggerOutputStream extends OutputStream {
	public static final String ERR = "err";
	public static final String OUT = "out";
	ByteArrayOutputStream stream;
	private String type;

	public LoggerOutputStream(String type) {
		stream = new ByteArrayOutputStream();
		this.type = type;
	}

	public void write(int b) throws IOException {
		stream.write(b);
	}

	public void close() throws IOException {
		stream = null;
	}

	public void flush() throws IOException {
		if (ERR.equals(type))
			MuJavaLogger.getLogger().error(new String(stream.toByteArray()));
		if (OUT.equals(type))
			MuJavaLogger.getLogger().debug(new String(stream.toByteArray()));
	}

	public void write(byte[] b, int off, int len) throws IOException {
		stream.write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		stream.write(b);
	}

}
