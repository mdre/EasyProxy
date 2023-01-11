package asm.proxy;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Dumper {
    public static void main(final String[] args) throws Exception {
        ClassReader cr;
        cr = new ClassReader("asm.proxy.ASMFooTarget");
        FileWriter fr = new FileWriter("/tmp/1/ASMFooTargetDumped.java");
        Printer printer = new ASMifier();
        //        Printer printer = new Textifier();
        cr.accept(new TraceClassVisitor(null, printer, new PrintWriter(fr)), ClassReader.SKIP_DEBUG);
    }

}


