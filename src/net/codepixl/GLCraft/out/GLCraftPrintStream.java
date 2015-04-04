package net.codepixl.GLCraft.out;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.nishu.utils.Camera;

public class GLCraftPrintStream extends PrintStream
{
	OutputStream out;
    public GLCraftPrintStream(OutputStream out)
    {
        super(out, true);
        this.out = out;
    }
    @Override
    public void print(String s)
    {//do what ever you like
    	try {
			out.write("printed".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        super.print(s);
    }
    @Override
    public void println(String s)
    {//do what ever you like
        super.print(s);
    }
}
