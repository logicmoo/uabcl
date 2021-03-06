/*
 * LispThread.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: LispThread.java 12105 2009-08-19 14:51:56Z mevenson $
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
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */

package org.armedbear.lisp;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import static org.armedbear.lisp.Nil.NIL;
import static org.armedbear.lisp.Lisp.*;

public final class LispThread extends AbstractLispObject implements UncaughtExceptionHandler
{

    /*public*/ static boolean use_fast_calls = false;

    // use a concurrent hashmap: we may want to add threads
    // while at the same time iterating the hash
    final /*public*/ static ConcurrentHashMap<Thread,LispThread> map = new ConcurrentHashMap<Thread,LispThread>();

//    public static ThreadLocal<LispThread> threads = new ThreadLocal<LispThread>(){
//        @Override
//        public LispThread initialValue() {
//            Thread thisThread = Thread.currentThread();
//            LispThread thread = LispThread.map.get(thisThread);
//            if (thread == null) {
//                thread = new LispThread(thisThread);
//                LispThread.map.put(thisThread,thread);
//            }
//            return thread;
//        }
//    };

    static int threadCount = 0;
    static LispThread lastCreated;
    public static final LispThread currentThread()
    {
    	if (threadCount==1) {    		
    		return lastCreated;
    	}
    	Thread thisThread = Thread.currentThread();
        UncaughtExceptionHandler uc = thisThread.getUncaughtExceptionHandler();
        if (uc instanceof LispThread) return (LispThread)uc;        
        LispThread thread = new LispThread(thisThread);   
        LispThread.map.put(thisThread,thread);
        thisThread.setUncaughtExceptionHandler(thread);
        return thread;
    }

  /*public*/ final Thread javaThread;
    public boolean destroyed;
  /*public*/ final LispObject name;
    public SpecialBinding lastSpecialBinding;
    public LispObject[] _values;
    /*public*/ boolean threadInterrupted;
    /*public*/ LispObject pending = NIL;

    public LispThread(Thread javaThread)
    {
    	threadCount++;
    	lastCreated = this;
        this.javaThread = javaThread;
        name = new SimpleString(javaThread.getName());
    }

    public LispThread(final Function fun, LispObject name)
    {
    	threadCount++;
    	lastCreated = this;
        Runnable r = new Runnable() {
            public void run()
            {            	
                try {
                    funcall(fun, new LispObject[0], LispThread.this);
                }
                catch (ThreadDestroyed ignored) {
                      // Might happen.
                }
                catch (Throwable t) {
                    if (isInterrupted()) {
                        try {
                            processThreadInterrupts();
                        }
                        catch (ConditionThrowable c) {
                            Debug.trace(c);
                        }
                    }
                }
                finally {
                    // make sure the thread is *always* removed from the hash again
                	threadCount--;
                    map.remove(Thread.currentThread());
                }
            }
        };
        javaThread = new Thread(r);
        this.name = name;
        map.put(javaThread, this);
        try {        	
        	if (name != NIL)
        		javaThread.setName(name.getStringValue());
        } catch (ConditionThrowable ex) {
            Debug.trace("Failed to set thread name:");
	    Debug.trace(ex);
        }
        javaThread.setDaemon(true);
        javaThread.start();
    }

    public StackTraceElement[] getJavaStackTrace() {
        return javaThread.getStackTrace();
    }

    @Override
    public LispObject typeOf()
    {
        return SymbolConstants.THREAD;
    }

    @Override
    public LispObject classOf()
    {
        return BuiltInClass.THREAD;
    }

    @Override
    public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
    {
        if (typeSpecifier == SymbolConstants.THREAD)
            return T;
        if (typeSpecifier == BuiltInClass.THREAD)
            return T;
        return super.typep(typeSpecifier);
    }

    public final synchronized boolean isDestroyed()
    {
        return destroyed;
    }

    public final synchronized boolean isInterrupted()
    {
        return threadInterrupted;
    }

    public final synchronized void setDestroyed(boolean b)
    {
        destroyed = b;
    }

