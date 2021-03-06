/*
 * BuiltInClass.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: BuiltInClass.java 12105 2009-08-19 14:51:56Z mevenson $
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
import static org.armedbear.lisp.Nil.NIL;
import static org.armedbear.lisp.Lisp.*;

public class BuiltInClass extends LispClass
{
  private BuiltInClass(Symbol symbol)
  {
    super(symbol);
  }

  @Override
  public LispObject typeOf()
  {
    return SymbolConstants.BUILT_IN_CLASS;
  }

  @Override
  public LispObject classOf()
  {
    return StandardClass.BUILT_IN_CLASS;
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.BUILT_IN_CLASS)
      return T;
    if (type == StandardClass.BUILT_IN_CLASS)
      return T;
    return super.typep(type);
  }

  @Override
  public LispObject getDescription() throws ConditionThrowable
  {
    return new SimpleString(writeToString());
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    FastStringBuffer sb = new FastStringBuffer("#<BUILT-IN-CLASS ");
    sb.append(symbol.writeToString());
    sb.append('>');
    return sb.toString();
  }

  private static BuiltInClass makeBuiltInClass(Symbol symbol)
  {
    BuiltInClass c = new BuiltInClass(symbol);
    Lisp.addLispClass(symbol, c);
    return c;
  }

  public static final BuiltInClass CLASS_T              = makeBuiltInClass(T);

  public static final BuiltInClass ARRAY                = makeBuiltInClass(SymbolConstants.ARRAY);
  public static final BuiltInClass BIGNUM               = makeBuiltInClass(SymbolConstants.BIGNUM);
  public static final BuiltInClass BASE_STRING          = makeBuiltInClass(SymbolConstants.BASE_STRING);
  public static final BuiltInClass BIT_VECTOR           = makeBuiltInClass(SymbolConstants.BIT_VECTOR);
  public static final BuiltInClass BROADCAST_STREAM     = makeBuiltInClass(SymbolConstants.BROADCAST_STREAM);
  public static final BuiltInClass CASE_FROB_STREAM     = makeBuiltInClass(SymbolConstants.CASE_FROB_STREAM);
  public static final BuiltInClass CHARACTER            = makeBuiltInClass(SymbolConstants.CHARACTER);
  public static final BuiltInClass COMPLEX              = makeBuiltInClass(SymbolConstants.COMPLEX);
  public static final BuiltInClass CONCATENATED_STREAM  = makeBuiltInClass(SymbolConstants.CONCATENATED_STREAM);
  public static final BuiltInClass CONS                 = makeBuiltInClass(SymbolConstants.CONS);
  public static final BuiltInClass DOUBLE_FLOAT         = makeBuiltInClass(SymbolConstants.DOUBLE_FLOAT);
  public static final BuiltInClass ECHO_STREAM          = makeBuiltInClass(SymbolConstants.ECHO_STREAM);
  public static final BuiltInClass ENVIRONMENT          = makeBuiltInClass(SymbolConstants.ENVIRONMENT);
  public static final BuiltInClass FILE_STREAM          = makeBuiltInClass(SymbolConstants.FILE_STREAM);
  public static final BuiltInClass FIXNUM               = makeBuiltInClass(SymbolConstants.FIXNUM);
  public static final BuiltInClass FLOAT                = makeBuiltInClass(SymbolConstants.FLOAT);
  public static final BuiltInClass FUNCTION             = makeBuiltInClass(SymbolConstants.FUNCTION);
  public static final BuiltInClass HASH_TABLE           = makeBuiltInClass(SymbolConstants.HASH_TABLE);
  public static final BuiltInClass INTEGER              = makeBuiltInClass(SymbolConstants.INTEGER);
  public static final BuiltInClass JAVA_OBJECT          = makeBuiltInClass(SymbolConstants.JAVA_OBJECT);
  public static final BuiltInClass LIST                 = makeBuiltInClass(SymbolConstants.LIST);
  public static final BuiltInClass LOGICAL_PATHNAME     = makeBuiltInClass(SymbolConstants.LOGICAL_PATHNAME);
  public static final BuiltInClass MAILBOX              = makeBuiltInClass(SymbolConstants.MAILBOX);
  public static final BuiltInClass METHOD_COMBINATION   = makeBuiltInClass(SymbolConstants.METHOD_COMBINATION);
  public static final BuiltInClass GATE                 = makeBuiltInClass(SymbolConstants.GATE);
  public static final BuiltInClass MUTEX                = makeBuiltInClass(SymbolConstants.MUTEX);
  public static final BuiltInClass NIL_VECTOR           = makeBuiltInClass(SymbolConstants.NIL_VECTOR);
  public static final BuiltInClass NULL                 = makeBuiltInClass(SymbolConstants.NULL);
  public static final BuiltInClass NUMBER               = makeBuiltInClass(SymbolConstants.NUMBER);
  public static final BuiltInClass PACKAGE              = makeBuiltInClass(SymbolConstants.PACKAGE);
  public static final BuiltInClass PATHNAME             = makeBuiltInClass(SymbolConstants.PATHNAME);
  public static final BuiltInClass RANDOM_STATE         = makeBuiltInClass(SymbolConstants.RANDOM_STATE);
  public static final BuiltInClass RATIO                = makeBuiltInClass(SymbolConstants.RATIO);
  public static final BuiltInClass RATIONAL             = makeBuiltInClass(SymbolConstants.RATIONAL);
  public static final BuiltInClass READTABLE            = makeBuiltInClass(SymbolConstants.READTABLE);
  public static final BuiltInClass REAL                 = makeBuiltInClass(SymbolConstants.REAL);
  public static final BuiltInClass RESTART              = makeBuiltInClass(SymbolConstants.RESTART);
  public static final BuiltInClass SEQUENCE             = makeBuiltInClass(SymbolConstants.SEQUENCE);
  public static final BuiltInClass SIMPLE_ARRAY         = makeBuiltInClass(SymbolConstants.SIMPLE_ARRAY);
  public static final BuiltInClass SIMPLE_BASE_STRING   = makeBuiltInClass(SymbolConstants.SIMPLE_BASE_STRING);
  public static final BuiltInClass SIMPLE_BIT_VECTOR    = makeBuiltInClass(SymbolConstants.SIMPLE_BIT_VECTOR);
  public static final BuiltInClass SIMPLE_STRING        = makeBuiltInClass(SymbolConstants.SIMPLE_STRING);
  public static final BuiltInClass SIMPLE_VECTOR        = makeBuiltInClass(SymbolConstants.SIMPLE_VECTOR);
  public static final BuiltInClass SINGLE_FLOAT         = makeBuiltInClass(SymbolConstants.SINGLE_FLOAT);
  public static final BuiltInClass SLIME_INPUT_STREAM   = makeBuiltInClass(SymbolConstants.SLIME_INPUT_STREAM);
  public static final BuiltInClass SLIME_OUTPUT_STREAM  = makeBuiltInClass(SymbolConstants.SLIME_OUTPUT_STREAM);
  public static final BuiltInClass SOCKET_STREAM        = makeBuiltInClass(SymbolConstants.SOCKET_STREAM);
  public static final BuiltInClass STREAM               = makeBuiltInClass(SymbolConstants.STREAM);
  public static final BuiltInClass STRING               = makeBuiltInClass(SymbolConstants.STRING);
  public static final BuiltInClass STRING_INPUT_STREAM  = makeBuiltInClass(SymbolConstants.STRING_INPUT_STREAM);
  public static final BuiltInClass STRING_OUTPUT_STREAM = makeBuiltInClass(SymbolConstants.STRING_OUTPUT_STREAM);
  public static final BuiltInClass STRING_STREAM        = makeBuiltInClass(SymbolConstants.STRING_STREAM);
  public static final BuiltInClass SYMBOL               = makeBuiltInClass(SymbolConstants.SYMBOL);
  public static final BuiltInClass SYNONYM_STREAM       = makeBuiltInClass(SymbolConstants.SYNONYM_STREAM);
  public static final BuiltInClass THREAD               = makeBuiltInClass(SymbolConstants.THREAD);
  public static final BuiltInClass TWO_WAY_STREAM       = makeBuiltInClass(SymbolConstants.TWO_WAY_STREAM);
  public static final BuiltInClass VECTOR               = makeBuiltInClass(SymbolConstants.VECTOR);
  public static final BuiltInClass STACK_FRAME          = makeBuiltInClass(SymbolConstants.STACK_FRAME);
  public static final BuiltInClass LISP_STACK_FRAME     = makeBuiltInClass(SymbolConstants.LISP_STACK_FRAME);
  public static final BuiltInClass JAVA_STACK_FRAME     = makeBuiltInClass(SymbolConstants.JAVA_STACK_FRAME);


  public static final StructureClass STRUCTURE_OBJECT =
    new StructureClass(SymbolConstants.STRUCTURE_OBJECT, list(CLASS_T));
  static
  {
	  Lisp.addLispClass(SymbolConstants.STRUCTURE_OBJECT, STRUCTURE_OBJECT);
  }

  static
  {
    ARRAY.setDirectSuperclass(CLASS_T);
    ARRAY.setCPL(ARRAY, CLASS_T);
    BASE_STRING.setDirectSuperclass(STRING);
    BASE_STRING.setCPL(BASE_STRING, STRING, VECTOR, ARRAY, SEQUENCE, CLASS_T);
    BIGNUM.setDirectSuperclass(INTEGER);
    BIGNUM.setCPL(BIGNUM, INTEGER, RATIONAL, REAL, NUMBER, CLASS_T);
    BIT_VECTOR.setDirectSuperclass(VECTOR);
    BIT_VECTOR.setCPL(BIT_VECTOR, VECTOR, ARRAY, SEQUENCE, CLASS_T);
    BROADCAST_STREAM.setDirectSuperclass(STREAM);
    BROADCAST_STREAM.setCPL(BROADCAST_STREAM, STREAM, CLASS_T);
    CASE_FROB_STREAM.setDirectSuperclass(STREAM);
    CASE_FROB_STREAM.setCPL(CASE_FROB_STREAM, STREAM, CLASS_T);
    CHARACTER.setDirectSuperclass(CLASS_T);
    CHARACTER.setCPL(CHARACTER, CLASS_T);
    CLASS_T.setCPL(CLASS_T);
    COMPLEX.setDirectSuperclass(NUMBER);
    COMPLEX.setCPL(COMPLEX, NUMBER, CLASS_T);
    CONCATENATED_STREAM.setDirectSuperclass(STREAM);
    CONCATENATED_STREAM.setCPL(CONCATENATED_STREAM, STREAM, CLASS_T);
    CONS.setDirectSuperclass(LIST);
    CONS.setCPL(CONS, LIST, SEQUENCE, CLASS_T);
    DOUBLE_FLOAT.setDirectSuperclass(FLOAT);
    DOUBLE_FLOAT.setCPL(DOUBLE_FLOAT, FLOAT, REAL, NUMBER, CLASS_T);
    ECHO_STREAM.setDirectSuperclass(STREAM);
    ECHO_STREAM.setCPL(ECHO_STREAM, STREAM, CLASS_T);
    ENVIRONMENT.setDirectSuperclass(CLASS_T);
    ENVIRONMENT.setCPL(ENVIRONMENT, CLASS_T);
    FIXNUM.setDirectSuperclass(INTEGER);
    FIXNUM.setCPL(FIXNUM, INTEGER, RATIONAL, REAL, NUMBER, CLASS_T);
    FILE_STREAM.setDirectSuperclass(STREAM);
    FILE_STREAM.setCPL(FILE_STREAM, STREAM, CLASS_T);
    FLOAT.setDirectSuperclass(REAL);
    FLOAT.setCPL(FLOAT, REAL, NUMBER, CLASS_T);
    FUNCTION.setDirectSuperclass(CLASS_T);
    FUNCTION.setCPL(FUNCTION, CLASS_T);
    HASH_TABLE.setDirectSuperclass(CLASS_T);
    HASH_TABLE.setCPL(HASH_TABLE, CLASS_T);
    INTEGER.setDirectSuperclass(RATIONAL);
    INTEGER.setCPL(INTEGER, RATIONAL, REAL, NUMBER, CLASS_T);
    JAVA_OBJECT.setDirectSuperclass(CLASS_T);
    JAVA_OBJECT.setCPL(JAVA_OBJECT, CLASS_T);
    LIST.setDirectSuperclass(SEQUENCE);
    LIST.setCPL(LIST, SEQUENCE, CLASS_T);
    LOGICAL_PATHNAME.setDirectSuperclass(PATHNAME);
    LOGICAL_PATHNAME.setCPL(LOGICAL_PATHNAME, PATHNAME, CLASS_T);
    MAILBOX.setDirectSuperclass(CLASS_T);
    MAILBOX.setCPL(MAILBOX, CLASS_T);
    METHOD_COMBINATION.setDirectSuperclass(CLASS_T);
    METHOD_COMBINATION.setCPL(METHOD_COMBINATION, CLASS_T);
    MUTEX.setDirectSuperclass(CLASS_T);
    MUTEX.setCPL(MUTEX, CLASS_T);
    NIL_VECTOR.setDirectSuperclass(STRING);
    NIL_VECTOR.setCPL(NIL_VECTOR, STRING, VECTOR, ARRAY, SEQUENCE, CLASS_T);
    NULL.setDirectSuperclass(LIST);
    NULL.setCPL(NULL, SYMBOL, LIST, SEQUENCE, CLASS_T);
    NUMBER.setDirectSuperclass(CLASS_T);
    NUMBER.setCPL(NUMBER, CLASS_T);
    PACKAGE.setDirectSuperclass(CLASS_T);
    PACKAGE.setCPL(PACKAGE, CLASS_T);
    PATHNAME.setDirectSuperclass(CLASS_T);
    PATHNAME.setCPL(PATHNAME, CLASS_T);
    RANDOM_STATE.setDirectSuperclass(CLASS_T);
    RANDOM_STATE.setCPL(RANDOM_STATE, CLASS_T);
    RATIO.setDirectSuperclass(RATIONAL);
    RATIO.setCPL(RATIO, RATIONAL, REAL, NUMBER, CLASS_T);
    RATIONAL.setDirectSuperclass(REAL);
    RATIONAL.setCPL(RATIONAL, REAL, NUMBER, CLASS_T);
    READTABLE.setDirectSuperclass(CLASS_T);
    READTABLE.setCPL(READTABLE, CLASS_T);
    REAL.setDirectSuperclass(NUMBER);
    REAL.setCPL(REAL, NUMBER, CLASS_T);
    RESTART.setDirectSuperclass(CLASS_T);
    RESTART.setCPL(RESTART, CLASS_T);
    SEQUENCE.setDirectSuperclass(CLASS_T);
    SEQUENCE.setCPL(SEQUENCE, CLASS_T);
    SIMPLE_ARRAY.setDirectSuperclass(ARRAY);
    SIMPLE_ARRAY.setCPL(SIMPLE_ARRAY, ARRAY, CLASS_T);
    SIMPLE_BASE_STRING.setDirectSuperclasses(list(BASE_STRING, SIMPLE_STRING));
    SIMPLE_BASE_STRING.setCPL(SIMPLE_BASE_STRING, BASE_STRING, SIMPLE_STRING,
                              STRING, VECTOR, SIMPLE_ARRAY, ARRAY, SEQUENCE,
                              CLASS_T);
    SIMPLE_BIT_VECTOR.setDirectSuperclasses(list(BIT_VECTOR, SIMPLE_ARRAY));
    SIMPLE_BIT_VECTOR.setCPL(SIMPLE_BIT_VECTOR, BIT_VECTOR, VECTOR,
                             SIMPLE_ARRAY, ARRAY, SEQUENCE, CLASS_T);
    SIMPLE_STRING.setDirectSuperclasses(list(BASE_STRING, STRING, SIMPLE_ARRAY));
    SIMPLE_STRING.setCPL(SIMPLE_STRING, BASE_STRING, STRING, VECTOR,
                         SIMPLE_ARRAY, ARRAY, SEQUENCE, CLASS_T);
    SIMPLE_VECTOR.setDirectSuperclasses(list(VECTOR, SIMPLE_ARRAY));
    SIMPLE_VECTOR.setCPL(SIMPLE_VECTOR, VECTOR, SIMPLE_ARRAY, ARRAY, SEQUENCE,
                         CLASS_T);
    SINGLE_FLOAT.setDirectSuperclass(FLOAT);
    SINGLE_FLOAT.setCPL(SINGLE_FLOAT, FLOAT, REAL, NUMBER, CLASS_T);
    SLIME_INPUT_STREAM.setDirectSuperclass(STRING_STREAM);
    SLIME_INPUT_STREAM.setCPL(SLIME_INPUT_STREAM, STRING_STREAM, STREAM,
                              CLASS_T);
    SLIME_OUTPUT_STREAM.setDirectSuperclass(STRING_STREAM);
    SLIME_OUTPUT_STREAM.setCPL(SLIME_OUTPUT_STREAM, STRING_STREAM, STREAM,
                               CLASS_T);
    SOCKET_STREAM.setDirectSuperclass(TWO_WAY_STREAM);
    SOCKET_STREAM.setCPL(SOCKET_STREAM, TWO_WAY_STREAM, STREAM, CLASS_T);
    STREAM.setDirectSuperclass(CLASS_T);
    STREAM.setCPL(STREAM, CLASS_T);
    STRING.setDirectSuperclass(VECTOR);
    STRING.setCPL(STRING, VECTOR, ARRAY, SEQUENCE, CLASS_T);
    STRING_INPUT_STREAM.setDirectSuperclass(STRING_STREAM);
    STRING_INPUT_STREAM.setCPL(STRING_INPUT_STREAM, STRING_STREAM, STREAM,
                               CLASS_T);
    STRING_OUTPUT_STREAM.setDirectSuperclass(STRING_STREAM);
    STRING_OUTPUT_STREAM.setCPL(STRING_OUTPUT_STREAM, STRING_STREAM, STREAM,
                                CLASS_T);
    STRING_STREAM.setDirectSuperclass(STREAM);
    STRING_STREAM.setCPL(STRING_STREAM, STREAM, CLASS_T);
    STRUCTURE_OBJECT.setCPL(STRUCTURE_OBJECT, CLASS_T);
    SYMBOL.setDirectSuperclass(CLASS_T);
    SYMBOL.setCPL(SYMBOL, CLASS_T);
    SYNONYM_STREAM.setDirectSuperclass(STREAM);
    SYNONYM_STREAM.setCPL(SYNONYM_STREAM, STREAM, CLASS_T);
    THREAD.setDirectSuperclass(CLASS_T);
    THREAD.setCPL(THREAD, CLASS_T);
    TWO_WAY_STREAM.setDirectSuperclass(STREAM);
    TWO_WAY_STREAM.setCPL(TWO_WAY_STREAM, STREAM, CLASS_T);
    VECTOR.setDirectSuperclasses(list(ARRAY, SEQUENCE));
    VECTOR.setCPL(VECTOR, ARRAY, SEQUENCE, CLASS_T);
    STACK_FRAME.setDirectSuperclasses(CLASS_T);
    STACK_FRAME.setCPL(STACK_FRAME, CLASS_T);
    LISP_STACK_FRAME.setDirectSuperclasses(STACK_FRAME);
    LISP_STACK_FRAME.setCPL(LISP_STACK_FRAME, STACK_FRAME, CLASS_T);
    JAVA_STACK_FRAME.setDirectSuperclasses(STACK_FRAME);
    JAVA_STACK_FRAME.setCPL(JAVA_STACK_FRAME, STACK_FRAME, CLASS_T);
  }

  static
  {
    try
      {
        StandardClass.initializeStandardClasses();
      }
    catch (Throwable t)
      {
        Debug.trace(t);
      }
  }
}
