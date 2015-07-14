package cn.kanmars.bsr.http.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class BSRServletOutputStream extends ServletOutputStream{
	private ByteArrayOutputStream bao = new ByteArrayOutputStream();
	@Override
	public void write(int b) throws IOException {
		bao.write(b);
	}
	
	public byte[] getContentBytes(){
		return bao.toByteArray();
	}

}