    public final synchronized void interrupt(LispObject function, LispObject args)
    {
        pending = makeCons(args, pending);
        pending = makeCons(function, pending);
        threadInterrupted = true;
        javaThread.interrupt();
    }

    public final synchronized void processThreadInterrupts()
        throws ConditionThrowable
    {
        while (pending != NIL) {
            LispObject function = pending.CAR();
            LispObject args = pending.CADR();
            pending = pending.CDDR();
            Primitives.APPLY.execute(function, args);
        }
        threadInterrupted = false;
    }

    public final LispObject[] getValues()
    {
        return _values;
    }

    public final LispObject[] getValues(LispObject result, int count)
    {
        if (_values == null) {
            LispObject[] values = new LispObject[count];
            if (count > 0)
                values[0] = result;
            for (int i = 1; i < count; i++)
                values[i] = NIL;
            return values;
        }
        // If the caller doesn't want any extra values, just return the ones
        // we've got.
        if (count <= _values.length)
            return _values;
        // The caller wants more values than we have. Pad with NILs.
        LispObject[] values = new LispObject[count];
        for (int i = _values.length; i-- > 0;)
            values[i] = _values[i];
        for (int i = _values.length; i < count; i++)
            values[i] = NIL;
        return values;
    }

    // Used by the JVM compiler for MULTIPLE-VALUE-CALL.
    public final LispObject[] accumulateValues(LispObject result,
                                               LispObject[] oldValues)
    {
        if (oldValues == null) {
            if (_values != null)
                return _values;
            LispObject[] values = new LispObject[1];
            values[0] = result;
            return values;
        }
        if (_values != null) {
            if (_values.length == 0)
                return oldValues;
            final int totalLength = oldValues.length + _values.length;
            LispObject[] values = new LispObject[totalLength];
            System.arraycopy(oldValues, 0,
                             values, 0,
                             oldValues.length);
            System.arraycopy(_values, 0,
                             values, oldValues.length,
                             _values.length);
            return values;
        }
        // _values is null.
        final int totalLength = oldValues.length + 1;
        LispObject[] values = new LispObject[totalLength];
        System.arraycopy(oldValues, 0,
                         values, 0,
                         oldValues.length);
        values[totalLength - 1] = result;
        return values;
    }

    public final LispObject setValues()
    {
        _values = new LispObject[0];
        return NIL;
    }

    public final LispObject setValues(LispObject value1)
    {
        _values = null;
        return value1;
    }

    public final LispObject setValues(LispObject value1, LispObject value2)
    {
        _values = new LispObject[2];
        _values[0] = value1;
        _values[1] = value2;
        return value1;
    }

    public final LispObject setValues(LispObject value1, LispObject value2,
                                      LispObject value3)
    {
        _values = new LispObject[3];
        _values[0] = value1;
        _values[1] = value2;
        _values[2] = value3;
        return value1;
    }

    public final LispObject setValues(LispObject value1, LispObject value2,
                                      LispObject value3, LispObject value4)
    {
        _values = new LispObject[4];
        _values[0] = value1;
        _values[1] = value2;
        _values[2] = value3;
        _values[3] = value4;
        return value1;
    }

    public final LispObject setValues(LispObject[] values)
    {
        switch (values.length) {
            case 0:
                _values = values;
                return NIL;
            case 1:
                _values = null;
                return values[0];
            default:
                _values = values;
                return values[0];
        }
    }

    public final void clearValues()
    {
        _values = null;
    }

    public final LispObject nothing()
    {
        _values = new LispObject[0];
        return NIL;
    }

    // Forces a single value, for situations where multiple values should be
    // ignored.
    public final LispObject value(LispObject obj)
    {
        _values = null;
        return obj;
    }

    public final void bindSpecial(Symbol name, LispObject value)
    {
        lastSpecialBinding = new SpecialBinding(name, value, lastSpecialBinding);
    }

    public final void bindSpecialToCurrentValue(Symbol name)
    {
        SpecialBinding binding = lastSpecialBinding;
        while (binding != null) {
            if (binding.name == name) {
                lastSpecialBinding =
                    new SpecialBinding(name, binding.value, lastSpecialBinding);
                return;
            }
            binding = binding.next;
        }
        // Not found.
        lastSpecialBinding =
            new SpecialBinding(name, name.getSymbolValue(), lastSpecialBinding);
    }

