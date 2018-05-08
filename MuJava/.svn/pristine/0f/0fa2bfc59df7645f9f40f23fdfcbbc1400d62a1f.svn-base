package kaist.selab.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {
	JTextArea area = null;
	public TextAreaOutputStream(JTextArea area) {
		assert(area != null);
		this.area = area;
	}
	
	public void write(int b) throws IOException {
		area.append(Byte.toString((byte)b));
	}

	public void close() throws IOException {
		
	}

	public void flush() throws IOException {
		area.repaint();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		String str = new String(b);
		String result = str.substring(off, off+len);
		area.append(result);
	}
	public void write(byte[] b) throws IOException {
		area.append(new String(b));
	}

}
