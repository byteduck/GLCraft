package net.codepixl.GLCraft.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compressor{
	public static byte[] compress(byte[] input) throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream gzipper = new GZIPOutputStream(bout);
		gzipper.write(input, 0, input.length);
		gzipper.close();
		return bout.toByteArray();
	}

	public static byte[] decompress(byte[] input) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPInputStream gzipper = new GZIPInputStream(new ByteArrayInputStream(input));
		int read;
		while((read = gzipper.read()) > -1){
			out.write(read);
		}
		gzipper.close();
		out.close();
		return out.toByteArray();
	}
}