    /** Looks up the value of a special binding in the context of the
     * given thread.
     *
     * In order to find the value of a special variable (in general),
     * use {@link Symbol#symbolValue}.
     *
     * @param name The name of the special variable, normally a symbol
     * @return The inner most binding of the special, or null if unbound
     *
     * @see Symbol#symbolValue
     */
    public final LispObject lookupSpecial(LispObject name)
    {
        SpecialBinding binding = lastSpecialBinding;
        while (binding != null) {
            if (binding.name == name)
                return binding.value;
            binding = binding.next;
        }
        return null;
    }

    public final SpecialBinding getSpecialBinding(LispObject name)
    {
        SpecialBinding binding = lastSpecialBinding;
        while (binding != null) {
            if (binding.name == name)
                return binding;
            binding = binding.next;
        }
        return null;
    }

    public final LispObject setSpecialVariable(Symbol name, LispObject value)
    {
        SpecialBinding binding = lastSpecialBinding;
        while (binding != null) {
            if (binding.name == name) {
                binding.value = value;
                return value;
            }
            binding = binding.next;
        }
        name.setSymbolValue(value);
        return value;
    }

    public final LispObject pushSpecial(Symbol name, LispObject thing)
        throws ConditionThrowable
    {
        SpecialBinding binding = lastSpecialBinding;
        while (binding != null) {
            if (binding.name == name) {
                LispObject newValue = makeCons(thing, binding.value);
                binding.value = newValue;
                return newValue;
            }
            binding = binding.next;
        }
        LispObject value = name.getSymbolValue();
        if (value != null) {
            LispObject newValue = makeCons(thing, value);
            name.setSymbolValue(newValue);
            return newValue;
        } else
            return error(new UnboundVariable(name));
    }

    // Returns symbol value or NIL if unbound.
    public final LispObject safeSymbolValue(Symbol name)
    {
        SpecialBinding binding = lastSpecialBinding;
        while (binding != null) {
            if (binding.name == name)
                return binding.value;
            binding = binding.next;
        }
        LispObject value = name.getSymbolValue();
        return value != null ? value : NIL;
    }

    public final void rebindSpecial(Symbol name, LispObject value)
    {
        SpecialBinding binding = getSpecialBinding(name);
        binding.value = value;
    }

    public LispObject catchTags = NIL;

    public void pushCatchTag(LispObject tag) throws ConditionThrowable
    {
        catchTags = makeCons(tag, catchTags);
    }

    public void popCatchTag() throws ConditionThrowable
    {
        if (catchTags != NIL)
            catchTags = catchTags.CDR();
        else
            Debug.assertTrue(false);
    }

    public void throwToTag(LispObject tag, LispObject result)
        throws ConditionThrowable
    {
        LispObject rest = catchTags;
        while (rest != NIL) {
            if (rest.CAR() == tag)
                throw new Throw(tag, result, this);
            rest = rest.CDR();
        }
        error(new ControlError("Attempt to throw to the nonexistent tag " +
                                tag.writeToString() + "."));
    }


    public StackFrame stack = null;

    @Deprecated
    public LispObject getStack()
    {
        return NIL;
    }

    @Deprecated
    public void setStack(LispObject stack)
    {
    }

    public final void pushStackFrame(StackFrame frame) 
	throws ConditionThrowable
    {
	frame.setNext(stack);
	stack = frame;
    }


    public final void popStackFrame()
    {
        if (stack != null)
            stack = stack.getNext();
    }

    public void resetStack()
    {
        stack = null;
    }

    @Override
    public LispObject execute(LispObject function) throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute();

        pushStackFrame(new LispStackFrame(function));
        try {
            return function.execute();
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject arg)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(arg);

