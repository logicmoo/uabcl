/*
 * Java.java
 *
 * Copyright (C) 2002-2006 Peter Graves, Andras Simon
 * $Id: Java.java 12111 2009-08-23 09:26:13Z ehuelsmann $
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.armedbear.lisp.Nil.NIL;
import static org.armedbear.lisp.Lisp.*;
public final class Java extends LispFile
{
    public static final Map<Class,Symbol> registeredExceptions =
       new HashMap<Class,Symbol>();

    public static final LispClass java_exception = findLispClass(SymbolConstants.JAVA_EXCEPTION);

    public static boolean isJavaException(LispClass lc) throws ConditionThrowable
    {
        return lc.subclassp(java_exception);
    }

    // ### register-java-exception exception-name condition-symbol => T
    public static final Primitive REGISTER_JAVA_EXCEPTION =
        new Primitive("register-java-exception", PACKAGE_JAVA, true,
                      "exception-name condition-symbol")
    {
        @Override
        public LispObject execute(LispObject className, LispObject symbol)
            throws ConditionThrowable
        {
            // FIXME Verify that CONDITION-SYMBOL is a symbol that names a condition.
            // FIXME Signal a continuable error if the exception is already registered.
            if ((symbol instanceof Symbol) && isJavaException(findLispClass((Symbol) symbol))) {
                registeredExceptions.put(classForName(className.getStringValue()),
                                         (Symbol)symbol);
                return T;
            }
            return NIL;
        }
    };

    // ### unregister-java-exception exception-name => T or NIL
    public static final Primitive UNREGISTER_JAVA_EXCEPTION =
        new Primitive("unregister-java-exception", PACKAGE_JAVA, true,
                      "exception-name")
    {
        @Override
        public LispObject execute(LispObject className)
            throws ConditionThrowable
        {
            // FIXME Verify that EXCEPTION-NAME designates a subclass of Throwable.
            return registeredExceptions.remove(classForName(className.getStringValue())) == null ? NIL : T;
        }
    };

    public static Symbol getCondition(Class cl) throws ConditionThrowable
    {
	Class o = classForName("java.lang.Object");
     	for (Class c = cl ; c != o ; c = c.getSuperclass()) {
            Object object = registeredExceptions.get(c);
            if (object != null && isJavaException(findLispClass((Symbol) object))) {
                return (Symbol) object;
            }
        }
        return null;
    }

    // ### jclass name-or-class-ref => class-ref
    public static final Primitive JCLASS =
        new Primitive(SymbolConstants.JCLASS, "name-or-class-ref",
"Returns a reference to the Java class designated by NAME-OR-CLASS-REF.")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return JavaObject.getInstance(javaClass(arg));
        }
    };

    // ### jfield - retrieve or modify a field in a Java class or instance.
    //
    // Supported argument patterns:
    //
    //   Case 1: class-ref  field-name:
    //               to retrieve the value of a static field.
    //
    //   Case 2: class-ref  field-name  instance-ref:
    //               to retrieve the value of a class field of the instance.
    //
    //   Case 3: class-ref  field-name  primitive-value:
    //               to store primitive-value in a static field.
    //
    //   Case 4: class-ref  field-name  instance-ref  value:
    //               to store value in a class field of the instance.
    //
    //   Case 5: class-ref  field-name  nil  value:
    //               to store value in a static field (when value may be
    //               confused with an instance-ref).
    //
    //   Case 6: field-name  instance:
    //               to retrieve the value of a field of the instance. The
    //               class is derived from the instance.
    //
    //   Case 7: field-name  instance  value:
    //               to store value in a field of the instance. The class is
    //               derived from the instance.
    //

    public static final LispObject jfield(Primitive fun, LispObject[] args, boolean translate)
            throws ConditionThrowable
    {
        if (args.length < 2 || args.length > 4)
            error(new WrongNumberOfArgumentsException(fun));
        String fieldName = null;
        Class c;
        Field f;
        Class fieldType;
        Object instance = null;
        try {
            if (args[1] instanceof AbstractString) {
                // Cases 1-5.
                fieldName = args[1].getStringValue();
                c = javaClass(args[0]);
            } else {
                // Cases 6 and 7.
                fieldName = args[0].getStringValue();
                instance = JavaObject.getObject(args[1]);
                c = instance.getClass();
            }
            f = c.getField(fieldName);
            fieldType = f.getType();
            switch (args.length) {
                case 2:
                    // Cases 1 and 6.
                    break;
                case 3:
                    // Cases 2,3, and 7.
                    if (instance == null) {
                        // Cases 2 and 3.
                        if (args[2] instanceof JavaObject) {
                            // Case 2.
                            instance = JavaObject.getObject(args[2]);
                            break;
                        } else {
                            // Case 3.
                            f.set(null,args[2].javaInstance(fieldType));
                            return args[2];
                        }
                    } else {
                        // Case 7.
                        f.set(instance,args[2].javaInstance(fieldType));
                        return args[2];
                    }
                case 4:
                    // Cases 4 and 5.
                    if (args[2] != NIL) {
                        // Case 4.
                        instance = JavaObject.getObject(args[2]);
                    }
                    f.set(instance,args[3].javaInstance(fieldType));
                    return args[3];
            }
            return JavaObject.getInstance(f.get(instance), translate);
        }
        catch (NoSuchFieldException e) {
            error(new LispError("no such field"));
        }
        catch (SecurityException e) {
            error(new LispError("inaccessible field"));
        }
        catch (IllegalAccessException e) {
            error(new LispError("illegal access"));
        }
        catch (IllegalArgumentException e) {
            error(new LispError("illegal argument"));
        }
        catch (Throwable t) {
            error(new LispError(getMessage(t)));
        }
        // Not reached.
        return NIL;
    }

    public static final Primitive JFIELD =
        new Primitive("jfield", PACKAGE_JAVA, true,
                      "class-ref-or-field field-or-instance &optional instance value")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jfield(this, args, true);
        }
    };

    // ### jfield-raw - retrieve or modify a field in a Java class or instance.
    public static final Primitive JFIELD_RAW =
        new Primitive("jfield-raw", PACKAGE_JAVA, true,
                      "class-ref-or-field field-or-instance &optional instance value")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jfield(this, args, false);
        }
    };

    // ### jconstructor class-ref &rest parameter-class-refs
    public static final Primitive JCONSTRUCTOR =
        new Primitive("jconstructor", PACKAGE_JAVA, true,
                      "class-ref &rest parameter-class-refs")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 1)
                error(new WrongNumberOfArgumentsException(this));
            try {
                final Class<?> c = javaClass(args[0]);
                int argCount = 0;
                if (args.length == 2 && args[1]  instanceof Fixnum) {
                    argCount = args[1].intValue();
                } else {
                    Class<?>[] parameterTypes = new Class[args.length-1];
                    for (int i = 1; i < args.length; i++) {
                        parameterTypes[i-1] = javaClass(args[i]);
                    }
                    return JavaObject.getInstance(c.getConstructor(parameterTypes));
                }
                // Parameter types not explicitly specified.
                Constructor[] constructors = c.getConstructors();
                for (int i = 0; i < constructors.length; i++) {
                    Constructor constructor = constructors[i];
                    if (constructor.getParameterTypes().length == argCount)
                        return JavaObject.getInstance(constructor);
                }
                throw new NoSuchMethodException();
            }
            catch (NoSuchMethodException e) {
                error(new LispError("no such constructor"));
            }
            catch (ConditionThrowable e) {
                throw e;
            }
            catch (Throwable t) {
                error(new LispError(getMessage(t)));
            }
            // Not reached.
            return NIL;
        }
    };

    // ### jmethod class-ref name &rest parameter-class-refs
    public static final Primitive JMETHOD =
        new Primitive("jmethod", PACKAGE_JAVA, true,
                      "class-ref name &rest parameter-class-refs")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 2)
                error(new WrongNumberOfArgumentsException(this));
            final Class<?> c = javaClass(args[0]);
            String methodName = args[1].getStringValue();
            try {
                int argCount = 0;
                if (args.length == 3 && args[2]  instanceof Fixnum) {
                    argCount = ((Fixnum)args[2]).intValue();
                } else {
                    Class<?>[] parameterTypes = new Class[args.length-2];
                    for (int i = 2; i < args.length; i++)
                        parameterTypes[i-2] = javaClass(args[i]);
                    return JavaObject.getInstance(c.getMethod(methodName,
                                                              parameterTypes));
                }
                // Parameter types were not explicitly specified.
                Method[] methods = c.getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.getName().equals(methodName) &&
                        method.getParameterTypes().length == argCount)
                        return JavaObject.getInstance(method);
                }
                throw new NoSuchMethodException();
            }
            catch (NoSuchMethodException e) {
                FastStringBuffer sb = new FastStringBuffer("No such method: ");
                sb.append(c.getName());
                sb.append('.');
                sb.append(methodName);
                sb.append('(');
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i].writeToString());
                    if (i < args.length - 1)
                        sb.append(',');
                }
                sb.append(')');
                error(new LispError(sb.toString()));
            }
            catch (ConditionThrowable e) {
                throw e;
            }
            catch (Throwable t) {
                error(new LispError(getMessage(t)));
            }
            // Not reached.
            return NIL;
        }
    };

    public static final LispObject jstatic(Primitive fun, LispObject[] args, boolean translate)
            throws ConditionThrowable
    {
        if (args.length < 2)
            error(new WrongNumberOfArgumentsException(fun));
        try {
            Method m = null;
            LispObject methodRef = args[0];
            if (methodRef instanceof JavaObject) {
                Object obj = ((JavaObject)methodRef).getObject();
                if (obj instanceof Method)
                    m = (Method) obj;
            } else if (methodRef instanceof AbstractString) {
                Class c = javaClass(args[1]);
                if (c != null) {
                    String methodName = methodRef.getStringValue();
                    Method[] methods = c.getMethods();
                    int argCount = args.length - 2;
                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        if (!Modifier.isStatic(method.getModifiers())
                            || method.getParameterTypes().length != argCount)
                            continue;
                        if (method.getName().equals(methodName)) {
                            m = method;
                            break;
                        }
                    }
                    if (m == null)
                        error(new LispError("no such method"));
                }
            } else
                error(new TypeError("wrong type: " + methodRef));
            Object[] methodArgs = new Object[args.length-2];
            Class[] argTypes = m.getParameterTypes();
            for (int i = 2; i < args.length; i++) {
                LispObject arg = args[i];
                if (arg == NIL)
                    methodArgs[i-2] = null;
                else
                    methodArgs[i-2] = arg.javaInstance(argTypes[i-2]);
            }
            Object result = m.invoke(null, methodArgs);
            return JavaObject.getInstance(result, translate);
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException)
                t = t.getCause();
            Symbol condition = getCondition(t.getClass());
            if (condition == null)
                error(new JavaException(t));
            else
                SymbolConstants.SIGNAL.execute(
                    condition,
                    Keyword.CAUSE,
                    JavaObject.getInstance(t),
                    Keyword.FORMAT_CONTROL,
                    new SimpleString(getMessage(t)));
        }
        // Not reached.
        return NIL;
    }

    // ### jstatic method class &rest args
    public static final Primitive JSTATIC =
        new Primitive("jstatic", PACKAGE_JAVA, true, "method class &rest args")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jstatic(this, args, true);
        }
    };

    // ### jstatic-raw method class &rest args
    public static final Primitive JSTATIC_RAW =
        new Primitive("jstatic-raw", PACKAGE_JAVA, true,
                      "method class &rest args")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jstatic(this, args, false);
        }
    };

    // ### jnew constructor &rest args
    public static final Primitive JNEW =
        new Primitive("jnew", PACKAGE_JAVA, true, "constructor &rest args")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 1)
                error(new WrongNumberOfArgumentsException(this));
            LispObject classRef = args[0];
            try {
                Constructor constructor = (Constructor) JavaObject.getObject(classRef);
                Class[] argTypes = constructor.getParameterTypes();
                Object[] initargs = new Object[args.length-1];
                for (int i = 1; i < args.length; i++) {
                    LispObject arg = args[i];
                    if (arg == NIL)
                        initargs[i-1] = null;
                    else {
                        initargs[i-1] = arg.javaInstance(argTypes[i-1]);
                    }
                }
                return JavaObject.getInstance(constructor.newInstance(initargs));
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException)
                    t = t.getCause();
                Symbol condition = getCondition(t.getClass());
                if (condition == null)
                    error(new JavaException(t));
                else
                    SymbolConstants.SIGNAL.execute(
                        condition,
                        Keyword.CAUSE,
                        JavaObject.getInstance(t),
                        Keyword.FORMAT_CONTROL,
                        new SimpleString(getMessage(t)));
            }
            // Not reached.
            return NIL;
        }
    };

    // ### jnew-array element-type &rest dimensions
    public static final Primitive JNEW_ARRAY =
        new Primitive("jnew-array", PACKAGE_JAVA, true,
                      "element-type &rest dimensions")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 2)
                error(new WrongNumberOfArgumentsException(this));
            try {
                Class c = javaClass(args[0]);
                int[] dimensions = new int[args.length - 1];
                for (int i = 1; i < args.length; i++)
                    dimensions[i-1] = ((Integer)args[i].javaInstance()).intValue();
                return JavaObject.getInstance(Array.newInstance(c, dimensions));
            }
            catch (Throwable t) {
                error(new JavaException(t));
            }
            // Not reached.
            return NIL;
        }
    };

    public static final LispObject jarray_ref(Primitive fun, LispObject[] args, boolean translate)
            throws ConditionThrowable
    {
        if (args.length < 2)
            error(new WrongNumberOfArgumentsException(fun));
        try {
            Object a = args[0].javaInstance();
            for (int i = 1; i<args.length - 1; i++)
                a = Array.get(a, ((Integer)args[i].javaInstance()).intValue());
            return JavaObject.getInstance(Array.get(a,
                    ((Integer)args[args.length - 1].javaInstance()).intValue()), translate);
        }
        catch (Throwable t) {
            Symbol condition = getCondition(t.getClass());
            if (condition == null)
                error(new JavaException(t));
            else
                SymbolConstants.SIGNAL.execute(
                    condition,
                    Keyword.CAUSE,
                    JavaObject.getInstance(t),
                    Keyword.FORMAT_CONTROL,
                    new SimpleString(getMessage(t)));
        }
        // Not reached.
        return NIL;
    }

    // ### jarray-ref java-array &rest indices
    public static final Primitive JARRAY_REF =
        new Primitive("jarray-ref", PACKAGE_JAVA, true,
                      "java-array &rest indices")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jarray_ref(this, args, true);
        }
    };

    // ### jarray-ref-raw java-array &rest indices
    public static final Primitive JARRAY_REF_RAW =
        new Primitive("jarray-ref-raw", PACKAGE_JAVA, true,
                      "java-array &rest indices")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jarray_ref(this, args, false);
        }
    };

    // ### jarray-set java-array new-value &rest indices
    public static final Primitive JARRAY_SET =
        new Primitive("jarray-set", PACKAGE_JAVA, true,
                      "java-array new-value &rest indices")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 3)
                error(new WrongNumberOfArgumentsException(this));
            try {
                Object a = args[0].javaInstance();
                LispObject v = args[1];
                for (int i = 2; i<args.length - 1; i++)
                    a = Array.get(a, ((Integer)args[i].javaInstance()).intValue());
                Array.set(a, ((Integer)args[args.length - 1].javaInstance()).intValue(), v.javaInstance());
                return v;
            }
            catch (Throwable t) {
                Symbol condition = getCondition(t.getClass());
                if (condition == null)
                    error(new JavaException(t));
                else
                    SymbolConstants.SIGNAL.execute(
                        condition,
                        Keyword.CAUSE,
                        JavaObject.getInstance(t),
                        Keyword.FORMAT_CONTROL,
                        new SimpleString(getMessage(t)));
            }
            // Not reached.
            return NIL;
        }
    };

    // ### jcall method instance &rest args
    // Calls makeLispObject() to convert the result to an appropriate Lisp type.
    public static final Primitive JCALL =
        new Primitive(SymbolConstants.JCALL, "method-ref instance &rest args")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jcall(this, args, true);
        }
    };

    // ### jcall-raw method instance &rest args
    // Does no type conversion. The result of the call is simply wrapped in a
    // JavaObject.
    public static final Primitive JCALL_RAW =
        new Primitive(SymbolConstants.JCALL_RAW, "method-ref instance &rest args")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            return jcall(this, args, false);
        }
    };

    public static LispObject jcall(Primitive fun, LispObject[] args, boolean translate)
            throws ConditionThrowable
    {
        if (args.length < 2)
            error(new WrongNumberOfArgumentsException(fun));
        final LispObject methodArg = args[0];
        final LispObject instanceArg = args[1];
        final Object instance;
        if (instanceArg instanceof AbstractString)
            instance = instanceArg.getStringValue();
        else if (instanceArg instanceof JavaObject)
            instance = ((JavaObject)instanceArg).getObject();
        else {
	    instance = instanceArg.javaInstance();
        }
        try {
            final Method method;
            if (methodArg instanceof AbstractString) {
                String methodName = methodArg.getStringValue();
                Class c = instance.getClass();
                // FIXME Use the actual args, not just the count!
                method = findMethod(c, methodName, args.length - 2);
            } else
                method = (Method) JavaObject.getObject(methodArg);
            Class<?>[] argTypes = (Class<?>[])method.getParameterTypes();
            Object[] methodArgs = new Object[args.length - 2];
            for (int i = 2; i < args.length; i++) {
                LispObject arg = args[i];
                if (arg == NIL)
                    methodArgs[i-2] = null;
                else
                    methodArgs[i-2] = arg.javaInstance(argTypes[i-2]);
            }
            return JavaObject.getInstance(method.invoke(instance, methodArgs),
                                          translate);
        }
        catch (ConditionThrowable t) {
            throw t;
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException)
                t = t.getCause();
            Symbol condition = getCondition(t.getClass());
            if (condition == null)
                error(new JavaException(t));
            else
                SymbolConstants.SIGNAL.execute(
                    condition,
                    Keyword.CAUSE,
                    JavaObject.getInstance(t),
                    Keyword.FORMAT_CONTROL,
                    new SimpleString(getMessage(t)));
        }
        // Not reached.
        return null;
    }

    // FIXME This just returns the first matching method that it finds. Allegro
    // signals a continuable error if there are multiple matching methods.
    public static Method findMethod(Class c, String methodName, int argCount)
    {
        Method[] methods = c.getMethods();
        for (int i = methods.length; i-- > 0;) {
            Method method = methods[i];
            if (method.getName().equals(methodName))
                if (method.getParameterTypes().length == argCount)
                    return method;
        }
        return null;
    }

    // ### make-immediate-object object &optional type
    public static final Primitive MAKE_IMMEDIATE_OBJECT =
        new Primitive("make-immediate-object", PACKAGE_JAVA, true,
                      "object &optional type")
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length < 1)
                error(new WrongNumberOfArgumentsException(this));
            LispObject object = args[0];
            try {
                if (args.length > 1) {
                    LispObject type = args[1];
                    if (type == Keyword.BOOLEAN) {
                        if (object == NIL)
                            return JavaObject.getInstance(Boolean.FALSE);
                        else
                            return JavaObject.getInstance(Boolean.TRUE);
                    }
                    if (type == Keyword.REF) {
                        if (object == NIL)
                            return JavaObject.getInstance(null);
                        else
                            throw new Error();
                    }
                    // other special cases come here
                }
                return JavaObject.getInstance(object.javaInstance());
            }
            catch (Throwable t) {
                error(new LispError("MAKE-IMMEDIATE-OBJECT: not implemented"));
            }
            // Not reached.
            return NIL;
        }
    };

    // ### java-object-p
    public static final Primitive JAVA_OBJECT_P =
        new Primitive("java-object-p", PACKAGE_JAVA, true, "object")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return (arg instanceof JavaObject) ? T : NIL;
        }
    };

    // ### jobject-lisp-value java-object
    public static final Primitive JOBJECT_LISP_VALUE =
        new Primitive("jobject-lisp-value", PACKAGE_JAVA, true, "java-object")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return JavaObject.getInstance(arg.javaInstance(), true);
        }
    };
    
    public static final Primitive JGET_PROPERTY_VALUE =
	    new Primitive("%jget-property-value", PACKAGE_JAVA, true,
	                  "java-object property-name") {
    	
        @Override
        public LispObject execute(LispObject javaObject, LispObject propertyName) throws ConditionThrowable {
			try {
				Object obj = javaObject.javaInstance();
				PropertyDescriptor pd = getPropertyDescriptor(obj, propertyName);
				Object value = pd.getReadMethod().invoke(obj);
				if(value instanceof LispObject) {
				    return (LispObject) value;
				} else if(value != null) {
				    return JavaObject.getInstance(value, true);
				} else {
				    return NIL;
				}
			} catch (Exception e) {
				ConditionThrowable t = new ConditionThrowable("Exception reading property");
				t.initCause(e);
				throw t;
			}
        }
    };
    
    public static final Primitive JSET_PROPERTY_VALUE =
	    new Primitive("%jset-property-value", PACKAGE_JAVA, true,
	                  "java-object property-name value") {
    	
        @Override
        public LispObject execute(LispObject javaObject, LispObject propertyName, LispObject value) throws ConditionThrowable {
	    Object obj = null;
	    try {
		obj = javaObject.javaInstance();
		PropertyDescriptor pd = getPropertyDescriptor(obj, propertyName);
		Object jValue;
		//TODO maybe we should do this in javaInstance(Class)
		if(value instanceof JavaObject) {
		    jValue = value.javaInstance();
		} else {
		    if(Boolean.TYPE.equals(pd.getPropertyType()) ||
		       Boolean.class.equals(pd.getPropertyType())) {
			jValue = Boolean.valueOf(value != NIL);
		    } else {
			jValue = value != NIL ? value.javaInstance() : null;
		    }
		}
		pd.getWriteMethod().invoke(obj, jValue);
		return value;
	    } catch (Exception e) {
		ConditionThrowable t = new ConditionThrowable("Exception writing property " + propertyName.writeToString() + " in object " + obj + " to " + value.writeToString());
		t.initCause(e);
		throw t;
	    }
        }
    };
    
    public static PropertyDescriptor getPropertyDescriptor(Object obj, LispObject propertyName) throws ConditionThrowable, IntrospectionException {
        String prop = ((AbstractString) propertyName).getStringValue();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        for(PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
        	if(pd.getName().equals(prop)) {
        		return pd;
        	}
        }
		throw new ConditionThrowable("Property " + prop + " not found in " + obj);
    }
    
    public static Class classForName(String className) throws ConditionThrowable
    {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(className, true, JavaClassLoader.getPersistentInstance());
            }
            catch (ClassNotFoundException ex) {
                error(new LispError("Class not found: " + className));
                // Not reached.
                return null;
            }
        }
    }

    // Supports Java primitive types too.
    public static Class javaClass(LispObject obj) throws ConditionThrowable
    {
        if (obj instanceof AbstractString || obj instanceof Symbol) {
            String s = javaString(obj);
            if (s.equals("boolean"))
                return Boolean.TYPE;
            if (s.equals("byte"))
                return Byte.TYPE;
            if (s.equals("char"))
                return Character.TYPE;
            if (s.equals("short"))
                return Short.TYPE;
            if (s.equals("int"))
                return Integer.TYPE;
            if (s.equals("long"))
                return Long.TYPE;
            if (s.equals("float"))
                return Float.TYPE;
            if (s.equals("double"))
                return Double.TYPE;
            // Not a primitive Java type.
            Class c = classForName(s);
            if (c == null)
                error(new LispError(s + " does not designate a Java class."));

            return c;
        }
        // It's not a string, so it must be a JavaObject.
        final JavaObject javaObject;
        if (obj instanceof JavaObject) {
            javaObject = (JavaObject) obj;
        }
        else {
            type_error(obj, list(SymbolConstants.OR, SymbolConstants.STRING,
                                       SymbolConstants.JAVA_OBJECT));
            // Not reached.
            return null;
        }
        final Object javaObjectgetObject = javaObject.getObject();
        if (javaObjectgetObject instanceof Class) {
            return (Class) javaObjectgetObject;
        }
            error(new LispError(obj.writeToString() + " does not designate a Java class."));
            return null;
    }

    public static final String getMessage(Throwable t)
    {
        String message = t.getMessage();
        if (message == null || message.length() == 0)
            message = t.getClass().getName();
        return message;
    }
}
