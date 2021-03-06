/*
 * AbclScriptEngine.java
 *
 * Copyright (C) 2008 Alessio Stalla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.armedbear.lisp.scripting;

import static org.armedbear.lisp.SymbolConstants.*;
import static org.armedbear.lisp.Lisp.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Map;
import java.util.Properties;

import javax.script.*;

import org.armedbear.lisp.*;
import org.armedbear.lisp.scripting.util.ReaderInputStream;
import org.armedbear.lisp.scripting.util.WriterOutputStream;


public class AbclScriptEngine extends AbstractScriptEngine implements Invocable, Compilable {

    private Interpreter interpreter;
    /**
     * The function used to evaluate a string of code.
     */
    private Function evalScript;
    /**
     * The function used to evaluate a Lisp function.
     */
    private Function evalFunction;
    /**
     * The function used to compile Lisp code.
     */
    private Function compileScript;
    /**
     * The function used to evaluate a compiled script.
     */
  /*private*/ Function evalCompiledScript;

    protected AbclScriptEngine() {
	interpreter = Interpreter.getInstance();
	if(interpreter == null) {
	    interpreter = Interpreter.createInstance();
	}
	try {
	    loadFromClasspath("/org/armedbear/lisp/scripting/lisp/packages.lisp");
	    loadFromClasspath("/org/armedbear/lisp/scripting/lisp/abcl-script.lisp");
	    loadFromClasspath("/org/armedbear/lisp/scripting/lisp/config.lisp");
	    if(getClass().getResource("/abcl-script-config.lisp") != null) {
		System.out.println("ABCL: loading configuration from " + getClass().getResource("/abcl-script-config.lisp"));
		loadFromClasspath("/abcl-script-config.lisp");
	    }
	    ((Function) interpreter.eval("#'abcl-script:configure-abcl")).execute(makeNewJavaObject(this));
	    System.out.println("ABCL: configured");
	    evalScript = (Function) this.findSymbol("EVAL-SCRIPT", "ABCL-SCRIPT").getSymbolFunction();
	    compileScript = (Function) this.findSymbol("COMPILE-SCRIPT", "ABCL-SCRIPT").getSymbolFunction();
	    evalCompiledScript = (Function) this.findSymbol("EVAL-COMPILED-SCRIPT", "ABCL-SCRIPT").getSymbolFunction();
	    evalFunction = (Function) this.findSymbol("EVAL-FUNCTION", "ABCL-SCRIPT").getSymbolFunction();
	} catch (ConditionThrowable e) {
	    throw new RuntimeException(e);
	}
    }
    
    public Interpreter getInterpreter() {
	return interpreter;
    }
    
    public void setStandardInput(InputStream stream, LispThread thread) {
	thread.setSpecialVariable(STANDARD_INPUT, new Stream(stream,CHARACTER, true));
    }
    
    public void setStandardInput(InputStream stream) {
	setStandardInput(stream, LispThread.currentThread());
    }
    
    public void setInterpreter(Interpreter interpreter) {
	this.interpreter = interpreter;
    }

    public static String escape(String s) {
	StringBuffer b = new StringBuffer();
	int len = s.length();
	char c;
	for (int i = 0; i < len; ++i) {
	    c = s.charAt(i);
	    if (c == '\\' || c == '"') {
		b.append('\\');
	    }
	    b.append(c);
	}
	return b.toString();
    }

	public LispObject loadFromClasspath(String classpathResource) throws ConditionThrowable {
		InputStream istream = getClass().getResourceAsStream(classpathResource);
		Stream stream = new Stream(istream, CHARACTER);
		return load(stream);
	}

	public LispObject load(Stream stream) throws ConditionThrowable {
		Symbol keyword_verbose = Lisp.internKeyword("VERBOSE");
		Symbol keyword_print = Lisp.internKeyword("PRINT");
		/*
		 * load (filespec &key (verbose *load-verbose*) (print *load-print*)
		 * (if-does-not-exist t) (external-format :default)
		 */
		return LOAD.getSymbolFunction().execute(
				new LispObject[] { stream, keyword_verbose, Lisp.NIL,
						keyword_print, Lisp.T, Keyword.IF_DOES_NOT_EXIST,
						Lisp.T, Keyword.EXTERNAL_FORMAT, Keyword.DEFAULT });
	}

	public LispObject load(String filespec) throws ConditionThrowable {
		return load(filespec, true);
	}

	public LispObject load(String filespec, boolean compileIfNecessary)	throws ConditionThrowable {
		if (isCompiled(filespec) || !compileIfNecessary) {
			return interpreter.eval("(load \"" + escape(filespec) + "\")");
		} else {
			return compileAndLoad(filespec);
		}
	}

	public static boolean isCompiled(String filespec) {
		if (filespec.endsWith(".abcl")) {
			return true;
		}
		File source;
		File compiled;
		if (filespec.endsWith(".lisp")) {
			source = new File(filespec);
			compiled = new File(filespec.substring(0, filespec.length() - 5)
					+ ".abcl");
		} else {
			source = new File(filespec + ".lisp");
			compiled = new File(filespec + ".abcl");
		}
		if (!source.exists()) {
			throw new IllegalArgumentException("The source file " + filespec + " cannot be found");
		}
		return compiled.exists()
				&& compiled.lastModified() >= source.lastModified();
	}

	public LispObject compileFile(String filespec) throws ConditionThrowable {
		return interpreter.eval("(compile-file \"" + escape(filespec) + "\")");
	}

	public LispObject compileAndLoad(String filespec) throws ConditionThrowable {
		return interpreter.eval("(load (compile-file \"" + escape(filespec)	+ "\"))");
	}

	public static boolean functionp(LispObject obj) {
		return obj instanceof Function;
	}

	public IJavaObject jsetq(String symbol, Object value) throws ConditionThrowable {
		Symbol s = findSymbol(symbol);
		IJavaObject jo;
		if (value instanceof IJavaObject) {
			jo = (IJavaObject) value;
		} else {
			jo = makeNewJavaObject(value);
		}
		s.setSymbolValue((LispObject)jo);
		return jo;
	}

	public Symbol findSymbol(String name, String pkg) throws ConditionThrowable {
		Cons values = (Cons) (interpreter.eval("(cl:multiple-value-list (find-symbol (symbol-name '#:"
											   + escape(name) + ")" + (pkg == null ? "" : " :" + escape(pkg))
											   + "))"));
		if(values.CADR() == Lisp.NIL) {
			return null;
		} else {
			return (Symbol) values.CAR();
		}
	}

	public Symbol findSymbol(String name) throws ConditionThrowable {
		//Known bug: doesn't handle escaped ':' e.g. |a:b|
		int i = name.indexOf(':');
		if(i < 0) { 
			return findSymbol(name, null);
		} else {
		    if((i < name.length() - 1) && (name.charAt(i + 1) == ':')) {
			return findSymbol(name.substring(i + 2), name.substring(0, i));
		    } else {
			return findSymbol(name.substring(i + 1), name.substring(0, i));
		    }
		}
	}
	
	public Function findFunction(String name) throws ConditionThrowable {
		return (Function) interpreter.eval("#'" + name);
	}

	//@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	private static LispObject makeBindings(Bindings bindings) throws ConditionThrowable {
		if (bindings == null || bindings.size() == 0) {
			return Lisp.NIL;
		}
		LispObject[] argList = new LispObject[bindings.size()];
		int i = 0;
		for (Map.Entry<String, Object> entry : bindings.entrySet()) {
			argList[i++] = CONS.execute(new SimpleString(entry.getKey()),
							   JavaObject.getInstance(entry.getValue(), true));
		}
		return LIST.getSymbolFunction().execute(argList);
	}

   /*private*/ Object eval(Function evaluator, LispObject code, ScriptContext ctx) throws ScriptException {
	ReaderInputStream in = null;
	WriterOutputStream out = null;
	LispObject retVal = null;
	try {
	    in = new ReaderInputStream(ctx.getReader());
	    out = new WriterOutputStream(ctx.getWriter());
	    Stream outStream = new Stream(out, CHARACTER);
	    Stream inStream  = new Stream(in,  CHARACTER);
	    retVal = evaluator.execute(makeBindings(ctx.getBindings(ScriptContext.GLOBAL_SCOPE)),
				       makeBindings(ctx.getBindings(ScriptContext.ENGINE_SCOPE)),
				       inStream, outStream,
				       code, makeNewJavaObject(ctx));
	    return retVal.javaInstance();
	} catch (ConditionThrowable e) {
	    throw new ScriptException(new Exception(e));
	} catch (IOException e) {
	    throw new ScriptException(e);
	}
    }
	
	//@Override
	public Object eval(String code, ScriptContext ctx) throws ScriptException {
		return eval(evalScript, new SimpleString(code), ctx);
	}

	private static String toString(Reader reader) throws IOException {
		StringWriter w = new StringWriter();
		int i;
		i = reader.read();
		while (i != -1) {
			w.write(i);
			i = reader.read();
		}
		return w.toString();
	}
	
	//@Override
	public Object eval(Reader code, ScriptContext ctx) throws ScriptException {
		try {
			return eval(toString(code), ctx);
		} catch (IOException e) {
			return new ScriptException(e);
		}
	}

	//@Override
	public ScriptEngineFactory getFactory() {
		return new AbclScriptEngineFactory();
	}
	
	//@Override
	public <T> T getInterface(Class<T> clasz) {
		try {
			return getInterface(eval("(cl:find-package '#:ABCL-SCRIPT-USER)"), clasz);
		} catch (ScriptException e) {
			throw new Error(e);
		}
	}

	@SuppressWarnings("unchecked")
	//@Override
	public <T> T getInterface(Object thiz, Class<T> clasz) {
		try {
			Symbol s = findSymbol("jmake-proxy", "JAVA");
			IJavaObject iface = makeNewJavaObject(clasz);
			return (T) ((IJavaObject) s.execute((LispObject)iface, (LispObject) thiz)).javaInstance();
		} catch (ConditionThrowable e) {
			throw new Error(e);
		}
	}	
	
    //@Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
	try {
	    Symbol s;
	    if(name.indexOf(':') >= 0) {
		s = findSymbol(name);
	    } else {
		s = findSymbol(name, "ABCL-SCRIPT-USER");
	    }
	    if(s != null) {
		LispObject f = s.getSymbolFunction();
		if(f != null && f instanceof Function) {
		    LispObject functionAndArgs = Lisp.NIL.push(f);
		    for(int i = 0; i < args.length; ++i) {
			functionAndArgs = functionAndArgs.push(JavaObject.getInstance(args[i], true));
		    }
		    functionAndArgs = functionAndArgs.reverse();
		    return eval(evalFunction, functionAndArgs, getContext());
		} else {
		    throw new NoSuchMethodException(name);
		}
	    } else {
		throw new NoSuchMethodException(name);
	    }
	} catch (ConditionThrowable e) {
	    throw new ScriptException(new RuntimeException(e));
	}
    }

    //@Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
	throw new UnsupportedOperationException("Common Lisp does not have methods in the Java sense.");
    }

    public class AbclCompiledScript extends CompiledScript {

	private LispObject function;
	
	public AbclCompiledScript(LispObject function) {
	    this.function = function;
	}
	
	@Override
	public Object eval(ScriptContext context) throws ScriptException {
	    return AbclScriptEngine.this.eval(evalCompiledScript, function, context);
	}
	
	@Override
	public ScriptEngine getEngine() {
	    return AbclScriptEngine.this;
	}
	
    }

	
	//@Override
	public CompiledScript compile(String script) throws ScriptException {
		try {
		    Function f = (Function) compileScript.execute(new SimpleString(script));
		    return new AbclCompiledScript(f);
		} catch (ConditionThrowable e) {
			throw new ScriptException(new Exception(e));
		} catch(ClassCastException e) {
			throw new ScriptException(e);
		}
	}

	//@Override
	public CompiledScript compile(Reader script) throws ScriptException {
		try {
			return compile(toString(script));
		} catch (IOException e) {
			throw new ScriptException(e);
		}
	}

}
