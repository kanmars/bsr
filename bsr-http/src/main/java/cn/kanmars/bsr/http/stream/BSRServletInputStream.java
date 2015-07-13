package cn.kanmars.bsr.http.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;

import javax.servlet.ServletInputStream;

public class BSRServletInputStream extends ServletInputStream{

	public String context= null;
	public InputStream is = null;
	
	public BSRServletInputStream(String context) {
		super();
		this.context = context;
		is = new StringBufferInputStream(context);
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

}