        pushStackFrame(new LispStackFrame(function, arg));
        try {
            return function.execute(arg);
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject first,
                              LispObject second)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second);

        pushStackFrame(new LispStackFrame(function, first, second));
        try {
            return function.execute(first, second);
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject first,
                              LispObject second, LispObject third)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second, third);

        pushStackFrame(new LispStackFrame(function, first, second, third));
        try {
            return function.execute(first, second, third);
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject first,
                              LispObject second, LispObject third,
                              LispObject fourth)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second, third, fourth);

        pushStackFrame(new LispStackFrame(function, first, second, third, fourth));
        try {
            return function.execute(first, second, third, fourth);
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject first,
                              LispObject second, LispObject third,
                              LispObject fourth, LispObject fifth)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second, third, fourth, fifth);

        pushStackFrame(new LispStackFrame(function, first, second, third, fourth, fifth));
        try {
            return function.execute(first, second, third, fourth, fifth);
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject first,
                              LispObject second, LispObject third,
                              LispObject fourth, LispObject fifth,
                              LispObject sixth)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second, third, fourth, fifth, sixth);

        pushStackFrame(new LispStackFrame(function, first, second, 
					  third, fourth, fifth, sixth));
        try {
            return function.execute(first, second, third, fourth, fifth, sixth);
        }
        finally {
            popStackFrame();
        }
    }

    @Override
    public LispObject execute(LispObject function, LispObject first,
                              LispObject second, LispObject third,
                              LispObject fourth, LispObject fifth,
                              LispObject sixth, LispObject seventh)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second, third, fourth, fifth, sixth,
                                    seventh);

        pushStackFrame(new LispStackFrame(function, first, second, third, 
					  fourth, fifth, sixth, seventh));
        try {
            return function.execute(first, second, third, fourth, fifth, sixth,
                                    seventh);
        }
        finally {
            popStackFrame();
        }
    }

    public LispObject execute(LispObject function, LispObject first,
                              LispObject second, LispObject third,
                              LispObject fourth, LispObject fifth,
                              LispObject sixth, LispObject seventh,
                              LispObject eighth)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(first, second, third, fourth, fifth, sixth,
                                    seventh, eighth);

        pushStackFrame(new LispStackFrame(function, first, second, third, 
					  fourth, fifth, sixth, seventh, eighth));
        try {
            return function.execute(first, second, third, fourth, fifth, sixth,
                                    seventh, eighth);
        }
        finally {
            popStackFrame();
        }
    }

    public LispObject execute(LispObject function, LispObject[] args)
        throws ConditionThrowable
    {
        if (use_fast_calls)
            return function.execute(args);

        pushStackFrame(new LispStackFrame(function, args));
        try {
            return function.execute(args);
        }
        finally {
            popStackFrame();
        }
    }

    public void printBacktrace()
    {
        printBacktrace(0);
    }

    public void printBacktrace(int limit)
    {
        if (stack != null) {
            try {
                int count = 0;
                Stream out =
                    checkCharacterOutputStream(SymbolConstants.TRACE_OUTPUT.symbolValue());
                out._writeLine("Evaluation stack:");
                out._finishOutput();

                StackFrame s = stack;
                while (s != null) {
                    out._writeString("  ");
                    out._writeString(String.valueOf(count));
                    out._writeString(": ");
                    
                    pprint(s.toLispList(), out.getCharPos(), out);
                    out.terpri();
                    out._finishOutput();
                    if (limit > 0 && ++count == limit)
                        break;
                    s = s.next;
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public LispObject backtrace(int limit) throws ConditionThrowable
    {
        LispObject result = NIL;
        if (stack != null) {
            int count = 0;
            try {
                StackFrame s = stack;
                while (s != null) {
                    result = result.push(s);
                    if (limit > 0 && ++count == limit)
                        break;
                    s = s.getNext();
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return result.nreverse();
    }

    public void incrementCallCounts() throws ConditionThrowable
    {
        StackFrame s = stack;

        for (int i = 0; i < 8; i++) {
            if (s == null)
                break;
	    if (s instanceof LispStackFrame) {
		LispObject operator = ((LispStackFrame)s).getOperator();
		if (operator != null) {
		    operator.incrementHotCount();
		    operator.incrementCallCount();
		}
		s = s.getNext();
	    }
        }

        while (s != null) {
	    if (s instanceof LispStackFrame) {
		LispObject operator = ((LispStackFrame)s).getOperator();
		if (operator != null)
		    operator.incrementCallCount();
	    }
	    s = s.getNext();
        }
    }

    public static void pprint(LispObject obj, int indentBy, Stream stream)
        throws ConditionThrowable
    {
        if (stream.getCharPos() == 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < indentBy; i++)
                sb.append(' ');
            stream._writeString(sb.toString());
        }
        String raw = obj.writeToString();
        if (stream.getCharPos() + raw.length() < 80) {
            // It fits.
            stream._writeString(raw);
            return;
        }
        // Object doesn't fit.
        if (obj instanceof Cons) {
            try {
                boolean newlineBefore = false;
                LispObject[] array = obj.copyToArray();
                if (array.length > 0) {
                    LispObject first = array[0];
                    if (first == SymbolConstants.LET) {
                        newlineBefore = true;
                    }
                }
                int charPos = stream.getCharPos();
                if (newlineBefore && charPos != indentBy) {
                    stream.terpri();
                    charPos = stream.getCharPos();
                }
                if (charPos < indentBy) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = charPos; i < indentBy; i++)
                        sb.append(' ');
                    stream._writeString(sb.toString());
                }
                stream.print('(');
                for (int i = 0; i < array.length; i++) {
                    pprint(array[i], indentBy + 2, stream);
                    if (i < array.length - 1)
                        stream.print(' ');
                }
                stream.print(')');
            }
            catch (ConditionThrowable t) {
                Debug.trace(t);
            }
        } else {
            stream.terpri();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < indentBy; i++)
                sb.append(' ');
            stream._writeString(sb.toString());
            stream._writeString(raw);
            return;
        }
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        StringBuffer sb = new StringBuffer("THREAD");
        if (name != NIL) {
            sb.append(" \"");
            sb.append(name.getStringValue());
            sb.append("\"");
        }
        return unreadableString(sb.toString());
    }

    // ### make-thread
    public static final Primitive MAKE_THREAD =
        new Primitive("make-thread", PACKAGE_THREADS, true, "function &optional &key name")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            final int length = args.length;
            if (length == 0)
                error(new WrongNumberOfArgumentsException(this));
            LispObject name = NIL;
            if (length > 1) {
                if ((length - 1) % 2 != 0)
                    error(new ProgramError("Odd number of keyword arguments."));
                if (length > 3)
                    error(new WrongNumberOfArgumentsException(this));
                if (args[1] == Keyword.NAME)
                    name = args[2].STRING();
                else
                    error(new ProgramError("Unrecognized keyword argument " +
                                            args[1].writeToString() + "."));
            }
            return new LispThread(checkFunction(args[0]), name);
        }
    };

    // ### threadp
    public static final Primitive THREADP =
        new Primitive("threadp", PACKAGE_THREADS, true, "object",
		      "Boolean predicate as whether OBJECT is a thread.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return arg instanceof LispThread ? T : NIL;
        }
    };

    // ### thread-alive-p
    public static final Primitive THREAD_ALIVE_P =
        new Primitive("thread-alive-p", PACKAGE_THREADS, true, "thread",
		      "Boolean predicate whether THREAD is alive.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            final LispThread lispThread;
            if (arg instanceof LispThread) {
                lispThread = (LispThread) arg;
            }
            else {
                return type_error(arg, SymbolConstants.THREAD);
            }
            return lispThread.javaThread.isAlive() ? T : NIL;
        }
    };

    // ### thread-name
    public static final Primitive THREAD_NAME =
        new Primitive("thread-name", PACKAGE_THREADS, true, "thread",
		      "Return the name of THREAD if it has one.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
                if (arg instanceof LispThread) {
                return ((LispThread)arg).name;
            }
                 return type_error(arg, SymbolConstants.THREAD);
        }
    };

    public static final long javaSleepInterval(LispObject lispSleep)
            throws ConditionThrowable
    {
        double d =
            checkDoubleFloat(lispSleep.multiplyBy(NumericLispObject.createDoubleFloat((double)1000))).getValue();
        if (d < 0)
            type_error(lispSleep, list(SymbolConstants.REAL, Fixnum.ZERO));

        return (d < Long.MAX_VALUE ? (long) d : Long.MAX_VALUE);
    }

    // ### sleep
    public static final Primitive SLEEP = new Primitive("sleep", PACKAGE_CL, true, "seconds",
							 "Causes the invoking thread to sleep for SECONDS seconds.\nSECONDS may be a value between 0 1and 1.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {

            try {
                Thread.sleep(javaSleepInterval(arg));
            }
            catch (InterruptedException e) {
                currentThread().processThreadInterrupts();
            }
            return NIL;
        }
    };

    // ### mapcar-threads
    public static final Primitive MAPCAR_THREADS =
        new Primitive("mapcar-threads", PACKAGE_THREADS, true, "function",
		      "Applies FUNCTION to all existing threads.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            Function fun = checkFunction(arg);
            final LispThread thread = LispThread.currentThread();
            LispObject result = NIL;
            Iterator it = map.values().iterator();
            while (it.hasNext()) {
                LispObject[] args = new LispObject[1];
                args[0] = (LispThread) it.next();
                result = makeCons(funcall(fun, args, thread), result);
            }
            return result;
        }
    };

    // ### destroy-thread
    public static final Primitive DESTROY_THREAD =
        new Primitive("destroy-thread", PACKAGE_THREADS, true, "thread", 
		      "Mark THREAD as destroyed.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            final LispThread thread;
            if (arg instanceof LispThread) {
                thread = (LispThread) arg;
            }
            else {
                return type_error(arg, SymbolConstants.THREAD);
            }
            thread.setDestroyed(true);
            return T;
        }
    };

    // ### interrupt-thread thread function &rest args => T
    // Interrupts thread and forces it to apply function to args. When the
    // function returns, the thread's original computation continues. If
    // multiple interrupts are queued for a thread, they are all run, but the
    // order is not guaranteed.
    public static final Primitive INTERRUPT_THREAD =
        new Primitive("interrupt-thread", PACKAGE_THREADS, true,
		      "thread function &rest args",
		      "Interrupts THREAD and forces it to apply FUNCTION to ARGS.\nWhen the function returns, the thread's original computation continues. If  multiple interrupts are queued for a thread, they are all run, but the order is not guaranteed.")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 2)
                return error(new WrongNumberOfArgumentsException(this));
            final LispThread thread;
            if (args[0] instanceof LispThread) {
                thread = (LispThread) args[0];
            }
            else {
                return type_error(args[0], SymbolConstants.THREAD);
            }
            LispObject fun = args[1];
            LispObject funArgs = NIL;
            for (int i = args.length; i-- > 2;)
                funArgs = makeCons(args[i], funArgs);
            thread.interrupt(fun, funArgs);
            return T;
        }
    };

    // ### current-thread
    public static final Primitive CURRENT_THREAD =
        new Primitive("current-thread", PACKAGE_THREADS, true, "",
		      "Returns a reference to invoking thread.")
    {
        @Override
        public LispObject execute() throws ConditionThrowable
        {
            return currentThread();
        }
    };

    // ### backtrace
    public static final Primitive BACKTRACE =
        new Primitive("backtrace", PACKAGE_SYS, true, "",
		      "Returns a backtrace of the invoking thread.")
    {
        @Override
        public LispObject execute(LispObject[] args)
            throws ConditionThrowable
        {
            if (args.length > 1)
                return error(new WrongNumberOfArgumentsException(this));
            int limit = args.length > 0 ? args[0].intValue() : 0;
            return currentThread().backtrace(limit);
        }
    };
    // ### frame-to-string
    public static final Primitive FRAME_TO_STRING =
        new Primitive("frame-to-string", PACKAGE_SYS, true, "frame")
    {
        @Override
        public LispObject execute(LispObject[] args)
            throws ConditionThrowable
        {
            if (args.length != 1)
                return error(new WrongNumberOfArgumentsException(this));
            
            return checkStackFrame(args[0]).toLispString();
        }
    };

    // ### frame-to-list
    public static final Primitive FRAME_TO_LIST =
        new Primitive("frame-to-list", PACKAGE_SYS, true, "frame")
    {
        @Override
        public LispObject execute(LispObject[] args)
            throws ConditionThrowable
        {
            if (args.length != 1)
                return error(new WrongNumberOfArgumentsException(this));

            return checkStackFrame(args[0]).toLispList();
        }
    };


    static {
        //FIXME: this block has been added for pre-0.16 compatibility
        // and can be removed the latest at release 0.22
        try {
            PACKAGE_EXT.export(intern("MAKE-THREAD", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("THREADP", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("THREAD-ALIVE-P", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("THREAD-NAME", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("MAPCAR-THREADS", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("DESTROY-THREAD", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("INTERRUPT-THREAD", PACKAGE_THREADS));
            PACKAGE_EXT.export(intern("CURRENT-THREAD", PACKAGE_THREADS));
        }
        catch (ConditionThrowable ct) { }
    }

    // ### use-fast-calls
    public static final Primitive USE_FAST_CALLS =
        new Primitive("use-fast-calls", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            use_fast_calls = (arg != NIL);
            return use_fast_calls ? T : NIL;
        }
    };

    // ### synchronized-on
    public static final SpecialOperator SYNCHRONIZED_ON =
        new SpecialOperator("synchronized-on", PACKAGE_THREADS, true,
                            "form &body body")
    {
        @Override
        public LispObject execute(LispObject args, Environment env)
            throws ConditionThrowable
        {
          if (args == NIL)
            return error(new WrongNumberOfArgumentsException(this));

          LispThread thread = LispThread.currentThread();
          synchronized (Lisp.eval(args.CAR(), env, thread).lockableInstance()) {
              return progn(args.CDR(), env, thread);
          }
        }
    };

    // ### object-wait
    public static final Primitive OBJECT_WAIT =
        new Primitive("object-wait", PACKAGE_THREADS, true,
                      "object &optional timeout")
    {
        @Override
        public LispObject execute(LispObject object)
            throws ConditionThrowable
        {
            try {
                object.lockableInstance().wait();
            }
            catch (InterruptedException e) {
                currentThread().processThreadInterrupts();
            }
            catch (IllegalMonitorStateException e) {
                return error(new IllegalMonitorState());
            }
            return NIL;
        }

        @Override
        public LispObject execute(LispObject object, LispObject timeout)
            throws ConditionThrowable
        {
            try {
                object.lockableInstance().wait(javaSleepInterval(timeout));
            }
            catch (InterruptedException e) {
                currentThread().processThreadInterrupts();
            }
            catch (IllegalMonitorStateException e) {
                return error(new IllegalMonitorState());
            }
            return NIL;
        }
    };

    // ### object-notify
    public static final Primitive OBJECT_NOTIFY =
        new Primitive("object-notify", PACKAGE_THREADS, true,
                      "object")
    {
        @Override
        public LispObject execute(LispObject object)
            throws ConditionThrowable
        {
            try {
                object.lockableInstance().notify();
            }
            catch (IllegalMonitorStateException e) {
                return error(new IllegalMonitorState());
            }
            return NIL;
        }
    };

    // ### object-notify-all
    public static final Primitive OBJECT_NOTIFY_ALL =
        new Primitive("object-notify-all", PACKAGE_THREADS, true,
                      "object")
    {
        @Override
        public LispObject execute(LispObject object)
            throws ConditionThrowable
        {
            try {
                object.lockableInstance().notifyAll();
            }
            catch (IllegalMonitorStateException e) {
                return error(new IllegalMonitorState());
            }
            return NIL;
        }
    };
    
	public void uncaughtException(Thread arg0, Throwable arg1) {
		if (arg1 instanceof Go) {
			Go go = (Go)arg1;
        	Debug.trace("CREATOR: "+go.creatorFrame);
        	Debug.trace("THROWER: "+Debug.frameString(LispThread.currentThread().stack));
        	error(go.getCondition());
		}
	    try {	    	
			error(new LispError(getMessage(arg1)));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(arg1);
		}
		
	}
    public static final String getMessage(Throwable t)
    {
        String message = t.getMessage();
        if (message == null || message.length() == 0)
            message = t.getClass().getName();
        return message;
    }

}
