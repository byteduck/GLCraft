package net.codepixl.GLCraft.util.logging;

import net.codepixl.GLCraft.util.LogSource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GLogger {
	private static PrintStream out, err;
	private static FileOutputStream lfos;
	public static RedirOut rout;

	public static void log(Object log, LogSource l) {
		if(log == null)
			log = "null";
		if(l != LogSource.SILENT)
			out.println(l.toString() + log);
		else
			try {
				lfos.write((l.toString() + log + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void log(Object log){
		if(log == null)
			log = "null";
		else
			try {
				lfos.write(("[" + Thread.currentThread().getStackTrace()[1] + "]" + log + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void logerr(Object log, LogSource l){
		if(log == null)
			log = "null";
		if(l != LogSource.SILENT)
			err.println(l.toString() + log);
		else
			try {
				lfos.write((l.toString() + log + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void logf(String log, LogSource l, Object...objects){
		if(l != LogSource.SILENT){
			out.print(l);
			out.printf(log, objects);
		}else
			try {
				lfos.write((l.toString() + log + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void init(FileOutputStream lfos){
		GLogger.lfos = lfos;
		out = System.out;
		rout = new RedirOut(out, System.err);
		System.setOut(rout);
		err = System.err;
	}

	public static class RedirOut extends PrintStream {

		public String warningString = " Please use GLogger.log instead.\n";
		private boolean suppressWarnings = false;
		
		public void setSuppressWarnings(boolean s){
			this.suppressWarnings = s;
		}
		
		public boolean getSuppressWarnings(){
			return this.suppressWarnings;
		}

		public RedirOut(OutputStream out, PrintStream err) {
			super(out);
		}

		@Override
		public void write(int arg0) {
			try {
				out.write(arg0);
				if(!suppressWarnings)
					err.write(warningString.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void write(byte[] b) {
			try {
				out.write(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
			write(0);
		}

		@Override
		public void write(byte[] b, int off, int len){
			try {
				out.write(b, off, len);
			} catch (IOException e) {
				e.printStackTrace();
			}
			write(0);
		}

	}
}
