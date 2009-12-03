/*
 * Primitives.java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id: Primitives.java 12290 2009-11-30 22:28:50Z vvoutilainen $
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

import static org.armedbear.lisp.Lisp.*;

import java.math.BigInteger;
import java.util.ArrayList;

import org.armedbear.lisp.InlinedPrimitiveRegistry.InlinableMethod;
import org.armedbear.lisp.InlinedPrimitiveRegistry.InlineThrows;
import org.armedbear.lisp.InlinedPrimitiveRegistry.NoInline;

/**
 * Description of the Class
 * 
 * @author Administrator
 */
public final class Primitives {

  @NoInline
  private static LispObject wrongNumberOfArgumetnts() throws InlineThrows {
    return error(new WrongNumberOfArgumentsException(InlinedPrimitiveRegistry.getCurrentOperator()));
  }

  // ### *
  public static final Primitive MULTIPLY = new Primitive(Symbol.STAR, "&rest numbers") {

    public LispObject execute() {
      return MULTIPLY_execute();
    }

    public LispObject execute(LispObject arg) {
      return MULTIPLY_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return MULTIPLY_execute(first, second);
    }

    public LispObject execute(LispObject[] args) {
      return MULTIPLY_execute(args);
    }
  };

  static final public LispObject MULTIPLY_execute() {
    return Fixnum.ONE;
  }

  static final public LispObject MULTIPLY_execute(LispObject arg) {
    if (arg.numberp()) {
      return arg;
    }
    return type_error(arg, Symbol.NUMBER);
  }

  static final public LispObject MULTIPLY_execute(LispObject first, LispObject second) {
    return first.multiplyBy(second);
  }

  static final public LispObject MULTIPLY_execute(LispObject[] args) {
    LispObject result = Fixnum.ONE;
    for (int i = 0; i < args.length; i++) {
      result = result.multiplyBy(args[i]);
    }
    return result;
  }

  // ### /
  public static final Primitive DIVIDE = new Primitive(Symbol.SLASH, "numerator &rest denominators") {

    public LispObject execute() {
      return DIVIDE_execute();
    }

    public LispObject execute(LispObject arg) {
      return DIVIDE_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return DIVIDE_execute(first, second);
    }

    public LispObject execute(LispObject[] args) {
      return DIVIDE_execute(args);
    }
  };

  static final public LispObject DIVIDE_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject DIVIDE_execute(LispObject arg) {
    return Fixnum.ONE.divideBy(arg);
  }

  static final public LispObject DIVIDE_execute(LispObject first, LispObject second) {
    return first.divideBy(second);
  }

  static final public LispObject DIVIDE_execute(LispObject[] args) {
    LispObject result = args[0];
    for (int i = 1; i < args.length; i++) {
      result = result.divideBy(args[i]);
    }
    return result;
  }

  // ### min
  public static final Primitive MIN = new Primitive(Symbol.MIN, "&rest reals") {

    public LispObject execute() {
      return MIN_execute();
    }

    public LispObject execute(LispObject arg) {
      return MIN_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return MIN_execute(first, second);
    }

    public LispObject execute(LispObject[] args) {
      return MIN_execute(args);
    }
  };

  static final public LispObject MIN_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject MIN_execute(LispObject arg) {
    if (arg.realp()) {
      return arg;
    }
    return type_error(arg, Symbol.REAL);
  }

  static final public LispObject MIN_execute(LispObject first, LispObject second) {
    return first.isLessThan(second) ? first : second;
  }

  static final public LispObject MIN_execute(LispObject[] args) {
    LispObject result = args[0];
    if (!result.realp()) {
      type_error(result, Symbol.REAL);
    }
    for (int i = 1; i < args.length; i++) {
      if (args[i].isLessThan(result)) {
        result = args[i];
      }
    }
    return result;
  }

  // ### max
  public static final Primitive MAX = new Primitive(Symbol.MAX, "&rest reals") {

    public LispObject execute() {
      return MAX_execute();
    }

    public LispObject execute(LispObject arg) {
      return MAX_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return MAX_execute(first, second);
    }

    public LispObject execute(LispObject[] args) {
      return MAX_execute(args);
    }
  };

  static final public LispObject MAX_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject MAX_execute(LispObject arg) {
    if (arg.realp()) {
      return arg;
    }
    return type_error(arg, Symbol.REAL);
  }

  static final public LispObject MAX_execute(LispObject first, LispObject second) {
    return first.isGreaterThan(second) ? first : second;
  }

  static final public LispObject MAX_execute(LispObject[] args) {
    LispObject result = args[0];
    if (!result.realp()) {
      type_error(result, Symbol.REAL);
    }
    for (int i = 1; i < args.length; i++) {
      if (args[i].isGreaterThan(result)) {
        result = args[i];
      }
    }
    return result;
  }

  // ### append
  public static final Primitive APPEND = new Primitive(Symbol.APPEND, "&rest lists") {

    public LispObject execute() {
      return APPEND_execute();
    }

    public LispObject execute(LispObject arg) {
      return APPEND_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return APPEND_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return APPEND_execute(first, second, third);
    }

    public LispObject execute(LispObject[] args) {
      return APPEND_execute(args);
    }
  };

  static final public LispObject APPEND_execute() {
    return NIL;
  }

  static final public LispObject APPEND_execute(LispObject arg) {
    return arg;
  }

  static final public LispObject APPEND_execute(LispObject first, LispObject second) {
    if (first == NIL) {
      return second;
    }
    // APPEND is required to copy its first argument.
    Cons result = new Cons(first.car());
    Cons splice = result;
    first = first.cdr();
    while (first != NIL) {
      Cons temp = new Cons(first.car());
      splice.cdr = temp;
      splice = temp;
      first = first.cdr();
    }
    splice.cdr = second;
    return result;
  }

  static final public LispObject APPEND_execute(LispObject first, LispObject second, LispObject third) {
    if (first == NIL) {
      return APPEND_execute(second, third);
    }
    Cons result = new Cons(first.car());
    Cons splice = result;
    first = first.cdr();
    while (first != NIL) {
      Cons temp = new Cons(first.car());
      splice.cdr = temp;
      splice = temp;
      first = first.cdr();
    }
    while (second != NIL) {
      Cons temp = new Cons(second.car());
      splice.cdr = temp;
      splice = temp;
      second = second.cdr();
    }
    splice.cdr = third;
    return result;
  }

  static final public LispObject APPEND_execute(LispObject[] args) {
    Cons result = null;
    Cons splice = null;
    final int limit = args.length - 1;
    int i;
    for (i = 0; i < limit; i++) {
      LispObject top = args[i];
      if (top == NIL) {
        continue;
      }
      result = new Cons(top.car());
      splice = result;
      top = top.cdr();
      while (top != NIL) {
        Cons temp = new Cons(top.car());
        splice.cdr = temp;
        splice = temp;
        top = top.cdr();
      }
      break;
    }
    if (result == null) {
      return args[i];
    }
    for (++i; i < limit; i++) {
      LispObject top = args[i];
      while (top != NIL) {
        Cons temp = new Cons(top.car());
        splice.cdr = temp;
        splice = temp;
        top = top.cdr();
      }
    }
    splice.cdr = args[i];
    return result;
  }

  // ### funcall
  // This needs to be public for LispAPI.java.
  public static final Primitive FUNCALL = new Primitive(Symbol.FUNCALL, "function &rest args") {

    public LispObject execute() {
      return FUNCALL_execute();
    }

    public LispObject execute(LispObject arg) {
      return FUNCALL_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return FUNCALL_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return FUNCALL_execute(first, second, third);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
      return FUNCALL_execute(first, second, third, fourth);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth, LispObject fifth) {
      return FUNCALL_execute(first, second, third, fourth, fifth);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth,
        LispObject fifth, LispObject sixth) {
      return FUNCALL_execute(first, second, third, fourth, fifth, sixth);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth,
        LispObject fifth, LispObject sixth, LispObject seventh) {
      return FUNCALL_execute(first, second, third, fourth, fifth, sixth, seventh);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth,
        LispObject fifth, LispObject sixth, LispObject seventh, LispObject eigth) {
      return FUNCALL_execute(first, second, third, fourth, fifth, sixth, seventh, eigth);
    }

    public LispObject execute(LispObject[] args) {
      return FUNCALL_execute(args);
    }
  };

  static final public LispObject FUNCALL_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject FUNCALL_execute(LispObject arg) {
    return LispThread.currentThread().execute(arg);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second) {
    return LispThread.currentThread().execute(first, second);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second, LispObject third) {
    return LispThread.currentThread().execute(first, second, third);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth) {
    return LispThread.currentThread().execute(first, second, third, fourth);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth, LispObject fifth) {
    return LispThread.currentThread().execute(first, second, third, fourth, fifth);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth, LispObject fifth, LispObject sixth) {
    return LispThread.currentThread().execute(first, second, third, fourth, fifth, sixth);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth, LispObject fifth, LispObject sixth, LispObject seventh) {
    return LispThread.currentThread().execute(first, second, third, fourth, fifth, sixth, seventh);
  }

  static final public LispObject FUNCALL_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth, LispObject fifth, LispObject sixth, LispObject seventh, LispObject eigth) {
    return LispThread.currentThread().execute(first, second, third, fourth, fifth, sixth, seventh, eigth);
  }

  static final public LispObject FUNCALL_execute(LispObject[] args) {
    final int length = args.length - 1;
    // Number of arguments.
    if (length == 8) {
      return LispThread.currentThread().execute(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7],
          args[8]);
    } else {
      LispObject[] newArgs = new LispObject[length];
      System.arraycopy(args, 1, newArgs, 0, length);
      return LispThread.currentThread().execute(args[0], newArgs);
    }
  }

  // ### apply

  public static final Primitive APPLY = new Primitive(Symbol.APPLY, "function &rest args") {

    public LispObject execute() {
      return APPLY_execute();
    }

    public LispObject execute(LispObject arg) {
      return APPLY_execute(arg);
    }

    public LispObject execute(LispObject fun, LispObject args) {
      return APPLY_execute(fun, args);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return APPLY_execute(first, second, third);
    }

    public LispObject execute(final LispObject[] args) {
      return APPLY_execute(args);
    }
  };

  static final public LispObject APPLY_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject APPLY_execute(LispObject arg) throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject APPLY_execute(LispObject fun, LispObject args) {
    final LispThread thread = LispThread.currentThread();
    final int length = args.length();
    switch (length) {
    case 0:
      return thread.execute(fun);
    case 1:
      return thread.execute(fun, ((Cons) args).car);
    case 2: {
      Cons cons = (Cons) args;
      return thread.execute(fun, cons.car, ((Cons) cons.cdr).car);
    }
    case 3:
      return thread.execute(fun, args.car(), args.cadr(), args.cdr().cdr().car());
    default: {
      final LispObject[] funArgs = new LispObject[length];
      int j = 0;
      while (args != NIL) {
        funArgs[j++] = args.car();
        args = args.cdr();
      }
      return funcall(fun, funArgs, thread);
    }
    }
  }

  static final public LispObject APPLY_execute(LispObject first, LispObject second, LispObject third) {
    if (third.listp()) {
      final int numFunArgs = 1 + third.length();
      final LispObject[] funArgs = new LispObject[numFunArgs];
      funArgs[0] = second;
      int j = 1;
      while (third != NIL) {
        funArgs[j++] = third.car();
        third = third.cdr();
      }
      return funcall(first, funArgs, LispThread.currentThread());
    }
    return type_error(third, Symbol.LIST);
  }

  static final public LispObject APPLY_execute(final LispObject[] args) {
    final int numArgs = args.length;
    LispObject spread = args[numArgs - 1];
    if (spread.listp()) {
      final int numFunArgs = numArgs - 2 + spread.length();
      final LispObject[] funArgs = new LispObject[numFunArgs];
      int j = 0;
      for (int i = 1; i < numArgs - 1; i++) {
        funArgs[j++] = args[i];
      }
      while (spread != NIL) {
        funArgs[j++] = spread.car();
        spread = spread.cdr();
      }
      return funcall(args[0], funArgs, LispThread.currentThread());
    }
    return type_error(spread, Symbol.LIST);
  }

  // ### nreverse
  public static final Primitive NREVERSE = new Primitive(Symbol.NREVERSE, "sequence") {

    public LispObject execute(LispObject arg) {
      return NREVERSE_execute(arg);
    }
  };

  static final public LispObject NREVERSE_execute(LispObject arg) {
    return arg.nreverse();
  }

  // ### symbol-name
  public static final Primitive SYMBOL_NAME = new Primitive(Symbol.SYMBOL_NAME, "symbol") {

    public LispObject execute(LispObject arg) {
      return SYMBOL_NAME_execute(arg);
    }
  };

  static final public LispObject SYMBOL_NAME_execute(LispObject arg) {
    return checkSymbol(arg).name;
  }

  // ### symbol-package
  public static final Primitive SYMBOL_PACKAGE = new Primitive(Symbol.SYMBOL_PACKAGE, "symbol") {

    public LispObject execute(LispObject arg) {
      return SYMBOL_PACKAGE_execute(arg);
    }
  };

  static final public LispObject SYMBOL_PACKAGE_execute(LispObject arg) {
    return checkSymbol(arg).getPackage();
  }

  // ### symbol-function
  public static final Primitive SYMBOL_FUNCTION = new Primitive(Symbol.SYMBOL_FUNCTION, "symbol") {

    public LispObject execute(LispObject arg) {
      return SYMBOL_FUNCTION_execute(arg);
    }
  };

  static final public LispObject SYMBOL_FUNCTION_execute(LispObject arg) {
    LispObject function = checkSymbol(arg).getSymbolFunction();
    if (function != null) {
      return function;
    }
    return error(new UndefinedFunction(arg));
  }

  // ### %set-symbol-function
  public static final Primitive _SET_SYMBOL_FUNCTION = new Primitive("%set-symbol-function", PACKAGE_SYS, false,
      "symbol function") {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_SYMBOL_FUNCTION_execute(first, second);
    }
  };

  static final public LispObject _SET_SYMBOL_FUNCTION_execute(LispObject first, LispObject second) {
    checkSymbol(first).setSymbolFunction(second);
    return second;
  }

  // ### symbol-plist
  public static final Primitive SYMBOL_PLIST = new Primitive(Symbol.SYMBOL_PLIST, "symbol") {

    public LispObject execute(LispObject arg) {
      return SYMBOL_PLIST_execute(arg);
    }
  };

  static final public LispObject SYMBOL_PLIST_execute(LispObject arg) {
    return checkSymbol(arg).getPropertyList();
  }

  // ### keywordp
  public static final Primitive KEYWORDP = new Primitive(Symbol.KEYWORDP, "object") {

    public LispObject execute(LispObject arg) {
      return KEYWORDP_execute(arg);
    }
  };

  static final public LispObject KEYWORDP_execute(LispObject arg) {
    if (arg instanceof Symbol) {
      if (checkSymbol(arg).getPackage() == PACKAGE_KEYWORD) {
        return T;
      }
    }
    return NIL;
  }

  // ### make-symbol
  public static final Primitive MAKE_SYMBOL = new Primitive(Symbol.MAKE_SYMBOL, "name") {

    public LispObject execute(LispObject arg) {
      return MAKE_SYMBOL_execute(arg);
    }
  };

  static final public LispObject MAKE_SYMBOL_execute(LispObject arg) {
    if (arg instanceof SimpleString) {
      return new Symbol((SimpleString) arg);
    }
    // Not a simple string.
    if (arg instanceof AbstractString) {
      return new Symbol(arg.getStringValue());
    }
    return type_error(arg, Symbol.STRING);
  }

  // ### makunbound
  public static final Primitive MAKUNBOUND = new Primitive(Symbol.MAKUNBOUND, "symbol") {

    public LispObject execute(LispObject arg) {
      return MAKUNBOUND_execute(arg);
    }
  };

  static final public LispObject MAKUNBOUND_execute(LispObject arg) {
    checkSymbol(arg).setSymbolValue(null);
    return arg;
  }

  // ### identity
  private static final Primitive IDENTITY = new Primitive(Symbol.IDENTITY, "object") {

    public LispObject execute(LispObject arg) {
      return IDENTITY_execute(arg);
    }
  };

  static final public LispObject IDENTITY_execute(LispObject arg) {
    return arg;
  }

  // ### compiled-function-p
  private static final Primitive COMPILED_FUNCTION_P = new Primitive(Symbol.COMPILED_FUNCTION_P, "object") {

    public LispObject execute(LispObject arg) {
      return COMPILED_FUNCTION_P_execute(arg);
    }
  };

  static final public LispObject COMPILED_FUNCTION_P_execute(LispObject arg) {
    return arg.typep(Symbol.COMPILED_FUNCTION);
  }

  // ### consp
  private static final Primitive CONSP = new Primitive(Symbol.CONSP, "object") {

    public LispObject execute(LispObject arg) {
      return CONSP_execute(arg);
    }
  };

  static final public LispObject CONSP_execute(LispObject arg) {
    return arg instanceof Cons ? T : NIL;
  }

  // ### listp
  private static final Primitive LISTP = new Primitive(Symbol.LISTP, "object") {

    public LispObject execute(LispObject arg) {
      return LISTP_execute(arg);
    }
  };

  static final public LispObject LISTP_execute(LispObject arg) {
    return arg.LISTP();
  }

  // ### abs
  private static final Primitive ABS = new Primitive(Symbol.ABS, "number") {

    public LispObject execute(LispObject arg) {
      return ABS_execute(arg);
    }
  };

  static final public LispObject ABS_execute(LispObject arg) {
    return arg.ABS();
  }

  // ### arrayp
  private static final Primitive ARRAYP = new Primitive(Symbol.ARRAYP, "object") {

    public LispObject execute(LispObject arg) {
      return ARRAYP_execute(arg);
    }
  };

  static final public LispObject ARRAYP_execute(LispObject arg) {
    return arg instanceof AbstractArray ? T : NIL;
  }

  // ### array-has-fill-pointer-p
  private static final Primitive ARRAY_HAS_FILL_POINTER_P = new Primitive(Symbol.ARRAY_HAS_FILL_POINTER_P, "array") {

    public LispObject execute(LispObject arg) {
      return ARRAY_HAS_FILL_POINTER_P_execute(arg);
    }
  };

  static final public LispObject ARRAY_HAS_FILL_POINTER_P_execute(LispObject arg) {
    return checkArray(arg).hasFillPointer() ? T : NIL;
  }

  // ### vectorp
  private static final Primitive VECTORP = new Primitive(Symbol.VECTORP, "object") {

    public LispObject execute(LispObject arg) {
      return VECTORP_execute(arg);
    }
  };

  static final public LispObject VECTORP_execute(LispObject arg) {
    return arg.VECTORP();
  }

  // ### simple-vector-p
  private static final Primitive SIMPLE_VECTOR_P = new Primitive(Symbol.SIMPLE_VECTOR_P, "object") {

    public LispObject execute(LispObject arg) {
      return SIMPLE_VECTOR_P_execute(arg);
    }
  };

  static final public LispObject SIMPLE_VECTOR_P_execute(LispObject arg) {
    return arg instanceof SimpleVector ? T : NIL;
  }

  // ### bit-vector-p
  private static final Primitive BIT_VECTOR_P = new Primitive(Symbol.BIT_VECTOR_P, "object") {

    public LispObject execute(LispObject arg) {
      return BIT_VECTOR_P_execute(arg);
    }
  };

  static final public LispObject BIT_VECTOR_P_execute(LispObject arg) {
    return arg instanceof AbstractBitVector ? T : NIL;
  }

  // ### simple-bit-vector-p
  private static final Primitive SIMPLE_BIT_VECTOR_P = new Primitive(Symbol.SIMPLE_BIT_VECTOR_P, "object") {

    public LispObject execute(LispObject arg) {
      return SIMPLE_BIT_VECTOR_P_execute(arg);
    }
  };

  static final public LispObject SIMPLE_BIT_VECTOR_P_execute(LispObject arg) {
    return arg.typep(Symbol.SIMPLE_BIT_VECTOR);
  }

  // ### %eval
  private static final Primitive _EVAL = new Primitive("%eval", PACKAGE_SYS, false, "form") {

    public LispObject execute(LispObject arg) {
      return _EVAL_execute(arg);
    }
  };

  static final public LispObject _EVAL_execute(LispObject arg) {
    return eval(arg, new Environment(), LispThread.currentThread());
  }

  // ### eq
  private static final Primitive EQ = new Primitive(Symbol.EQ, "x y") {

    public LispObject execute(LispObject first, LispObject second) {
      return EQ_execute(first, second);
    }
  };

  static final public LispObject EQ_execute(LispObject first, LispObject second) {
    return first == second ? T : NIL;
  }

  // ### eql
  private static final Primitive EQL = new Primitive(Symbol.EQL, "x y") {

    public LispObject execute(LispObject first, LispObject second) {
      return EQL_execute(first, second);
    }
  };

  static final public LispObject EQL_execute(LispObject first, LispObject second) {
    return first.eql(second) ? T : NIL;
  }

  // ### equal
  private static final Primitive EQUAL = new Primitive(Symbol.EQUAL, "x y") {

    public LispObject execute(LispObject first, LispObject second) {
      return EQUAL_execute(first, second);
    }
  };

  static final public LispObject EQUAL_execute(LispObject first, LispObject second) {
    return first.equal(second) ? T : NIL;
  }

  // ### equalp
  private static final Primitive EQUALP = new Primitive(Symbol.EQUALP, "x y") {

    public LispObject execute(LispObject first, LispObject second) {
      return EQUALP_execute(first, second);
    }
  };

  static final public LispObject EQUALP_execute(LispObject first, LispObject second) {
    return first.equalp(second) ? T : NIL;
  }

  // ### values
  private static final Primitive VALUES = new Primitive(Symbol.VALUES, "&rest object") {

    public LispObject execute() {
      return VALUES_execute();
    }

    public LispObject execute(LispObject arg) {
      return VALUES_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return VALUES_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return VALUES_execute(first, second, third);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
      return VALUES_execute(first, second, third, fourth);
    }

    public LispObject execute(LispObject[] args) {
      return VALUES_execute(args);
    }
  };

  static final public LispObject VALUES_execute() {
    return LispThread.currentThread().setValues();
  }

  static final public LispObject VALUES_execute(LispObject arg) {
    return LispThread.currentThread().setValues(arg);
  }

  static final public LispObject VALUES_execute(LispObject first, LispObject second) {
    return LispThread.currentThread().setValues(first, second);
  }

  static final public LispObject VALUES_execute(LispObject first, LispObject second, LispObject third) {
    return LispThread.currentThread().setValues(first, second, third);
  }

  static final public LispObject VALUES_execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
    return LispThread.currentThread().setValues(first, second, third, fourth);
  }

  static final public LispObject VALUES_execute(LispObject[] args) {
    return LispThread.currentThread().setValues(args);
  }

  // ### values-list list => element*
  // Returns the elements of the list as multiple values.
  private static final Primitive VALUES_LIST = new Primitive(Symbol.VALUES_LIST, "list") {

    public LispObject execute(LispObject arg) {
      return VALUES_LIST_execute(arg);
    }
  };

  static final public LispObject VALUES_LIST_execute(LispObject arg) {
    if (arg == NIL) {
      return LispThread.currentThread().setValues();
    }
    if (arg.cdr() == NIL) {
      return arg.car();
    }
    return LispThread.currentThread().setValues(arg.copyToArray());
  }

  // ### cons
  private static final Primitive CONS = new Primitive(Symbol.CONS, "object-1 object-2") {

    public LispObject execute(LispObject first, LispObject second) {
      return CONS_execute(first, second);
    }
  };

  static final public LispObject CONS_execute(LispObject first, LispObject second) {
    return new Cons(first, second);
  }

  // ### length
  private static final Primitive LENGTH = new Primitive(Symbol.LENGTH, "sequence") {

    public LispObject execute(LispObject arg) {
      return LENGTH_execute(arg);
    }
  };

  static final public LispObject LENGTH_execute(LispObject arg) {
    return arg.LENGTH();
  }

  // ### elt
  private static final Primitive ELT = new Primitive(Symbol.ELT, "sequence index") {

    public LispObject execute(LispObject first, LispObject second) {
      return ELT_execute(first, second);
    }
  };

  static final public LispObject ELT_execute(LispObject first, LispObject second) {
    return first.elt(Fixnum.getValue(second));
  }

  // ### atom
  private static final Primitive ATOM = new Primitive(Symbol.ATOM, "object") {

    public LispObject execute(LispObject arg) {
      return ATOM_execute(arg);
    }
  };

  static final public LispObject ATOM_execute(LispObject arg) {
    return arg instanceof Cons ? NIL : T;
  }

  // ### constantp
  private static final Primitive CONSTANTP = new Primitive(Symbol.CONSTANTP, "form &optional environment") {

    public LispObject execute(LispObject arg) {
      return CONSTANTP_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return CONSTANTP_execute(first, second);
    }
  };

  static final public LispObject CONSTANTP_execute(LispObject arg) {
    return arg.constantp() ? T : NIL;
  }

  static final public LispObject CONSTANTP_execute(LispObject first, LispObject second) {
    return first.constantp() ? T : NIL;
  }

  // ### functionp
  private static final Primitive FUNCTIONP = new Primitive(Symbol.FUNCTIONP, "object") {

    public LispObject execute(LispObject arg) {
      return FUNCTIONP_execute(arg);
    }
  };

  static final public LispObject FUNCTIONP_execute(LispObject arg) {
    return (arg instanceof Function || arg instanceof StandardGenericFunction) ? T : NIL;
  }

  // ### special-operator-p
  private static final Primitive SPECIAL_OPERATOR_P = new Primitive(Symbol.SPECIAL_OPERATOR_P, "symbol") {

    public LispObject execute(LispObject arg) {
      return SPECIAL_OPERATOR_P_execute(arg);
    }
  };

  static final public LispObject SPECIAL_OPERATOR_P_execute(LispObject arg) {
    return arg.isSpecialOperator() ? T : NIL;
  }

  // ### symbolp
  private static final Primitive SYMBOLP = new Primitive(Symbol.SYMBOLP, "object") {

    public LispObject execute(LispObject arg) {
      return SYMBOLP_execute(arg);
    }
  };

  static final public LispObject SYMBOLP_execute(LispObject arg) {
    return arg instanceof Symbol ? T : NIL;
  }

  // ### endp
  private static final Primitive ENDP = new Primitive(Symbol.ENDP, "list") {

    public LispObject execute(LispObject arg) {
      return ENDP_execute(arg);
    }
  };

  static final public LispObject ENDP_execute(LispObject arg) {
    return arg.endp() ? T : NIL;
  }

  // ### null
  private static final Primitive NULL = new Primitive(Symbol.NULL, "object") {

    public LispObject execute(LispObject arg) {
      return NULL_execute(arg);
    }
  };

  static final public LispObject NULL_execute(LispObject arg) {
    return arg == NIL ? T : NIL;
  }

  // ### not
  private static final Primitive NOT = new Primitive(Symbol.NOT, "x") {

    public LispObject execute(LispObject arg) {
      return NOT_execute(arg);
    }
  };

  static final public LispObject NOT_execute(LispObject arg) {
    return arg == NIL ? T : NIL;
  }

  // ### plusp
  private static final Primitive PLUSP = new Primitive(Symbol.PLUSP, "real") {

    public LispObject execute(LispObject arg) {
      return PLUSP_execute(arg);
    }
  };

  static final public LispObject PLUSP_execute(LispObject arg) {
    return arg.PLUSP();
  }

  // ### minusp
  private static final Primitive MINUSP = new Primitive(Symbol.MINUSP, "real") {

    public LispObject execute(LispObject arg) {
      return MINUSP_execute(arg);
    }
  };

  static final public LispObject MINUSP_execute(LispObject arg) {
    return arg.MINUSP();
  }

  // ### zerop
  private static final Primitive ZEROP = new Primitive(Symbol.ZEROP, "number") {

    public LispObject execute(LispObject arg) {
      return ZEROP_execute(arg);
    }
  };

  static final public LispObject ZEROP_execute(LispObject arg) {
    return arg.ZEROP();
  }

  // ### fixnump
  private static final Primitive FIXNUMP = new Primitive("fixnump", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return FIXNUMP_execute(arg);
    }
  };

  static final public LispObject FIXNUMP_execute(LispObject arg) {
    return arg instanceof Fixnum ? T : NIL;
  }

  // ### symbol-value
  private static final Primitive SYMBOL_VALUE = new Primitive(Symbol.SYMBOL_VALUE, "symbol") {

    public LispObject execute(LispObject arg) {
      return SYMBOL_VALUE_execute(arg);
    }
  };

  static final public LispObject SYMBOL_VALUE_execute(LispObject arg) {
    final LispObject value;
    value = checkSymbol(arg).symbolValue();
    if (value instanceof SymbolMacro) {
      return error(new LispError(arg.writeToString() + " has no dynamic value."));
    }
    return value;
  }

  // ### set symbol value => value
  private static final Primitive SET = new Primitive(Symbol.SET, "symbol value") {

    public LispObject execute(LispObject first, LispObject second) {
      return SET_execute(first, second);
    }
  };

  static final public LispObject SET_execute(LispObject first, LispObject second) {
    return LispThread.currentThread().setSpecialVariable(checkSymbol(first), second);
  }

  // ### rplaca
  private static final Primitive RPLACA = new Primitive(Symbol.RPLACA, "cons object") {

    public LispObject execute(LispObject first, LispObject second) {
      return RPLACA_execute(first, second);
    }
  };

  static final public LispObject RPLACA_execute(LispObject first, LispObject second) {
    first.setCar(second);
    return first;
  }

  // ### rplacd
  private static final Primitive RPLACD = new Primitive(Symbol.RPLACD, "cons object") {

    public LispObject execute(LispObject first, LispObject second) {
      return RPLACD_execute(first, second);
    }
  };

  static final public LispObject RPLACD_execute(LispObject first, LispObject second) {
    first.setCdr(second);
    return first;
  }

  // ### +
  private static final Primitive ADD = new Primitive(Symbol.PLUS, "&rest numbers") {

    public LispObject execute() {
      return ADD_execute();
    }

    public LispObject execute(LispObject arg) {
      return ADD_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return ADD_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return ADD_execute(first, second, third);
    }

    public LispObject execute(LispObject[] args) {
      return ADD_execute(args);
    }
  };

  static final public LispObject ADD_execute() {
    return Fixnum.ZERO;
  }

  static final public LispObject ADD_execute(LispObject arg) {
    if (arg.numberp()) {
      return arg;
    }
    return type_error(arg, Symbol.NUMBER);
  }

  static final public LispObject ADD_execute(LispObject first, LispObject second) {
    return first.add(second);
  }

  static final public LispObject ADD_execute(LispObject first, LispObject second, LispObject third) {
    return first.add(second).add(third);
  }

  static final public LispObject ADD_execute(LispObject[] args) {
    LispObject result = Fixnum.ZERO;
    final int length = args.length;
    for (int i = 0; i < length; i++) {
      result = result.add(args[i]);
    }
    return result;
  }

  // ### 1+
  private static final Primitive ONE_PLUS = new Primitive(Symbol.ONE_PLUS, "number") {

    public LispObject execute(LispObject arg) {
      return ONE_PLUS_execute(arg);
    }
  };

  static final public LispObject ONE_PLUS_execute(LispObject arg) {
    return arg.incr();
  }

  // ### -
  private static final Primitive SUBTRACT = new Primitive(Symbol.MINUS, "minuend &rest subtrahends") {

    public LispObject execute() {
      return SUBTRACT_execute();
    }

    public LispObject execute(LispObject arg) {
      return SUBTRACT_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return SUBTRACT_execute(first, second);
    }

    public LispObject execute(LispObject[] args) {
      return SUBTRACT_execute(args);
    }
  };

  static final public LispObject SUBTRACT_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject SUBTRACT_execute(LispObject arg) {
    return arg.negate();
  }

  static final public LispObject SUBTRACT_execute(LispObject first, LispObject second) {
    return first.subtract(second);
  }

  static final public LispObject SUBTRACT_execute(LispObject[] args) {
    LispObject result = args[0];
    for (int i = 1; i < args.length; i++) {
      result = result.subtract(args[i]);
    }
    return result;
  }

  // ### 1-
  private static final Primitive ONE_MINUS = new Primitive(Symbol.ONE_MINUS, "number") {

    public LispObject execute(LispObject arg) {
      return ONE_MINUS_execute(arg);
    }
  };

  static final public LispObject ONE_MINUS_execute(LispObject arg) {
    return arg.decr();
  }

  // ### when
  private static final SpecialOperator WHEN = new SpecialOperator(Symbol.WHEN) {

    public LispObject execute(LispObject args, Environment env) {
      return WHEN_execute(args, env);
    }
  };

  static final public LispObject WHEN_execute(LispObject args, Environment env) throws InlineThrows {
    if (args == NIL) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    if (eval(args.car(), env, thread) != NIL) {
      args = args.cdr();
      thread.clearValues();
      return progn(args, env, thread);
    }
    return thread.setValues(NIL);
  }

  // ### unless
  private static final SpecialOperator UNLESS = new SpecialOperator(Symbol.UNLESS) {

    public LispObject execute(LispObject args, Environment env) {
      return UNLESS_execute(args, env);
    }
  };

  static final public LispObject UNLESS_execute(LispObject args, Environment env) throws InlineThrows {
    if (args == NIL) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    if (eval(args.car(), env, thread) == NIL) {
      args = args.cdr();
      thread.clearValues();
      return progn(args, env, thread);
    }
    return thread.setValues(NIL);
  }

  // ### %stream-output-object object stream => object
  private static final Primitive _STREAM_OUTPUT_OBJECT = new Primitive("%stream-output-object", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _STREAM_OUTPUT_OBJECT_execute(first, second);
    }
  };

  static final public LispObject _STREAM_OUTPUT_OBJECT_execute(LispObject first, LispObject second) {
    checkStream(second)._writeString(first.writeToString());
    return first;
  }

  // ### %output-object object stream => object
  private static final Primitive _OUTPUT_OBJECT = new Primitive("%output-object", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _OUTPUT_OBJECT_execute(first, second);
    }
  };

  static final public LispObject _OUTPUT_OBJECT_execute(LispObject first, LispObject second) {
    final LispObject out;
    if (second == T) {
      out = Symbol.TERMINAL_IO.symbolValue();
    } else if (second == NIL) {
      out = Symbol.STANDARD_OUTPUT.symbolValue();
    } else {
      out = second;
    }
    checkStream(out)._writeString(first.writeToString());
    return first;
  }

  // ### %write-to-string object => string
  private static final Primitive _WRITE_TO_STRING = new Primitive("%write-to-string", PACKAGE_SYS, false) {

    public LispObject execute(LispObject arg) {
      return _WRITE_TO_STRING_execute(arg);
    }
  };

  static final public LispObject _WRITE_TO_STRING_execute(LispObject arg) {
    return new SimpleString(arg.writeToString());
  }

  // ### %stream-terpri output-stream => nil
  private static final Primitive _STREAM_TERPRI = new Primitive("%stream-terpri", PACKAGE_SYS, true, "output-stream") {

    public LispObject execute(LispObject arg) {
      return _STREAM_TERPRI_execute(arg);
    }
  };

  static final public LispObject _STREAM_TERPRI_execute(LispObject arg) {
    checkStream(arg)._writeChar('\n');
    return NIL;
  }

  // ### %terpri output-stream => nil
  private static final Primitive _TERPRI = new Primitive("%terpri", PACKAGE_SYS, false, "output-stream") {

    public LispObject execute(LispObject arg) {
      return _TERPRI_execute(arg);
    }
  };

  static final public LispObject _TERPRI_execute(LispObject arg) {
    if (arg == T) {
      arg = Symbol.TERMINAL_IO.symbolValue();
    } else if (arg == NIL) {
      arg = Symbol.STANDARD_OUTPUT.symbolValue();
    }
    final Stream stream;
    stream = checkStream(arg);
    return stream.terpri();
  }

  // ### %fresh-line
  // %fresh-line &optional output-stream => generalized-boolean
  private static final Primitive _FRESH_LINE = new Primitive("%fresh-line", PACKAGE_SYS, false, "output-stream") {

    public LispObject execute(LispObject arg) {
      return _FRESH_LINE_execute(arg);
    }
  };

  static final public LispObject _FRESH_LINE_execute(LispObject arg) {
    if (arg == T) {
      arg = Symbol.TERMINAL_IO.symbolValue();
    } else if (arg == NIL) {
      arg = Symbol.STANDARD_OUTPUT.symbolValue();
    }
    final Stream stream;
    stream = checkStream(arg);
    return stream.freshLine();
  }

  // ### boundp
  // Determines only whether a symbol has a value in the global environment;
  // any lexical bindings are ignored.
  private static final Primitive BOUNDP = new Primitive(Symbol.BOUNDP, "symbol") {

    public LispObject execute(LispObject arg) {
      return BOUNDP_execute(arg);
    }
  };

  static final public LispObject BOUNDP_execute(LispObject arg) {
    final Symbol symbol;
    symbol = checkSymbol(arg);
    // PROGV: "If too few values are supplied, the remaining symbols
    // are bound and then made to have no value." So BOUNDP must
    // explicitly check for a binding with no value.
    SpecialBinding binding = LispThread.currentThread().getSpecialBinding(symbol);
    if (binding != null) {
      return binding.value != null ? T : NIL;
    }
    // No binding.
    return symbol.getSymbolValue() != null ? T : NIL;
  }

  // ### fboundp
  private static final Primitive FBOUNDP = new Primitive(Symbol.FBOUNDP, "name") {

    public LispObject execute(LispObject arg) {
      return FBOUNDP_execute(arg);
    }
  };

  static final public LispObject FBOUNDP_execute(LispObject arg) {
    if (arg instanceof Symbol) {
      return arg.getSymbolFunction() != null ? T : NIL;
    }
    if (isValidSetfFunctionName(arg)) {
      LispObject f = get(arg.cadr(), Symbol.SETF_FUNCTION, null);
      return f != null ? T : NIL;
    }
    return type_error(arg, FUNCTION_NAME);
  }

  // ### fmakunbound name => name
  private static final Primitive FMAKUNBOUND = new Primitive(Symbol.FMAKUNBOUND, "name") {

    public LispObject execute(LispObject arg) {
      return FMAKUNBOUND_execute(arg);
    }
  };

  static final public LispObject FMAKUNBOUND_execute(LispObject arg) {
    if (arg instanceof Symbol) {
      checkSymbol(arg).setSymbolFunction(null);
      return arg;
    }
    if (isValidSetfFunctionName(arg)) {
      remprop((Symbol) arg.cadr(), Symbol.SETF_FUNCTION);
      return arg;
    }
    return type_error(arg, FUNCTION_NAME);
  }

  // ### setf-function-name-p
  private static final Primitive SETF_FUNCTION_NAME_P = new Primitive("setf-function-name-p", PACKAGE_SYS, true,
      "thing") {

    public LispObject execute(LispObject arg) {
      return SETF_FUNCTION_NAME_P_execute(arg);
    }
  };

  static final public LispObject SETF_FUNCTION_NAME_P_execute(LispObject arg) {
    return isValidSetfFunctionName(arg) ? T : NIL;
  }

  // ### remprop
  private static final Primitive REMPROP = new Primitive(Symbol.REMPROP, "symbol indicator") {

    public LispObject execute(LispObject first, LispObject second) {
      return REMPROP_execute(first, second);
    }
  };

  static final public LispObject REMPROP_execute(LispObject first, LispObject second) {
    return remprop(checkSymbol(first), second);
  }

  // ### nconc
  private static final Primitive NCONC = new Primitive(Symbol.NCONC, "&rest lists") {

    public LispObject execute() {
      return NCONC_execute();
    }

    public LispObject execute(LispObject arg) {
      return NCONC_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return NCONC_execute(first, second);
    }

    public LispObject execute(LispObject[] array) {
      return NCONC_execute(array);
    }
  };

  static final public LispObject NCONC_execute() {
    return NIL;
  }

  static final public LispObject NCONC_execute(LispObject arg) {
    return arg;
  }

  static final public LispObject NCONC_execute(LispObject first, LispObject second) {
    if (first == NIL) {
      return second;
    }
    if (first instanceof Cons) {
      LispObject result = first;
      Cons splice = null;
      while (first instanceof Cons) {
        splice = (Cons) first;
        first = splice.cdr;
      }
      splice.cdr = second;
      return result;
    }
    return type_error(first, Symbol.LIST);
  }

  static final public LispObject NCONC_execute(LispObject[] array) {
    LispObject result = null;
    Cons splice = null;
    final int limit = array.length - 1;
    int i;
    for (i = 0; i < limit; i++) {
      LispObject list = array[i];
      if (list == NIL) {
        continue;
      }
      if (list instanceof Cons) {
        if (splice != null) {
          splice.cdr = list;
          splice = (Cons) list;
        }
        while (list instanceof Cons) {
          if (result == null) {
            result = list;
            splice = (Cons) result;
          } else {
            splice = (Cons) list;
          }
          list = splice.cdr;
        }
      } else {
        type_error(list, Symbol.LIST);
      }
    }
    if (result == null) {
      return array[i];
    }
    splice.cdr = array[i];
    return result;
  }

  // ### =
  // Numeric equality.
  private static final Primitive EQUALS = new Primitive(Symbol.EQUALS, "&rest numbers") {

    public LispObject execute() {
      return EQUALS_execute();
    }

    public LispObject execute(LispObject arg) {
      return EQUALS_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return EQUALS_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return EQUALS_execute(first, second, third);
    }

    public LispObject execute(LispObject[] array) {
      return EQUALS_execute(array);
    }
  };

  static final public LispObject EQUALS_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject EQUALS_execute(LispObject arg) {
    return T;
  }

  static final public LispObject EQUALS_execute(LispObject first, LispObject second) {
    return first.isEqualTo(second) ? T : NIL;
  }

  static final public LispObject EQUALS_execute(LispObject first, LispObject second, LispObject third) {
    if (first.isEqualTo(second) && second.isEqualTo(third)) {
      return T;
    } else {
      return NIL;
    }
  }

  static final public LispObject EQUALS_execute(LispObject[] array) {
    final int length = array.length;
    final LispObject obj = array[0];
    for (int i = 1; i < length; i++) {
      if (array[i].isNotEqualTo(obj)) {
        return NIL;
      }
    }
    return T;
  }

  // ### /=
  // Returns true if no two numbers are the same; otherwise returns false.
  private static final Primitive NOT_EQUALS = new Primitive(Symbol.NOT_EQUALS, "&rest numbers") {

    public LispObject execute() {
      return NOT_EQUALS_execute();
    }

    public LispObject execute(LispObject arg) {
      return NOT_EQUALS_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return NOT_EQUALS_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return NOT_EQUALS_execute(first, second, third);
    }

    public LispObject execute(LispObject[] array) {
      return NOT_EQUALS_execute(array);
    }
  };

  static final public LispObject NOT_EQUALS_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject NOT_EQUALS_execute(LispObject arg) {
    return T;
  }

  static final public LispObject NOT_EQUALS_execute(LispObject first, LispObject second) {
    return first.isNotEqualTo(second) ? T : NIL;
  }

  static final public LispObject NOT_EQUALS_execute(LispObject first, LispObject second, LispObject third) {
    if (first.isEqualTo(second)) {
      return NIL;
    }
    if (first.isEqualTo(third)) {
      return NIL;
    }
    if (second.isEqualTo(third)) {
      return NIL;
    }
    return T;
  }

  static final public LispObject NOT_EQUALS_execute(LispObject[] array) {
    final int length = array.length;
    for (int i = 0; i < length; i++) {
      final LispObject obj = array[i];
      for (int j = i + 1; j < length; j++) {
        if (array[j].isEqualTo(obj)) {
          return NIL;
        }
      }
    }
    return T;
  }

  // ### <
  // Numeric comparison.
  private static final Primitive LT = new Primitive(Symbol.LT, "&rest numbers") {

    public LispObject execute() {
      return LT_execute();
    }

    public LispObject execute(LispObject arg) {
      return LT_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return LT_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return LT_execute(first, second, third);
    }

    public LispObject execute(LispObject[] array) {
      return LT_execute(array);
    }
  };

  static final public LispObject LT_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject LT_execute(LispObject arg) {
    return T;
  }

  static final public LispObject LT_execute(LispObject first, LispObject second) {
    return first.isLessThan(second) ? T : NIL;
  }

  static final public LispObject LT_execute(LispObject first, LispObject second, LispObject third) {
    if (first.isLessThan(second) && second.isLessThan(third)) {
      return T;
    } else {
      return NIL;
    }
  }

  static final public LispObject LT_execute(LispObject[] array) {
    final int length = array.length;
    for (int i = 1; i < length; i++) {
      if (array[i].isLessThanOrEqualTo(array[i - 1])) {
        return NIL;
      }
    }
    return T;
  }

  // ### <=
  private static final Primitive LE = new Primitive(Symbol.LE, "&rest numbers") {

    public LispObject execute() {
      return LE_execute();
    }

    public LispObject execute(LispObject arg) {
      return LE_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return LE_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return LE_execute(first, second, third);
    }

    public LispObject execute(LispObject[] array) {
      return LE_execute(array);
    }
  };

  static final public LispObject LE_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject LE_execute(LispObject arg) {
    return T;
  }

  static final public LispObject LE_execute(LispObject first, LispObject second) {
    return first.isLessThanOrEqualTo(second) ? T : NIL;
  }

  static final public LispObject LE_execute(LispObject first, LispObject second, LispObject third) {
    if (first.isLessThanOrEqualTo(second) && second.isLessThanOrEqualTo(third)) {
      return T;
    } else {
      return NIL;
    }
  }

  static final public LispObject LE_execute(LispObject[] array) {
    final int length = array.length;
    for (int i = 1; i < length; i++) {
      if (array[i].isLessThan(array[i - 1])) {
        return NIL;
      }
    }
    return T;
  }

  // ### >
  private static final Primitive GT = new Primitive(Symbol.GT, "&rest numbers") {

    public LispObject execute() {
      return GT_execute();
    }

    public LispObject execute(LispObject arg) {
      return GT_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return GT_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return GT_execute(first, second, third);
    }

    public LispObject execute(LispObject[] array) {
      return GT_execute(array);
    }
  };

  static final public LispObject GT_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject GT_execute(LispObject arg) {
    return T;
  }

  static final public LispObject GT_execute(LispObject first, LispObject second) {
    return first.isGreaterThan(second) ? T : NIL;
  }

  static final public LispObject GT_execute(LispObject first, LispObject second, LispObject third) {
    if (first.isGreaterThan(second) && second.isGreaterThan(third)) {
      return T;
    } else {
      return NIL;
    }
  }

  static final public LispObject GT_execute(LispObject[] array) {
    final int length = array.length;
    for (int i = 1; i < length; i++) {
      if (array[i].isGreaterThanOrEqualTo(array[i - 1])) {
        return NIL;
      }
    }
    return T;
  }

  // ### >=
  private static final Primitive GE = new Primitive(Symbol.GE, "&rest numbers") {

    public LispObject execute() {
      return GE_execute();
    }

    public LispObject execute(LispObject arg) {
      return GE_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return GE_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return GE_execute(first, second, third);
    }

    public LispObject execute(LispObject[] array) {
      return GE_execute(array);
    }
  };

  static final public LispObject GE_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject GE_execute(LispObject arg) {
    return T;
  }

  static final public LispObject GE_execute(LispObject first, LispObject second) {
    return first.isGreaterThanOrEqualTo(second) ? T : NIL;
  }

  static final public LispObject GE_execute(LispObject first, LispObject second, LispObject third) {
    if (first.isGreaterThanOrEqualTo(second) && second.isGreaterThanOrEqualTo(third)) {
      return T;
    } else {
      return NIL;
    }
  }

  static final public LispObject GE_execute(LispObject[] array) {
    final int length = array.length;
    for (int i = 1; i < length; i++) {
      if (array[i].isGreaterThan(array[i - 1])) {
        return NIL;
      }
    }
    return T;
  }

  // ### nth n list => object
  private static final Primitive NTH = new Primitive(Symbol.NTH, "n list") {

    public LispObject execute(LispObject first, LispObject second) {
      return NTH_execute(first, second);
    }
  };

  static final public LispObject NTH_execute(LispObject first, LispObject second) {
    return second.NTH(first);
  }

  // ### %set-nth n list new-object => new-object
  private static final Primitive _SET_NTH = new Primitive("%set-nth", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return _SET_NTH_execute(first, second, third);
    }
  };

  static final public LispObject _SET_NTH_execute(LispObject first, LispObject second, LispObject third) {
    int index = Fixnum.getValue(first);
    if (index < 0) {
      error(new TypeError("(SETF NTH): invalid index " + index + "."));
    }
    int i = 0;
    while (true) {
      if (i == index) {
        second.setCar(third);
        return third;
      }
      second = second.cdr();
      if (second == NIL) {
        return error(new LispError("(SETF NTH): the index " + index + "is too large."));
      }
      ++i;
    }
  }

  // ### nthcdr
  private static final Primitive NTHCDR = new Primitive(Symbol.NTHCDR, "n list") {

    public LispObject execute(LispObject first, LispObject second) {
      return NTHCDR_execute(first, second);
    }
  };

  static final public LispObject NTHCDR_execute(LispObject first, LispObject second) {
    final int index = Fixnum.getValue(first);
    if (index < 0) {
      return type_error(first, list(Symbol.INTEGER, Fixnum.ZERO));
    }
    for (int i = 0; i < index; i++) {
      second = second.cdr();
      if (second == NIL) {
        return NIL;
      }
    }
    return second;
  }

  /** Stub to be replaced later when signal.lisp has been loaded. */
  // ### error
  @NoInline
  private static final Primitive ERROR = new Primitive(Symbol.ERROR, "datum &rest arguments") {

    public LispObject execute(LispObject[] args) {
      return ERROR_execute(args);
    }
  };

  @NoInline
  static final public LispObject ERROR_execute(LispObject[] args) {
    Error e = new Error();

    e.printStackTrace();

    System.out.println("ERROR placeholder called with arguments:");

    if (args.length == 1 && args[0] instanceof Condition) {
      System.out.println(args[0].writeToString());
      System.out.println(((Condition) args[0]).getConditionReport());
    } else {
      for (LispObject a : args) {
        System.out.println(a.writeToString());
      }
    }

    // ###FIXME: Bail out, but do it nicer...
    System.exit(1);
    return NIL;
  }

  /** Stub replaced when compiler-pass2.lisp has been loaded */
  // ### autocompile
  @NoInline
  private static final Primitive AUTOCOMPILE = new Primitive(Symbol.AUTOCOMPILE, "function") {

    public LispObject execute(LispObject function) {
      return AUTOCOMPILE_execute(function);
    }
  };

  static final public LispObject AUTOCOMPILE_execute(LispObject function) {
    return NIL;
  }

  // ### signal
  /**
   * Placeholder function, to be replaced by the function defined in signal.lisp
   * Calling this function is an error: we're not set up for signalling yet.
   */
  @NoInline
  private static final Primitive SIGNAL = new Primitive(Symbol.SIGNAL, "datum &rest arguments") {

    public LispObject execute(LispObject[] args) {
      return SIGNAL_execute(args);
    }
  };

  static final public LispObject SIGNAL_execute(LispObject[] args) throws InlineThrows {
    if (args.length < 1) {
      return wrongNumberOfArgumetnts();
    }
    if (args[0] instanceof Condition) {
      return error((Condition) args[0]);
    }
    return error(new SimpleCondition());
  }

  // ### undefined-function-called
  // Redefined in restart.lisp.
  @NoInline
  private static final Primitive UNDEFINED_FUNCTION_CALLED = new Primitive(Symbol.UNDEFINED_FUNCTION_CALLED,
      "name arguments") {

    public LispObject execute(LispObject first, LispObject second) {
      return UNDEFINED_FUNCTION_CALLED_execute(first, second);
    }
  };

  static final public LispObject UNDEFINED_FUNCTION_CALLED_execute(LispObject first, LispObject second) {
    return error(new UndefinedFunction(first));
  }

  // ### %format

  private static final Primitive _FORMAT = new Primitive("%format", PACKAGE_SYS, false,
      "destination control-string &rest args") {

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return _FORMAT_execute(first, second, third);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
      return _FORMAT_execute(first, second, third, fourth);
    }

    public LispObject execute(LispObject[] args) {
      return _FORMAT_execute(args);
    }

  };

  static private final String _format(LispObject[] args) {
    LispObject formatControl = args[0];
    LispObject formatArguments = NIL;
    for (int i = 1; i < args.length; i++) {
      formatArguments = new Cons(args[i], formatArguments);
    }
    formatArguments = formatArguments.nreverse();
    return format(formatControl, formatArguments);
  }

  static private final LispObject outputFormattedString(String s, LispObject destination) {
    if (destination == T) {
      checkCharacterOutputStream(Symbol.STANDARD_OUTPUT.symbolValue())._writeString(s);
      return NIL;
    }
    if (destination == NIL) {
      return new SimpleString(s);
    }
    if (destination instanceof TwoWayStream) {
      Stream out = ((TwoWayStream) destination).getOutputStream();
      if (out instanceof Stream) {
        (out)._writeString(s);
        return NIL;
      }
      error(new TypeError("The value " + destination.writeToString() + " is not a character output stream."));
    }
    if (destination instanceof Stream) {
      ((Stream) destination)._writeString(s);
      return NIL;
    }
    return NIL;
  }

  static final public LispObject _FORMAT_execute(LispObject first, LispObject second, LispObject third) {
    LispObject destination = first;
    // Copy remaining arguments.
    LispObject[] _args = new LispObject[2];
    _args[0] = second;
    _args[1] = third;
    String s = _format(_args);
    return outputFormattedString(s, destination);
  }

  static final public LispObject _FORMAT_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth) {
    LispObject destination = first;
    // Copy remaining arguments.
    LispObject[] _args = new LispObject[3];
    _args[0] = second;
    _args[1] = third;
    _args[2] = fourth;
    String s = _format(_args);
    return outputFormattedString(s, destination);
  }

  static final public LispObject _FORMAT_execute(LispObject[] args) throws InlineThrows {
    if (args.length < 2) {
      return wrongNumberOfArgumetnts();
    }
    LispObject destination = args[0];
    // Copy remaining arguments.
    LispObject[] _args = new LispObject[args.length - 1];
    for (int i = 0; i < _args.length; i++) {
      _args[i] = args[i + 1];
    }
    String s = _format(_args);
    return outputFormattedString(s, destination);
  }

  private static final Symbol _SIMPLE_FORMAT_FUNCTION_ = internSpecial("*SIMPLE-FORMAT-FUNCTION*", PACKAGE_SYS, _FORMAT);

  @NoInline
  // ### %defun name definition => name
  private static final Primitive _DEFUN = new Primitive("%defun", PACKAGE_SYS, true, "name definition") {

    public LispObject execute(LispObject name, LispObject definition) {
      return _DEFUN_execute(name, definition);
    }
  };

  static final public LispObject _DEFUN_execute(LispObject name, LispObject definition) {
    if (name instanceof Symbol) {
      Symbol symbol = (Symbol) name;
      if (symbol.getSymbolFunction() instanceof SpecialOperator) {
        String message = symbol.getName() + " is a special operator and may not be redefined.";
        return error(new ProgramError(message));
      }
    } else if (!isValidSetfFunctionName(name)) {
      return type_error(name, FUNCTION_NAME);
    }
    if (definition instanceof Function) {
      Symbol.FSET.execute(name, definition, NIL, ((Function) definition).getLambdaList());
      return name;
    }
    return type_error(definition, Symbol.FUNCTION);
  }

  // ### fdefinition-block-name
  private static final Primitive FDEFINITION_BLOCK_NAME = new Primitive("fdefinition-block-name", PACKAGE_SYS, true,
      "function-name") {

    public LispObject execute(LispObject arg) {
      return FDEFINITION_BLOCK_NAME_execute(arg);
    }
  };

  static final public LispObject FDEFINITION_BLOCK_NAME_execute(LispObject arg) {
    if (arg instanceof Symbol) {
      return arg;
    }
    if (isValidSetfFunctionName(arg)) {
      return arg.cadr();
    }
    return type_error(arg, FUNCTION_NAME);
  }

  // ### macro-function
  private static final Primitive MACRO_FUNCTION = new Primitive(Symbol.MACRO_FUNCTION, "symbol &optional environment") {

    public LispObject execute(LispObject arg) {
      return MACRO_FUNCTION_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return MACRO_FUNCTION_execute(first, second);
    }
  };

  static final public LispObject MACRO_FUNCTION_execute(LispObject arg) {
    LispObject obj = arg.getSymbolFunction();
    if (obj instanceof AutoloadMacro) {
      ((AutoloadMacro) obj).load();
      obj = arg.getSymbolFunction();
    }
    if (obj instanceof MacroObject) {
      return ((MacroObject) obj).expander;
    }
    if (obj instanceof SpecialOperator) {
      obj = get(arg, Symbol.MACROEXPAND_MACRO, NIL);
      if (obj instanceof AutoloadMacro) {
        ((AutoloadMacro) obj).load();
        obj = get(arg, Symbol.MACROEXPAND_MACRO, NIL);
      }
      if (obj instanceof MacroObject) {
        return ((MacroObject) obj).expander;
      }
    }
    return NIL;
  }

  static final public LispObject MACRO_FUNCTION_execute(LispObject first, LispObject second) {
    LispObject obj;
    if (second != NIL) {
      Environment env = checkEnvironment(second);
      obj = env.lookupFunction(first);
    } else {
      obj = first.getSymbolFunction();
    }
    if (obj instanceof AutoloadMacro) {
      ((AutoloadMacro) obj).load();
      obj = first.getSymbolFunction();
    }
    if (obj instanceof MacroObject) {
      return ((MacroObject) obj).expander;
    }
    if (obj instanceof SpecialOperator) {
      obj = get(first, Symbol.MACROEXPAND_MACRO, NIL);
      if (obj instanceof AutoloadMacro) {
        ((AutoloadMacro) obj).load();
        obj = get(first, Symbol.MACROEXPAND_MACRO, NIL);
      }
      if (obj instanceof MacroObject) {
        return ((MacroObject) obj).expander;
      }
    }
    return NIL;
  }

  // ### defmacro
  private static final SpecialOperator DEFMACRO = new SpecialOperator(Symbol.DEFMACRO) {

    public LispObject execute(LispObject args, Environment env) {
      return DEFMACRO_execute(args, env);
    }
  };

  static final public LispObject DEFMACRO_execute(LispObject args, Environment env) {
    Symbol symbol = checkSymbol(args.car());
    LispObject lambdaList = checkList(args.cadr());
    LispObject body = args.cddr();
    LispObject block = new Cons(Symbol.BLOCK, new Cons(symbol, body));
    LispObject toBeApplied = list(Symbol.FUNCTION, list(Symbol.LAMBDA, lambdaList, block));
    final LispThread thread = LispThread.currentThread();
    LispObject formArg = gensym("FORM-", thread);
    LispObject envArg = gensym("ENV-", thread);
    // Ignored.
    LispObject expander = list(Symbol.LAMBDA, list(formArg, envArg), list(Symbol.APPLY, toBeApplied, list(Symbol.CDR,
        formArg)));
    Closure expansionFunction = new Closure(expander, env);
    MacroObject macroObject = new MacroObject(symbol, expansionFunction);
    if (symbol.getSymbolFunction() instanceof SpecialOperator) {
      put(symbol, Symbol.MACROEXPAND_MACRO, macroObject);
    } else {
      symbol.setSymbolFunction(macroObject);
    }
    macroObject.setLambdaList(lambdaList);
    thread._values = null;
    return symbol;
  }

  // ### make-macro
  private static final Primitive MAKE_MACRO = new Primitive("make-macro", PACKAGE_SYS, true, "name expansion-function") {

    public LispObject execute(LispObject first, LispObject second) {
      return MAKE_MACRO_execute(first, second);
    }
  };

  static final public LispObject MAKE_MACRO_execute(LispObject first, LispObject second) {
    return new MacroObject(first, second);
  }

  // ### macro-function-p
  private static final Primitive MACRO_FUNCTION_P = new Primitive("macro-function-p", PACKAGE_SYS, true, "value") {

    public LispObject execute(LispObject arg) {
      return MACRO_FUNCTION_P_execute(arg);
    }
  };

  static final public LispObject MACRO_FUNCTION_P_execute(LispObject arg) {
    return (arg instanceof MacroObject) ? T : NIL;
  }

  // ### make-symbol-macro
  private static final Primitive MAKE_SYMBOL_MACRO = new Primitive("make-symbol-macro", PACKAGE_SYS, true, "expansion") {

    public LispObject execute(LispObject arg) {
      return MAKE_SYMBOL_MACRO_execute(arg);
    }
  };

  static final public LispObject MAKE_SYMBOL_MACRO_execute(LispObject arg) {
    return new SymbolMacro(arg);
  }

  // ### symbol-macro-p
  private static final Primitive SYMBOL_MACRO_P = new Primitive("symbol-macro-p", PACKAGE_SYS, true, "value") {

    public LispObject execute(LispObject arg) {
      return SYMBOL_MACRO_P_execute(arg);
    }
  };

  static final public LispObject SYMBOL_MACRO_P_execute(LispObject arg) {
    return (arg instanceof SymbolMacro) ? T : NIL;
  }

  // ### %defparameter
  private static final Primitive _DEFPARAMETER = new Primitive("%defparameter", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return _DEFPARAMETER_execute(first, second, third);
    }
  };

  static final public LispObject _DEFPARAMETER_execute(LispObject first, LispObject second, LispObject third) {
    final Symbol symbol;
    symbol = checkSymbol(first);
    if (third instanceof AbstractString) {
      symbol.setDocumentation(Symbol.VARIABLE, third);
    } else if (third != NIL) {
      type_error(third, Symbol.STRING);
    }
    symbol.initializeSpecial(second);
    return symbol;
  }

  // ### %defvar
  private static final Primitive _DEFVAR = new Primitive("%defvar", PACKAGE_SYS, false) {

    public LispObject execute(LispObject arg) {
      return _DEFVAR_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return _DEFVAR_execute(first, second);
    }
  };

  static final public LispObject _DEFVAR_execute(LispObject arg) {
    final Symbol symbol;
    symbol = checkSymbol(arg);
    symbol.setSpecial(true);
    return symbol;
  }

  static final public LispObject _DEFVAR_execute(LispObject first, LispObject second) {
    final Symbol symbol;
    symbol = checkSymbol(first);
    symbol.initializeSpecial(second);
    return symbol;
  }

  // ### %defconstant name initial-value documentation => name
  private static final Primitive _DEFCONSTANT = new Primitive("%defconstant", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return _DEFCONSTANT_execute(first, second, third);
    }
  };

  static final public LispObject _DEFCONSTANT_execute(LispObject first, LispObject second, LispObject third) {
    final Symbol symbol;
    symbol = checkSymbol(first);
    if (third != NIL) {
      if (third instanceof AbstractString) {
        symbol.setDocumentation(Symbol.VARIABLE, third);
      } else {
        return type_error(third, Symbol.STRING);
      }
    }
    symbol.initializeConstant(second);
    return symbol;
  }

  // ### cond
  private static final SpecialOperator COND = new SpecialOperator(Symbol.COND, "&rest clauses") {

    public LispObject execute(LispObject args, Environment env) {
      return COND_execute(args, env);
    }
  };

  static final public LispObject COND_execute(LispObject args, Environment env) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = NIL;
    while (args != NIL) {
      LispObject clause = args.car();
      result = eval(clause.car(), env, thread);
      thread._values = null;
      if (result != NIL) {
        LispObject body = clause.cdr();
        while (body != NIL) {
          result = eval(body.car(), env, thread);
          body = ((Cons) body).cdr;
        }
        return result;
      }
      args = ((Cons) args).cdr;
    }
    return result;
  }

  // ### case
  private static final SpecialOperator CASE = new SpecialOperator(Symbol.CASE, "keyform &body cases") {

    public LispObject execute(LispObject args, Environment env) {
      return CASE_execute(args, env);
    }
  };

  static final public LispObject CASE_execute(LispObject args, Environment env) {
    final LispThread thread = LispThread.currentThread();
    LispObject key = eval(args.car(), env, thread);
    args = args.cdr();
    while (args != NIL) {
      LispObject clause = args.car();
      LispObject keys = clause.car();
      boolean match = false;
      if (keys.listp()) {
        while (keys != NIL) {
          LispObject candidate = keys.car();
          if (key.eql(candidate)) {
            match = true;
            break;
          }
          keys = keys.cdr();
        }
      } else {
        LispObject candidate = keys;
        if (candidate == T || candidate == Symbol.OTHERWISE) {
          match = true;
        } else if (key.eql(candidate)) {
          match = true;
        }
      }
      if (match) {
        return progn(clause.cdr(), env, thread);
      }
      args = args.cdr();
    }
    return NIL;
  }

  // ### ecase
  private static final SpecialOperator ECASE = new SpecialOperator(Symbol.ECASE, "keyform &body cases") {

    public LispObject execute(LispObject args, Environment env) {
      return ECASE_execute(args, env);
    }
  };

  static final public LispObject ECASE_execute(LispObject args, Environment env) {
    final LispThread thread = LispThread.currentThread();
    LispObject key = eval(args.car(), env, thread);
    LispObject clauses = args.cdr();
    while (clauses != NIL) {
      LispObject clause = clauses.car();
      LispObject keys = clause.car();
      boolean match = false;
      if (keys.listp()) {
        while (keys != NIL) {
          LispObject candidate = keys.car();
          if (key.eql(candidate)) {
            match = true;
            break;
          }
          keys = keys.cdr();
        }
      } else {
        LispObject candidate = keys;
        if (key.eql(candidate)) {
          match = true;
        }
      }
      if (match) {
        return progn(clause.cdr(), env, thread);
      }
      clauses = clauses.cdr();
    }
    LispObject expectedType = NIL;
    clauses = args.cdr();
    while (clauses != NIL) {
      LispObject clause = clauses.car();
      LispObject keys = clause.car();
      if (keys.listp()) {
        while (keys != NIL) {
          expectedType = expectedType.push(keys.car());
          keys = keys.cdr();
        }
      } else {
        expectedType = expectedType.push(keys);
      }
      clauses = clauses.cdr();
    }
    expectedType = expectedType.nreverse();
    expectedType = expectedType.push(Symbol.MEMBER);
    return type_error(key, expectedType);
  }

  // ### upgraded-array-element-type typespec &optional environment
  // => upgraded-typespec

  private static final Primitive UPGRADED_ARRAY_ELEMENT_TYPE = new Primitive(Symbol.UPGRADED_ARRAY_ELEMENT_TYPE,
      "typespec &optional environment") {

    public LispObject execute(LispObject arg) {
      return UPGRADED_ARRAY_ELEMENT_TYPE_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return UPGRADED_ARRAY_ELEMENT_TYPE_execute(first, second);
    }
  };

  static final public LispObject UPGRADED_ARRAY_ELEMENT_TYPE_execute(LispObject arg) {
    return getUpgradedArrayElementType(arg);
  }

  static final public LispObject UPGRADED_ARRAY_ELEMENT_TYPE_execute(LispObject first, LispObject second) {
    // Ignore environment.
    return getUpgradedArrayElementType(first);
  }

  // ### array-rank array => rank
  private static final Primitive ARRAY_RANK = new Primitive(Symbol.ARRAY_RANK, "array") {

    public LispObject execute(LispObject arg) {
      return ARRAY_RANK_execute(arg);
    }
  };

  static final public LispObject ARRAY_RANK_execute(LispObject arg) {
    return Fixnum.getInstance(checkArray(arg).getRank());
  }

  // ### array-dimensions array => dimensions
  // Returns a list of integers. Fill pointer (if any) is ignored.
  private static final Primitive ARRAY_DIMENSIONS = new Primitive(Symbol.ARRAY_DIMENSIONS, "array") {

    public LispObject execute(LispObject arg) {
      return ARRAY_DIMENSIONS_execute(arg);
    }
  };

  static final public LispObject ARRAY_DIMENSIONS_execute(LispObject arg) {
    return checkArray(arg).getDimensions();
  }

  // ### array-dimension array axis-number => dimension
  private static final Primitive ARRAY_DIMENSION = new Primitive(Symbol.ARRAY_DIMENSION, "array axis-number") {

    public LispObject execute(LispObject first, LispObject second) {
      return ARRAY_DIMENSION_execute(first, second);
    }
  };

  static final public LispObject ARRAY_DIMENSION_execute(LispObject first, LispObject second) {
    final AbstractArray array = checkArray(first);
    return Fixnum.getInstance(array.getDimension(Fixnum.getValue(second)));
  }

  // ### array-total-size array => size
  private static final Primitive ARRAY_TOTAL_SIZE = new Primitive(Symbol.ARRAY_TOTAL_SIZE, "array") {

    public LispObject execute(LispObject arg) {
      return ARRAY_TOTAL_SIZE_execute(arg);
    }
  };

  static final public LispObject ARRAY_TOTAL_SIZE_execute(LispObject arg) {
    return Fixnum.getInstance(checkArray(arg).getTotalSize());
  }

  // ### array-element-type
  // array-element-type array => typespec
  private static final Primitive ARRAY_ELEMENT_TYPE = new Primitive(Symbol.ARRAY_ELEMENT_TYPE, "array") {

    public LispObject execute(LispObject arg) {
      return ARRAY_ELEMENT_TYPE_execute(arg);
    }
  };

  static final public LispObject ARRAY_ELEMENT_TYPE_execute(LispObject arg) {
    return checkArray(arg).getElementType();
  }

  // ### adjustable-array-p
  private static final Primitive ADJUSTABLE_ARRAY_P = new Primitive(Symbol.ADJUSTABLE_ARRAY_P, "array") {

    public LispObject execute(LispObject arg) {
      return ADJUSTABLE_ARRAY_P_execute(arg);
    }
  };

  static final public LispObject ADJUSTABLE_ARRAY_P_execute(LispObject arg) {
    return checkArray(arg).isAdjustable() ? T : NIL;
  }

  // ### array-displacement array => displaced-to, displaced-index-offset
  private static final Primitive ARRAY_DISPLACEMENT = new Primitive(Symbol.ARRAY_DISPLACEMENT, "array") {

    public LispObject execute(LispObject arg) {
      return ARRAY_DISPLACEMENT_execute(arg);
    }
  };

  static final public LispObject ARRAY_DISPLACEMENT_execute(LispObject arg) {
    return checkArray(arg).arrayDisplacement();
  }

  // ### array-in-bounds-p array &rest subscripts => generalized-boolean
  private static final Primitive ARRAY_IN_BOUNDS_P = new Primitive(Symbol.ARRAY_IN_BOUNDS_P, "array &rest subscripts") {

    public LispObject execute(LispObject[] args) {
      return ARRAY_IN_BOUNDS_P_execute(args);
    }
  };

  static final public LispObject ARRAY_IN_BOUNDS_P_execute(LispObject[] args) throws InlineThrows {
    if (args.length < 1) {
      return wrongNumberOfArgumetnts();
    }
    final AbstractArray array;
    LispObject r = args[0];
    array = checkArray(r);
    int rank = array.getRank();
    if (rank != args.length - 1) {
      FastStringBuffer sb = new FastStringBuffer("ARRAY-IN-BOUNDS-P: ");
      sb.append("wrong number of subscripts (");
      sb.append(args.length - 1);
      sb.append(") for array of rank ");
      sb.append(rank);
      error(new ProgramError(sb.toString()));
    }
    for (int i = 0; i < rank; i++) {
      LispObject arg = args[i + 1];
      if (arg instanceof Fixnum) {
        int subscript = ((Fixnum) arg).value;
        if (subscript < 0 || subscript >= array.getDimension(i)) {
          return NIL;
        }
      } else if (arg instanceof Bignum) {
        return NIL;
      } else {
        type_error(arg, Symbol.INTEGER);
      }
    }
    return T;
  }

  // ### %array-row-major-index array subscripts => index
  private static final Primitive _ARRAY_ROW_MAJOR_INDEX = new Primitive("%array-row-major-index", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second) {
      return _ARRAY_ROW_MAJOR_INDEX_execute(first, second);
    }
  };

  static final public LispObject _ARRAY_ROW_MAJOR_INDEX_execute(LispObject first, LispObject second) {
    final AbstractArray array;
    array = checkArray(first);
    LispObject[] subscripts = second.copyToArray();
    return number(array.getRowMajorIndex(subscripts));
  }

  // ### aref array &rest subscripts => element
  private static final Primitive AREF = new Primitive(Symbol.AREF, "array &rest subscripts") {

    public LispObject execute() {
      return AREF_execute();
    }

    public LispObject execute(LispObject arg) {
      return AREF_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return AREF_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return AREF_execute(first, second, third);
    }

    public LispObject execute(LispObject[] args) {
      return AREF_execute(args);
    }
  };

  static final public LispObject AREF_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject AREF_execute(LispObject arg) {
    final AbstractArray array;
    array = checkArray(arg);
    if (array.getRank() == 0) {
      return array.AREF(0);
    }
    FastStringBuffer sb = new FastStringBuffer("Wrong number of subscripts (0) for array of rank ");
    sb.append(array.getRank());
    sb.append('.');
    return error(new ProgramError(sb.toString()));
  }

  static final public LispObject AREF_execute(LispObject first, LispObject second) {
    return first.AREF(second);
  }

  static final public LispObject AREF_execute(LispObject first, LispObject second, LispObject third) {
    return checkArray(first).get(new int[] { Fixnum.getValue(second), Fixnum.getValue(third) });
  }

  static final public LispObject AREF_execute(LispObject[] args) {
    final AbstractArray array = checkArray(args[0]);
    final int[] subs = new int[args.length - 1];
    for (int i = subs.length; i-- > 0;) {
      subs[i] = Fixnum.getValue(args[i + 1]);
    }
    return array.get(subs);
  }

  // ### aset array subscripts new-element => new-element
  private static final Primitive ASET = new Primitive("aset", PACKAGE_SYS, true, "array subscripts new-element") {

    public LispObject execute(LispObject first, LispObject second) {
      return ASET_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return ASET_execute(first, second, third);
    }

    public LispObject execute(LispObject[] args) {
      return ASET_execute(args);
    }
  };

  static final public LispObject ASET_execute(LispObject first, LispObject second) {
    // Rank zero array.
    final ZeroRankArray array;
    if (first instanceof ZeroRankArray) {
      array = (ZeroRankArray) first;
    } else {
      return error(new TypeError("The value " + first.writeToString() + " is not an array of rank 0."));
    }
    array.aset(0, second);
    return second;
  }

  static final public LispObject ASET_execute(LispObject first, LispObject second, LispObject third) {
    first.aset(second, third);
    return third;
  }

  static final public LispObject ASET_execute(LispObject[] args) {
    final AbstractArray array = checkArray(args[0]);
    final int nsubs = args.length - 2;
    final int[] subs = new int[nsubs];
    for (int i = nsubs; i-- > 0;) {
      subs[i] = Fixnum.getValue(args[i + 1]);
    }
    final LispObject newValue = args[args.length - 1];
    array.set(subs, newValue);
    return newValue;
  }

  // ### row-major-aref array index => element
  private static final Primitive ROW_MAJOR_AREF = new Primitive(Symbol.ROW_MAJOR_AREF, "array index") {

    public LispObject execute(LispObject first, LispObject second) {
      return ROW_MAJOR_AREF_execute(first, second);
    }
  };

  static final public LispObject ROW_MAJOR_AREF_execute(LispObject first, LispObject second) {
    return checkArray(first).AREF(Fixnum.getValue(second));
  }

  // ### vector
  private static final Primitive VECTOR = new Primitive(Symbol.VECTOR, "&rest objects") {

    public LispObject execute(LispObject[] args) {
      return VECTOR_execute(args);
    }
  };

  static final public LispObject VECTOR_execute(LispObject[] args) {
    return new SimpleVector(args);
  }

  // ### fill-pointer
  private static final Primitive FILL_POINTER = new Primitive(Symbol.FILL_POINTER, "vector") {

    public LispObject execute(LispObject arg) {
      return FILL_POINTER_execute(arg);
    }
  };

  static final public LispObject FILL_POINTER_execute(LispObject arg) {
    if (arg instanceof AbstractArray) {
      AbstractArray aa = (AbstractArray) arg;
      if (aa.hasFillPointer()) {
        return Fixnum.getInstance(aa.getFillPointer());
      }
    }
    return type_error(arg, list(Symbol.AND, Symbol.VECTOR, list(Symbol.SATISFIES, Symbol.ARRAY_HAS_FILL_POINTER_P)));
  }

  // ### %set-fill-pointer vector new-fill-pointer
  private static final Primitive _SET_FILL_POINTER = new Primitive("%set-fill-pointer", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_FILL_POINTER_execute(first, second);
    }
  };

  static final public LispObject _SET_FILL_POINTER_execute(LispObject first, LispObject second) {

    if (first instanceof AbstractVector) {
      AbstractVector v = (AbstractVector) first;
      if (v.hasFillPointer()) {
        v.setFillPointer(second);
      } else {
        v.noFillPointer();
      }
      return second;
    }

    return type_error(first, list(Symbol.AND, Symbol.VECTOR, list(Symbol.SATISFIES, Symbol.ARRAY_HAS_FILL_POINTER_P)));
  }

  // ### vector-push new-element vector => index-of-new-element
  private static final Primitive VECTOR_PUSH = new Primitive(Symbol.VECTOR_PUSH, "new-element vector") {

    public LispObject execute(LispObject first, LispObject second) {
      return VECTOR_PUSH_execute(first, second);
    }
  };

  static final public LispObject VECTOR_PUSH_execute(LispObject first, LispObject second) {
    final AbstractVector v = checkVector(second);
    int fillPointer = v.getFillPointer();
    if (fillPointer < 0) {
      v.noFillPointer();
    }
    if (fillPointer >= v.capacity()) {
      return NIL;
    }
    v.aset(fillPointer, first);
    v.setFillPointer(fillPointer + 1);
    return Fixnum.getInstance(fillPointer);
  }

  // ### vector-push-extend new-element vector &optional extension
  // => index-of-new-element
  private static final Primitive VECTOR_PUSH_EXTEND = new Primitive(Symbol.VECTOR_PUSH_EXTEND,
      "new-element vector &optional extension") {

    public LispObject execute(LispObject first, LispObject second) {
      return VECTOR_PUSH_EXTEND_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return VECTOR_PUSH_EXTEND_execute(first, second, third);
    }
  };

  static final public LispObject VECTOR_PUSH_EXTEND_execute(LispObject first, LispObject second) {
    return second.VECTOR_PUSH_EXTEND(first);
  }

  static final public LispObject VECTOR_PUSH_EXTEND_execute(LispObject first, LispObject second, LispObject third) {
    return second.VECTOR_PUSH_EXTEND(first, third);
  }

  // ### vector-pop vector => element
  private static final Primitive VECTOR_POP = new Primitive(Symbol.VECTOR_POP, "vector") {

    public LispObject execute(LispObject arg) {
      return VECTOR_POP_execute(arg);
    }
  };

  static final public LispObject VECTOR_POP_execute(LispObject arg) {
    final AbstractVector v = checkVector(arg);
    int fillPointer = v.getFillPointer();
    if (fillPointer < 0) {
      v.noFillPointer();
    }
    if (fillPointer == 0) {
      error(new LispError("nothing left to pop"));
    }
    int newFillPointer = v.checkIndex(fillPointer - 1);
    LispObject element = v.AREF(newFillPointer);
    v.setFillPointer(newFillPointer);
    return element;
  }

  // ### type-of
  private static final Primitive TYPE_OF = new Primitive(Symbol.TYPE_OF, "object") {

    public LispObject execute(LispObject arg) {
      return TYPE_OF_execute(arg);
    }
  };

  static final public LispObject TYPE_OF_execute(LispObject arg) {
    return arg.typeOf();
  }

  // ### class-of
  private static final Primitive CLASS_OF = new Primitive(Symbol.CLASS_OF, "object") {

    public LispObject execute(LispObject arg) {
      return CLASS_OF_execute(arg);
    }
  };

  @NoInline
  static final public LispObject CLASS_OF_execute(LispObject arg) {
    return arg.classOf();
  }

  // ### simple-typep
  private static final Primitive SIMPLE_TYPEP = new Primitive("simple-typep", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return SIMPLE_TYPEP_execute(first, second);
    }
  };

  static final public LispObject SIMPLE_TYPEP_execute(LispObject first, LispObject second) {
    return first.typep(second);
  }

  // ### function-lambda-expression function =>
  // lambda-expression, closure-p, name
  private static final Primitive FUNCTION_LAMBDA_EXPRESSION = new Primitive(Symbol.FUNCTION_LAMBDA_EXPRESSION,
      "function") {

    public LispObject execute(LispObject arg) {
      return FUNCTION_LAMBDA_EXPRESSION_execute(arg);
    }
  };

  static final public LispObject FUNCTION_LAMBDA_EXPRESSION_execute(LispObject arg) {
    final LispObject value1;
    final LispObject value2;
    final LispObject value3;
    if (arg instanceof CompiledClosure) {
      value1 = NIL;
      value2 = T;
      LispObject name = ((CompiledClosure) arg).getLambdaName();
      value3 = name != null ? name : NIL;
    } else if (arg instanceof Closure) {
      Closure closure = (Closure) arg;
      LispObject expr = closure.getBody();
      expr = new Cons(closure.getLambdaList(), expr);
      expr = new Cons(Symbol.LAMBDA, expr);
      value1 = expr;
      Environment env = closure.getEnvironment();
      if (env == null || env.isEmpty()) {
        value2 = NIL;
      } else {
        value2 = env;
      }
      // Return environment as closure-p.
      LispObject name = ((Closure) arg).getLambdaName();
      value3 = name != null ? name : NIL;
    } else if (arg instanceof Function) {
      value1 = NIL;
      value2 = T;
      value3 = ((Function) arg).getLambdaName();
    } else if (arg instanceof StandardGenericFunction) {
      value1 = NIL;
      value2 = T;
      value3 = ((StandardGenericFunction) arg).getGenericFunctionName();
    } else {
      return type_error(arg, Symbol.FUNCTION);
    }
    return LispThread.currentThread().setValues(value1, value2, value3);
  }

  // ### mapcar
  private static final Primitive MAPCAR = new Primitive(Symbol.MAPCAR, "function &rest lists") {

    public LispObject execute(LispObject fun, LispObject list) {
      return MAPCAR_execute(fun, list);
    }

    public LispObject execute(LispObject fun, LispObject list1, LispObject list2) {
      return MAPCAR_execute(fun, list1, list2);
    }

    public LispObject execute(final LispObject[] args) {
      return MAPCAR_execute(args);
    }
  };

  static final public LispObject MAPCAR_execute(LispObject fun, LispObject list) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = NIL;
    Cons splice = null;
    while (list != NIL) {
      Cons cons;
      if (list instanceof Cons) {
        cons = (Cons) list;
      } else {
        return type_error(list, Symbol.LIST);
      }
      LispObject obj = thread.execute(fun, cons.car);
      if (splice == null) {
        splice = new Cons(obj, result);
        result = splice;
      } else {
        Cons c = new Cons(obj);
        splice.cdr = c;
        splice = c;
      }
      list = cons.cdr;
    }
    thread._values = null;
    return result;
  }

  static final public LispObject MAPCAR_execute(LispObject fun, LispObject list1, LispObject list2) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = NIL;
    Cons splice = null;
    while (list1 != NIL && list2 != NIL) {
      LispObject obj = thread.execute(fun, list1.car(), list2.car());
      if (splice == null) {
        splice = new Cons(obj, result);
        result = splice;
      } else {
        Cons cons = new Cons(obj);
        splice.cdr = cons;
        splice = cons;
      }
      list1 = list1.cdr();
      list2 = list2.cdr();
    }
    thread._values = null;
    return result;
  }

  static final public LispObject MAPCAR_execute(final LispObject[] args) throws InlineThrows {
    final int numArgs = args.length;
    if (numArgs < 2) {
      return wrongNumberOfArgumetnts();
    }
    int commonLength = -1;
    for (int i = 1; i < numArgs; i++) {
      if (!args[i].listp()) {
        type_error(args[i], Symbol.LIST);
      }
      int len = args[i].length();
      if (commonLength < 0) {
        commonLength = len;
      } else if (commonLength > len) {
        commonLength = len;
      }
    }
    final LispThread thread = LispThread.currentThread();
    LispObject[] results = new LispObject[commonLength];
    final int numFunArgs = numArgs - 1;
    final LispObject[] funArgs = new LispObject[numFunArgs];
    for (int i = 0; i < commonLength; i++) {
      for (int j = 0; j < numFunArgs; j++) {
        funArgs[j] = args[j + 1].car();
      }
      results[i] = funcall(args[0], funArgs, thread);
      for (int j = 1; j < numArgs; j++) {
        args[j] = args[j].cdr();
      }
    }
    thread._values = null;
    LispObject result = NIL;
    for (int i = commonLength; i-- > 0;) {
      result = new Cons(results[i], result);
    }
    return result;
  }

  // ### mapc
  private static final Primitive MAPC = new Primitive(Symbol.MAPC, "function &rest lists") {

    public LispObject execute(LispObject fun, LispObject list) {
      return MAPC_execute(fun, list);
    }

    public LispObject execute(LispObject fun, LispObject list1, LispObject list2) {
      return MAPC_execute(fun, list1, list2);
    }

    public LispObject execute(final LispObject[] args) {
      return MAPC_execute(args);
    }
  };

  static final public LispObject MAPC_execute(LispObject fun, LispObject list) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = list;
    while (list != NIL) {
      Cons cons;
      if (list instanceof Cons) {
        cons = (Cons) list;
      } else {
        return type_error(list, Symbol.LIST);
      }
      thread.execute(fun, cons.car);
      list = cons.cdr;
    }
    thread._values = null;
    return result;
  }

  static final public LispObject MAPC_execute(LispObject fun, LispObject list1, LispObject list2) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = list1;
    while (list1 != NIL && list2 != NIL) {
      thread.execute(fun, list1.car(), list2.car());
      list1 = ((Cons) list1).cdr;
      list2 = ((Cons) list2).cdr;
    }
    thread._values = null;
    return result;
  }

  static final public LispObject MAPC_execute(final LispObject[] args) throws InlineThrows {
    final int numArgs = args.length;
    if (numArgs < 2) {
      return wrongNumberOfArgumetnts();
    }
    int commonLength = -1;
    for (int i = 1; i < numArgs; i++) {
      if (!args[i].listp()) {
        type_error(args[i], Symbol.LIST);
      }
      int len = args[i].length();
      if (commonLength < 0) {
        commonLength = len;
      } else if (commonLength > len) {
        commonLength = len;
      }
    }
    final LispThread thread = LispThread.currentThread();
    LispObject result = args[1];
    final int numFunArgs = numArgs - 1;
    final LispObject[] funArgs = new LispObject[numFunArgs];
    for (int i = 0; i < commonLength; i++) {
      for (int j = 0; j < numFunArgs; j++) {
        funArgs[j] = args[j + 1].car();
      }
      funcall(args[0], funArgs, thread);
      for (int j = 1; j < numArgs; j++) {
        args[j] = args[j].cdr();
      }
    }
    thread._values = null;
    return result;
  }

  // ### macroexpand
  private static final Primitive MACROEXPAND = new Primitive(Symbol.MACROEXPAND, "form &optional env") {

    public LispObject execute(LispObject form) {
      return MACROEXPAND_execute(form);
    }

    public LispObject execute(LispObject form, LispObject env) {
      return MACROEXPAND_execute(form, env);
    }
  };

  static final public LispObject MACROEXPAND_execute(LispObject form) {
    return macroexpand(form, new Environment(), LispThread.currentThread());
  }

  static final public LispObject MACROEXPAND_execute(LispObject form, LispObject env) {
    return macroexpand(form, env != NIL ? checkEnvironment(env) : new Environment(), LispThread.currentThread());
  }

  // ### macroexpand-1
  private static final Primitive MACROEXPAND_1 = new Primitive(Symbol.MACROEXPAND_1, "form &optional env") {

    public LispObject execute(LispObject form) {
      return MACROEXPAND_1_execute(form);
    }

    public LispObject execute(LispObject form, LispObject env) {
      return MACROEXPAND_1_execute(form, env);
    }
  };

  static final public LispObject MACROEXPAND_1_execute(LispObject form) {
    return macroexpand_1(form, new Environment(), LispThread.currentThread());
  }

  static final public LispObject MACROEXPAND_1_execute(LispObject form, LispObject env) {
    return macroexpand_1(form, env != NIL ? checkEnvironment(env) : new Environment(), LispThread.currentThread());
  }

  // ### string
  private static final Primitive STRING = new Primitive(Symbol.STRING, "x") {

    public LispObject execute(LispObject arg) {
      return STRING_execute(arg);
    }
  };

  static final public LispObject STRING_execute(LispObject arg) {
    return arg.STRING();
  }

  // ### gensym
  private static final Primitive GENSYM = new Primitive(Symbol.GENSYM, "&optional x") {

    public LispObject execute() {
      return GENSYM_execute();
    }

    public LispObject execute(LispObject arg) {
      return GENSYM_execute(arg);
    }
  };

  static final public LispObject GENSYM_execute() {
    return gensym("G", LispThread.currentThread());
  }

  static final public LispObject GENSYM_execute(LispObject arg) {
    if (arg instanceof Fixnum) {
      int n = ((Fixnum) arg).value;
      if (n >= 0) {
        FastStringBuffer sb = new FastStringBuffer('G');
        sb.append(n);
        // Decimal representation.
        return new Symbol(new SimpleString(sb));
      }
    } else if (arg instanceof Bignum) {
      BigInteger n = ((Bignum) arg).value;
      if (n.signum() >= 0) {
        FastStringBuffer sb = new FastStringBuffer('G');
        sb.append(n.toString());
        // Decimal representation.
        return new Symbol(new SimpleString(sb));
      }
    } else if (arg instanceof AbstractString) {
      return gensym(arg.getStringValue(), LispThread.currentThread());
    }
    return type_error(arg, list(Symbol.OR, Symbol.STRING, Symbol.UNSIGNED_BYTE));
  }

  // ### intern string &optional package => symbol, status
  // STATUS is one of :INHERITED, :EXTERNAL, :INTERNAL or NIL.
  // "It is implementation-dependent whether the string that becomes the new
  // symbol's name is the given string or a copy of it."
  private static final Primitive INTERN = new Primitive(Symbol.INTERN, "string &optional package") {

    public LispObject execute(LispObject arg) {
      return INTERN_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return INTERN_execute(first, second);
    }
  };

  static final public LispObject INTERN_execute(LispObject arg) {
    final SimpleString s;
    if (arg instanceof SimpleString) {
      s = (SimpleString) arg;
    } else {
      s = new SimpleString(arg.getStringValue());
    }
    final LispThread thread = LispThread.currentThread();
    Package pkg = (Package) Symbol._PACKAGE_.symbolValue(thread);
    return pkg.intern(s, thread);
  }

  static final public LispObject INTERN_execute(LispObject first, LispObject second) {
    final SimpleString s;
    if (first instanceof SimpleString) {
      s = (SimpleString) first;
    } else {
      s = new SimpleString(first.getStringValue());
    }
    Package pkg = coerceToPackage(second);
    return pkg.intern(s, LispThread.currentThread());
  }

  // ### unintern
  // unintern symbol &optional package => generalized-boolean
  private static final Primitive UNINTERN = new Primitive(Symbol.UNINTERN, "symbol &optional package") {

    public LispObject execute(LispObject[] args) {
      return UNINTERN_execute(args);
    }
  };

  static final public LispObject UNINTERN_execute(LispObject[] args) throws InlineThrows {
    if (args.length == 0 || args.length > 2) {
      return wrongNumberOfArgumetnts();
    }
    Symbol symbol = checkSymbol(args[0]);
    Package pkg;
    if (args.length == 2) {
      pkg = coerceToPackage(args[1]);
    } else {
      pkg = getCurrentPackage();
    }
    return pkg.unintern(symbol);
  }

  // ### find-package
  private static final Primitive FIND_PACKAGE = new Primitive(Symbol.FIND_PACKAGE, "name") {

    public LispObject execute(LispObject arg) {
      return FIND_PACKAGE_execute(arg);
    }
  };

  static final public LispObject FIND_PACKAGE_execute(LispObject arg) {
    if (arg instanceof Package) {
      return arg;
    }
    if (arg instanceof AbstractString) {
      Package pkg = Packages.findPackage(arg.getStringValue());
      return pkg != null ? pkg : NIL;
    }
    if (arg instanceof Symbol) {
      Package pkg = Packages.findPackage(checkSymbol(arg).getName());
      return pkg != null ? pkg : NIL;
    }
    if (arg instanceof LispCharacter) {
      String packageName = String.valueOf(new char[] { ((LispCharacter) arg).getValue() });
      Package pkg = Packages.findPackage(packageName);
      return pkg != null ? pkg : NIL;
    }
    return NIL;
  }

  // ### %make-package
  // %make-package package-name nicknames use => package
  private static final Primitive _MAKE_PACKAGE = new Primitive("%make-package", PACKAGE_SYS, false) {
    /**
     * This invocation is solely used to be able to create a package to bind to
     * *FASL-ANONYMOUS-PACKAGE*
     * 
     * @return Description of the Returned Value
     */

    public LispObject execute() {
      return _MAKE_PACKAGE_execute();
    }

    /**
     * This invocation is used by MAKE-PACKAGE to create a package
     * 
     * @param first
     *          Description of Parameter
     * @param second
     *          Description of Parameter
     * @param third
     *          Description of Parameter
     * @return Description of the Returned Value
     */

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return _MAKE_PACKAGE_execute(first, second, third);
    }
  };

  /**
   * This invocation is solely used to be able to create a package to bind to
   * *FASL-ANONYMOUS-PACKAGE*
   * 
   * @return Description of the Returned Value
   */
  static final public LispObject _MAKE_PACKAGE_execute() {
    return new Package();
  }

  /**
   * This invocation is used by MAKE-PACKAGE to create a package
   * 
   * @param first
   *          Description of Parameter
   * @param second
   *          Description of Parameter
   * @param third
   *          Description of Parameter
   * 
   * @return Description of the Returned Value
   */
  static final public LispObject _MAKE_PACKAGE_execute(LispObject first, LispObject second, LispObject third) {
    String packageName = javaString(first);
    Package pkg = Packages.findPackage(packageName);
    if (pkg != null) {
      error(new LispError("Package " + packageName + " already exists."));
    }
    LispObject nicknames = checkList(second);
    if (nicknames != NIL) {
      LispObject list = nicknames;
      while (list != NIL) {
        String nick = javaString(list.car());
        if (Packages.findPackage(nick) != null) {
          error(new PackageError("A package named " + nick + " already exists."));
        }
        list = list.cdr();
      }
    }
    LispObject use = checkList(third);
    if (use != NIL) {
      LispObject list = use;
      while (list != NIL) {
        LispObject obj = list.car();
        if (obj instanceof Package) {
          // OK.
        } else {
          String s = javaString(obj);
          Package p = Packages.findPackage(s);
          if (p == null) {
            error(new LispError(obj.writeToString() + " is not the name of a package."));
            return NIL;
          }
        }
        list = list.cdr();
      }
    }
    // Now create the package.
    pkg = Packages.createPackage(packageName);
    // Add the nicknames.
    while (nicknames != NIL) {
      String nick = javaString(nicknames.car());
      pkg.addNickname(nick);
      nicknames = nicknames.cdr();
    }
    // Create the use list.
    while (use != NIL) {
      LispObject obj = use.car();
      if (obj instanceof Package) {
        pkg.usePackage((Package) obj);
      } else {
        String s = javaString(obj);
        Package p = Packages.findPackage(s);
        if (p == null) {
          error(new LispError(obj.writeToString() + " is not the name of a package."));
          return NIL;
        }
        pkg.usePackage(p);
      }
      use = use.cdr();
    }
    return pkg;
  }

  // ### %in-package
  private static final Primitive _IN_PACKAGE = new Primitive("%in-package", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return _IN_PACKAGE_execute(arg);
    }
  };

  static final public LispObject _IN_PACKAGE_execute(LispObject arg) {
    final String packageName = javaString(arg);
    final Package pkg = Packages.findPackage(packageName);
    if (pkg == null) {
      return error(new PackageError("The name " + packageName + " does not designate any package."));
    }
    SpecialBinding binding = LispThread.currentThread().getSpecialBinding(Symbol._PACKAGE_);
    if (binding != null) {
      binding.value = pkg;
    } else {
      // No dynamic binding.
      Symbol._PACKAGE_.setSymbolValue(pkg);
    }
    return pkg;
  }

  // ### use-package packages-to-use &optional package => t
  private static final Primitive USE_PACKAGE = new Primitive(Symbol.USE_PACKAGE, "packages-to-use &optional package") {

    public LispObject execute(LispObject[] args) {
      return USE_PACKAGE_execute(args);
    }
  };

  static final public LispObject USE_PACKAGE_execute(LispObject[] args) throws InlineThrows {
    if (args.length < 1 || args.length > 2) {
      return wrongNumberOfArgumetnts();
    }
    Package pkg;
    if (args.length == 2) {
      pkg = coerceToPackage(args[1]);
    } else {
      pkg = getCurrentPackage();
    }
    if (args[0].listp()) {
      LispObject list = args[0];
      while (list != NIL) {
        pkg.usePackage(coerceToPackage(list.car()));
        list = list.cdr();
      }
    } else {
      pkg.usePackage(coerceToPackage(args[0]));
    }
    return T;
  }

  // ### package-symbols
  private static final Primitive PACKAGE_SYMBOLS = new Primitive("package-symbols", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return PACKAGE_SYMBOLS_execute(arg);
    }
  };

  static final public LispObject PACKAGE_SYMBOLS_execute(LispObject arg) {
    return coerceToPackage(arg).getSymbols();
  }

  // ### package-internal-symbols
  private static final Primitive PACKAGE_INTERNAL_SYMBOLS = new Primitive("package-internal-symbols", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return PACKAGE_INTERNAL_SYMBOLS_execute(arg);
    }
  };

  static final public LispObject PACKAGE_INTERNAL_SYMBOLS_execute(LispObject arg) {
    return coerceToPackage(arg).PACKAGE_INTERNAL_SYMBOLS();
  }

  // ### package-external-symbols
  private static final Primitive PACKAGE_EXTERNAL_SYMBOLS = new Primitive("package-external-symbols", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return PACKAGE_EXTERNAL_SYMBOLS_execute(arg);
    }
  };

  static final public LispObject PACKAGE_EXTERNAL_SYMBOLS_execute(LispObject arg) {
    return coerceToPackage(arg).PACKAGE_EXTERNAL_SYMBOLS();
  }

  // ### package-inherited-symbols
  private static final Primitive PACKAGE_INHERITED_SYMBOLS = new Primitive("package-inherited-symbols", PACKAGE_SYS,
      true) {

    public LispObject execute(LispObject arg) {
      return PACKAGE_INHERITED_SYMBOLS_execute(arg);
    }
  };

  static final public LispObject PACKAGE_INHERITED_SYMBOLS_execute(LispObject arg) {
    return coerceToPackage(arg).PACKAGE_INHERITED_SYMBOLS();
  }

  // ### export symbols &optional package
  private static final Primitive EXPORT = new Primitive(Symbol.EXPORT, "symbols &optional package") {

    public LispObject execute(LispObject arg) {
      return EXPORT_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return EXPORT_execute(first, second);
    }
  };

  static final public LispObject EXPORT_execute(LispObject arg) {
    final Package pkg = (Package) Symbol._PACKAGE_.symbolValue();
    if (arg instanceof Cons) {
      for (LispObject list = arg; list != NIL; list = list.cdr()) {
        pkg.export(checkSymbol(list.car()));
      }
    } else {
      pkg.export(checkSymbol(arg));
    }
    return T;
  }

  static final public LispObject EXPORT_execute(LispObject first, LispObject second) {
    if (first instanceof Cons) {
      Package pkg = coerceToPackage(second);
      for (LispObject list = first; list != NIL; list = list.cdr()) {
        pkg.export(checkSymbol(list.car()));
      }
    } else {
      coerceToPackage(second).export(checkSymbol(first));
    }
    return T;
  }

  // ### find-symbol string &optional package => symbol, status
  private static final Primitive FIND_SYMBOL = new Primitive(Symbol.FIND_SYMBOL, "string &optional package") {

    public LispObject execute(LispObject arg) {
      return FIND_SYMBOL_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return FIND_SYMBOL_execute(first, second);
    }
  };

  static final public LispObject FIND_SYMBOL_execute(LispObject arg) {
    return getCurrentPackage().findSymbol(arg.getStringValue());
  }

  static final public LispObject FIND_SYMBOL_execute(LispObject first, LispObject second) {
    return coerceToPackage(second).findSymbol(first.getStringValue());
  }

  // ### fset name function &optional source-position arglist documentation
  // => function
  private static final Primitive FSET = new Primitive("fset", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return FSET_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return FSET_execute(first, second, third);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
      return FSET_execute(first, second, third, fourth);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth, LispObject fifth) {
      return FSET_execute(first, second, third, fourth, fifth);
    }
  };

  static final public LispObject FSET_execute(LispObject first, LispObject second) {
    return FSET_execute(first, second, NIL, NIL, NIL);
  }

  static final public LispObject FSET_execute(LispObject first, LispObject second, LispObject third) {
    return FSET_execute(first, second, third, NIL, NIL);
  }

  static final public LispObject FSET_execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
    return FSET_execute(first, second, third, fourth, NIL);
  }

  static final public LispObject FSET_execute(LispObject first, LispObject second, LispObject third, LispObject fourth,
      LispObject fifth) {
    if (first instanceof Symbol) {
      checkRedefinition(first);
      Symbol symbol = checkSymbol(first);
      symbol.setSymbolFunction(second);
      final LispThread thread = LispThread.currentThread();
      LispObject sourcePathname = _SOURCE_.symbolValue(thread);
      LispObject sourcePosition = third;
      if (sourcePathname != NIL) {
        sourcePosition = _SOURCE_POSITION_.symbolValue(thread);
      }
      if (sourcePathname == NIL) {
        sourcePathname = Keyword.TOP_LEVEL;
      }
      if (sourcePathname != Keyword.TOP_LEVEL) {
        put(symbol, Symbol._SOURCE, new Cons(sourcePathname, third));
      } else {
        put(symbol, Symbol._SOURCE, sourcePathname);
      }
    } else if (isValidSetfFunctionName(first)) {
      // SETF function
      checkRedefinition(first);
      Symbol symbol = checkSymbol(first.cadr());
      put(symbol, Symbol.SETF_FUNCTION, second);
    } else {
      return type_error(first, FUNCTION_NAME);
    }
    if (second instanceof Operator) {
      Operator op = (Operator) second;
      op.setLambdaName(first);
      if (fourth != NIL) {
        op.setLambdaList(fourth);
      }
      if (fifth != NIL) {
        op.setDocumentation(Symbol.FUNCTION, fifth);
      }
    }
    return second;
  }

  // ### %set-symbol-plist
  private static final Primitive _SET_SYMBOL_PLIST = new Primitive("%set-symbol-plist", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_SYMBOL_PLIST_execute(first, second);
    }
  };

  static final public LispObject _SET_SYMBOL_PLIST_execute(LispObject first, LispObject second) {
    checkSymbol(first).setPropertyList(checkList(second));
    return second;
  }

  // ### getf plist indicator &optional default => value
  private static final Primitive GETF = new Primitive(Symbol.GETF, "plist indicator &optional default") {

    public LispObject execute(LispObject plist, LispObject indicator) {
      return GETF_execute(plist, indicator);
    }

    public LispObject execute(LispObject plist, LispObject indicator, LispObject defaultValue) {
      return GETF_execute(plist, indicator, defaultValue);
    }
  };

  static final public LispObject GETF_execute(LispObject plist, LispObject indicator) {
    return getf(plist, indicator, NIL);
  }

  static final public LispObject GETF_execute(LispObject plist, LispObject indicator, LispObject defaultValue) {
    return getf(plist, indicator, defaultValue);
  }

  // ### get symbol indicator &optional default => value
  private static final Primitive GET = new Primitive(Symbol.GET, "symbol indicator &optional default") {

    public LispObject execute(LispObject symbol, LispObject indicator) {
      return GET_execute(symbol, indicator);
    }

    public LispObject execute(LispObject symbol, LispObject indicator, LispObject defaultValue) {
      return GET_execute(symbol, indicator, defaultValue);
    }
  };

  static final public LispObject GET_execute(LispObject symbol, LispObject indicator) {
    return get(symbol, indicator, NIL);
  }

  static final public LispObject GET_execute(LispObject symbol, LispObject indicator, LispObject defaultValue) {
    return get(symbol, indicator, defaultValue);
  }

  // ### put symbol indicator value => value
  private static final Primitive PUT = new Primitive("put", PACKAGE_SYS, true) {

    public LispObject execute(LispObject symbol, LispObject indicator, LispObject value) {
      return PUT_execute(symbol, indicator, value);
    }

    public LispObject execute(LispObject symbol, LispObject indicator, LispObject defaultValue, LispObject value) {
      return PUT_execute(symbol, indicator, defaultValue, value);
    }
  };

  static final public LispObject PUT_execute(LispObject symbol, LispObject indicator, LispObject value) {
    return put(checkSymbol(symbol), indicator, value);
  }

  static final public LispObject PUT_execute(LispObject symbol, LispObject indicator, LispObject defaultValue,
      LispObject value) {
    return put(checkSymbol(symbol), indicator, value);
  }

  // ### macrolet
  private static final SpecialOperator MACROLET = new SpecialOperator(Symbol.MACROLET, "definitions &rest body") {

    public LispObject execute(LispObject args, Environment env) {
      return MACROLET_execute(args, env);
    }
  };

  static final public LispObject MACROLET_execute(LispObject args, Environment env) {
    LispObject defs = checkList(args.car());
    final LispThread thread = LispThread.currentThread();
    final SpecialBindingsMark mark = thread.markSpecialBindings();

    try {
      Environment ext = new Environment(env);
      while (defs != NIL) {
        LispObject def = checkList(defs.car());
        Symbol symbol = checkSymbol(def.car());
        Symbol make_expander_for_macrolet = PACKAGE_SYS.intern("MAKE-EXPANDER-FOR-MACROLET");
        LispObject expander = make_expander_for_macrolet.execute(def);
        Closure expansionFunction = new Closure(expander, env);
        MacroObject macroObject = new MacroObject(symbol, expansionFunction);
        ext.addFunctionBinding(symbol, macroObject);
        defs = defs.cdr();
      }
      return progn(ext.processDeclarations(args.cdr()), ext, thread);
    } finally {
      thread.resetSpecialBindings(mark);
    }
  }

  private static final Primitive MAKE_EXPANDER_FOR_MACROLET = new Primitive("make-expander-for-macrolet", PACKAGE_SYS,
      true, "definition") {

    public LispObject execute(LispObject definition) {
      return MAKE_EXPANDER_FOR_MACROLET_execute(definition);
    }
  };

  static final public LispObject MAKE_EXPANDER_FOR_MACROLET_execute(LispObject definition) {
    Symbol symbol = checkSymbol(definition.car());
    LispObject lambdaList = definition.cadr();
    LispObject body = definition.cddr();
    LispObject block = new Cons(Symbol.BLOCK, new Cons(symbol, body));
    LispObject toBeApplied = list(Symbol.LAMBDA, lambdaList, block);
    final LispThread thread = LispThread.currentThread();
    LispObject formArg = gensym("WHOLE-", thread);
    LispObject envArg = gensym("ENVIRONMENT-", thread);
    // Ignored.
    LispObject expander = list(Symbol.LAMBDA, list(formArg, envArg), list(Symbol.APPLY, toBeApplied, list(Symbol.CDR,
        formArg)));
    return expander;
  }

  // ### tagbody
  private static final SpecialOperator TAGBODY = new SpecialOperator(Symbol.TAGBODY, "&rest statements") {

    public LispObject execute(LispObject args, Environment env) {
      return TAGBODY_execute(args, env);
    }
  };

  static final public LispObject TAGBODY_execute(LispObject args, Environment env) {
    Environment ext = new Environment(env);
    try {
      return processTagBody(args, preprocessTagBody(args, ext), ext);
    } finally {
      ext.inactive = true;
    }
  }

  // ### go
  private static final SpecialOperator GO = new SpecialOperator(Symbol.GO, "tag") {

    public LispObject execute(LispObject args, Environment env) {
      return GO_execute(args, env);
    }
  };

  static final public LispObject GO_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() != 1) {
      return wrongNumberOfArgumetnts();
    }
    Binding binding = env.getTagBinding(args.car());
    if (binding == null) {
      return error(new ControlError("No tag named " + args.car().writeToString() + " is currently visible."));
    }

    return nonLocalGo(binding, args.car());
  }

  // ### block
  private static final SpecialOperator BLOCK = new SpecialOperator(Symbol.BLOCK, "name &rest forms") {

    public LispObject execute(LispObject args, Environment env) {
      return BLOCK_execute(args, env);
    }
  };

  static final public LispObject BLOCK_execute(LispObject args, Environment env) throws InlineThrows {
    if (args == NIL) {
      return wrongNumberOfArgumetnts();
    }
    LispObject tag;
    tag = checkSymbol(args.car());
    LispObject body = ((Cons) args).cdr();
    Environment ext = new Environment(env);
    final LispObject block = new LispObject();
    ext.addBlock(tag, block);
    LispObject result = NIL;
    final LispThread thread = LispThread.currentThread();
    try {
      return progn(body, ext, thread);
    } catch (Return ret) {
      if (ret.getBlock() == block) {
        return ret.getResult();
      }
      throw ret;
    } finally {
      ext.inactive = true;
    }
  }

  // ### return-from
  private static final SpecialOperator RETURN_FROM = new SpecialOperator(Symbol.RETURN_FROM, "name &optional value") {

    public LispObject execute(LispObject args, Environment env) {
      return RETURN_FROM_execute(args, env);
    }
  };

  static final public LispObject RETURN_FROM_execute(LispObject args, Environment env) throws InlineThrows {
    final int length = args.length();
    if (length < 1 || length > 2) {
      return wrongNumberOfArgumetnts();
    }
    Symbol symbol;
    symbol = checkSymbol(args.car());

    return nonLocalReturn(env.getBlockBinding(symbol), symbol, (length == 2) ? eval(args.cadr(), env, LispThread
        .currentThread()) : NIL);
  }

  // ### catch
  private static final SpecialOperator CATCH = new SpecialOperator(Symbol.CATCH, "tag &body body") {

    public LispObject execute(LispObject args, Environment env) {
      return CATCH_execute(args, env);
    }
  };

  static final public LispObject CATCH_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() < 1) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    LispObject tag = eval(args.car(), env, thread);
    thread.pushCatchTag(tag);
    LispObject body = args.cdr();
    LispObject result = NIL;
    try {
      return progn(body, env, thread);
    } catch (Throw t) {
      if (t.tag == tag) {
        return t.getResult(thread);
      }
      throw t;
    } catch (Return ret) {
      throw ret;
    } finally {
      thread.popCatchTag();
    }
  }

  // ### throw
  private static final SpecialOperator THROW = new SpecialOperator(Symbol.THROW, "tag result") {

    public LispObject execute(LispObject args, Environment env) {
      return THROW_execute(args, env);
    }
  };

  static final public LispObject THROW_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() != 2) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    thread.throwToTag(eval(args.car(), env, thread), eval(args.cadr(), env, thread));
    // Not reached.
    return NIL;
  }

  // ### unwind-protect
  private static final SpecialOperator UNWIND_PROTECT = new SpecialOperator(Symbol.UNWIND_PROTECT,
      "protected &body cleanup") {

    public LispObject execute(LispObject args, Environment env) {
      return UNWIND_PROTECT_execute(args, env);
    }
  };

  static final public LispObject UNWIND_PROTECT_execute(LispObject args, Environment env) {
    final LispThread thread = LispThread.currentThread();
    LispObject result;
    LispObject[] values;
    try {
      result = eval(args.car(), env, thread);
    } finally {
      values = thread._values;
      LispObject body = args.cdr();
      while (body != NIL) {
        eval(body.car(), env, thread);
        body = ((Cons) body).cdr;
      }
      thread._values = values;
    }
    if (values != null) {
      thread.setValues(values);
    } else {
      thread._values = null;
    }
    return result;
  }

  // ### eval-when
  private static final SpecialOperator EVAL_WHEN = new SpecialOperator(Symbol.EVAL_WHEN, "situations &rest forms") {

    public LispObject execute(LispObject args, Environment env) {
      return EVAL_WHEN_execute(args, env);
    }
  };

  static final public LispObject EVAL_WHEN_execute(LispObject args, Environment env) {
    LispObject situations = args.car();
    if (situations != NIL) {
      if (memq(Keyword.EXECUTE, situations) || memq(Symbol.EVAL, situations)) {
        return progn(args.cdr(), env, LispThread.currentThread());
      }
    }
    return NIL;
  }

  // ### multiple-value-bind
  // multiple-value-bind (var*) values-form declaration* form*
  // Should be a macro.
  private static final SpecialOperator MULTIPLE_VALUE_BIND = new SpecialOperator(Symbol.MULTIPLE_VALUE_BIND,
      "vars value-form &body body") {

    public LispObject execute(LispObject args, Environment env) {
      return MULTIPLE_VALUE_BIND_execute(args, env);
    }
  };

  static final public LispObject MULTIPLE_VALUE_BIND_execute(LispObject args, Environment env) {
    LispObject vars = args.car();
    args = args.cdr();
    LispObject valuesForm = args.car();
    LispObject body = args.cdr();
    final LispThread thread = LispThread.currentThread();
    LispObject value = eval(valuesForm, env, thread);
    LispObject[] values = thread._values;
    if (values == null) {
      // eval() did not return multiple values.
      values = new LispObject[1];
      values[0] = value;
    }
    // Process declarations.
    LispObject bodyAndDecls = parseBody(body, false);
    LispObject specials = parseSpecials(bodyAndDecls.NTH(1));
    body = bodyAndDecls.car();

    final SpecialBindingsMark mark = thread.markSpecialBindings();
    final Environment ext = new Environment(env);
    int i = 0;
    LispObject var = vars.car();
    while (var != NIL) {
      final Symbol sym;

      sym = checkSymbol(var);

      LispObject val = i < values.length ? values[i] : NIL;
      if (specials != NIL && memq(sym, specials)) {
        thread.bindSpecial(sym, val);
        ext.declareSpecial(sym);
      } else if (sym.isSpecialVariable()) {
        thread.bindSpecial(sym, val);
      } else {
        ext.bind(sym, val);
      }
      vars = vars.cdr();
      var = vars.car();
      ++i;
    }
    // Make sure free special declarations are visible in the body.
    // "The scope of free declarations specifically does not include
    // initialization forms for bindings established by the form
    // containing the declarations." (3.3.4)
    while (specials != NIL) {
      Symbol symbol = (Symbol) specials.car();
      ext.declareSpecial(symbol);
      specials = ((Cons) specials).cdr;
    }
    thread._values = null;
    LispObject result = NIL;
    try {
      result = progn(body, ext, thread);
    } finally {
      thread.resetSpecialBindings(mark);
    }
    return result;
  }

  // ### multiple-value-prog1
  private static final SpecialOperator MULTIPLE_VALUE_PROG1 = new SpecialOperator(Symbol.MULTIPLE_VALUE_PROG1,
      "values-form &rest forms") {

    public LispObject execute(LispObject args, Environment env) {
      return MULTIPLE_VALUE_PROG1_execute(args, env);
    }
  };

  static final public LispObject MULTIPLE_VALUE_PROG1_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() == 0) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    LispObject result = eval(args.car(), env, thread);
    LispObject[] values = thread._values;
    while ((args = args.cdr()) != NIL) {
      eval(args.car(), env, thread);
    }
    if (values != null) {
      thread.setValues(values);
    } else {
      thread._values = null;
    }
    return result;
  }

  // ### multiple-value-call
  private static final SpecialOperator MULTIPLE_VALUE_CALL = new SpecialOperator(Symbol.MULTIPLE_VALUE_CALL,
      "fun &rest args") {

    public LispObject execute(LispObject args, Environment env) {
      return MULTIPLE_VALUE_CALL_execute(args, env);
    }
  };

  @NoInline
  static final public LispObject MULTIPLE_VALUE_CALL_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() == 0) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    LispObject function;
    LispObject obj = eval(args.car(), env, thread);
    args = args.cdr();
    if (obj instanceof Symbol) {
      function = obj.getSymbolFunction();
      if (function == null) {
        error(new UndefinedFunction(obj));
      }
    } else if (obj instanceof Function) {
      function = obj;
    } else {
      error(new LispError(obj.writeToString() + " is not a function name."));
      return NIL;
    }
    ArrayList<LispObject> arrayList = new ArrayList<LispObject>();
    while (args != NIL) {
      LispObject form = args.car();
      LispObject result = eval(form, env, thread);
      LispObject[] values = thread._values;
      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          arrayList.add(values[i]);
        }
      } else {
        arrayList.add(result);
      }
      args = ((Cons) args).cdr;
    }
    LispObject[] argv = new LispObject[arrayList.size()];
    arrayList.toArray(argv);
    return funcall(function, argv, thread);
  }

  // ### and
  // Should be a macro.
  private static final SpecialOperator AND = new SpecialOperator(Symbol.AND, "&rest forms") {

    public LispObject execute(LispObject args, Environment env) {
      return AND_execute(args, env);
    }
  };

  static final public LispObject AND_execute(LispObject args, Environment env) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = T;
    while (args != NIL) {
      result = eval(args.car(), env, thread);
      if (result == NIL) {
        if (((Cons) args).cdr != NIL) {
          // Not the last form.
          thread._values = null;
        }
        break;
      }
      args = ((Cons) args).cdr;
    }
    return result;
  }

  // ### or
  // Should be a macro.
  private static final SpecialOperator OR = new SpecialOperator(Symbol.OR, "&rest forms") {

    public LispObject execute(LispObject args, Environment env) {
      return OR_execute(args, env);
    }
  };

  static final public LispObject OR_execute(LispObject args, Environment env) {
    final LispThread thread = LispThread.currentThread();
    LispObject result = NIL;
    while (args != NIL) {
      result = eval(args.car(), env, thread);
      if (result != NIL) {
        if (((Cons) args).cdr != NIL) {
          // Not the last form.
          thread._values = null;
        }
        break;
      }
      args = ((Cons) args).cdr;
    }
    return result;
  }

  // ### multiple-value-list form => list
  // Evaluates form and creates a list of the multiple values it returns.
  // Should be a macro.
  private static final SpecialOperator MULTIPLE_VALUE_LIST = new SpecialOperator(Symbol.MULTIPLE_VALUE_LIST,
      "value-form") {

    public LispObject execute(LispObject args, Environment env) {
      return MULTIPLE_VALUE_LIST_execute(args, env);
    }
  };

  static final public LispObject MULTIPLE_VALUE_LIST_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() != 1) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    LispObject result = eval(((Cons) args).car, env, thread);
    LispObject[] values = thread._values;
    if (values == null) {
      return new Cons(result);
    }
    thread._values = null;
    LispObject list = NIL;
    for (int i = values.length; i-- > 0;) {
      list = new Cons(values[i], list);
    }
    return list;
  }

  // ### nth-value n form => object
  // Evaluates n and then form and returns the nth value returned by form, or
  // NIL if n >= number of values returned.
  // Should be a macro.
  private static final SpecialOperator NTH_VALUE = new SpecialOperator(Symbol.NTH_VALUE, "n form") {

    public LispObject execute(LispObject args, Environment env) {
      return NTH_VALUE_execute(args, env);
    }
  };

  static final public LispObject NTH_VALUE_execute(LispObject args, Environment env) throws InlineThrows {
    if (args.length() != 2) {
      return wrongNumberOfArgumetnts();
    }
    final LispThread thread = LispThread.currentThread();
    int n = Fixnum.getValue(eval(args.car(), env, thread));
    if (n < 0) {
      n = 0;
    }
    LispObject result = eval(args.cadr(), env, thread);
    LispObject[] values = thread._values;
    thread._values = null;
    if (values == null) {
      // A single value was returned.
      return n == 0 ? result : NIL;
    }
    if (n < values.length) {
      return values[n];
    }
    return NIL;
  }

  // ### call-count
  private static final Primitive CALL_COUNT = new Primitive("call-count", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return CALL_COUNT_execute(arg);
    }
  };

  static final public LispObject CALL_COUNT_execute(LispObject arg) {
    return Fixnum.getInstance(arg.getCallCount());
  }

  // ### set-call-count
  private static final Primitive SET_CALL_COUNT = new Primitive("set-call-count", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return SET_CALL_COUNT_execute(first, second);
    }
  };

  static final public LispObject SET_CALL_COUNT_execute(LispObject first, LispObject second) {
    first.setCallCount(Fixnum.getValue(second));
    return second;
  }

  // ### call-count
  private static final Primitive HOT_COUNT = new Primitive("hot-count", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return HOT_COUNT_execute(arg);
    }
  };

  static final public LispObject HOT_COUNT_execute(LispObject arg) {
    return Fixnum.getInstance(arg.getHotCount());
  }

  // ### set-call-count
  private static final Primitive SET_HOT_COUNT = new Primitive("set-hot-count", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return SET_HOT_COUNT_execute(first, second);
    }
  };

  static final public LispObject SET_HOT_COUNT_execute(LispObject first, LispObject second) {
    first.setHotCount(Fixnum.getValue(second));
    return second;
  }

  // ### lambda-name
  private static final Primitive LAMBDA_NAME = new Primitive("lambda-name", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return LAMBDA_NAME_execute(arg);
    }
  };

  static final public LispObject LAMBDA_NAME_execute(LispObject arg) {
    if (arg instanceof Operator) {
      return ((Operator) arg).getLambdaName();
    }
    if (arg instanceof StandardGenericFunction) {
      return ((StandardGenericFunction) arg).getGenericFunctionName();
    }
    return type_error(arg, Symbol.FUNCTION);
  }

  // ### %set-lambda-name
  private static final Primitive _SET_LAMBDA_NAME = new Primitive("%set-lambda-name", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_LAMBDA_NAME_execute(first, second);
    }
  };

  static final public LispObject _SET_LAMBDA_NAME_execute(LispObject first, LispObject second) {
    if (first instanceof Operator) {
      ((Operator) first).setLambdaName(second);
      return second;
    }
    if (first instanceof StandardGenericFunction) {
      ((StandardGenericFunction) first).setGenericFunctionName(second);
      return second;
    }
    return type_error(first, Symbol.FUNCTION);
  }

  // ### shrink-vector vector new-size => vector
  // Destructively alters the vector, changing its length to NEW-SIZE, which
  // must be less than or equal to its current length.
  // shrink-vector vector new-size => vector
  private static final Primitive SHRINK_VECTOR = new Primitive("shrink-vector", PACKAGE_SYS, true, "vector new-size") {

    public LispObject execute(LispObject first, LispObject second) {
      return SHRINK_VECTOR_execute(first, second);
    }
  };

  static final public LispObject SHRINK_VECTOR_execute(LispObject first, LispObject second) {
    checkVector(first).shrink(Fixnum.getValue(second));
    return first;
  }

  // ### subseq sequence start &optional end
  private static final Primitive SUBSEQ = new Primitive(Symbol.SUBSEQ, "sequence start &optional end") {

    public LispObject execute(LispObject first, LispObject second) {
      return SUBSEQ_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return SUBSEQ_execute(first, second, third);
    }
  };

  static final public LispObject SUBSEQ_execute(LispObject first, LispObject second) {
    final int start = Fixnum.getValue(second);
    if (start < 0) {
      FastStringBuffer sb = new FastStringBuffer("Bad start index (");
      sb.append(start);
      sb.append(") for SUBSEQ.");
      error(new TypeError(sb.toString()));
    }
    if (first.listp()) {
      return list_subseq(first, start, -1);
    }
    if (first instanceof AbstractVector) {
      final AbstractVector v = (AbstractVector) first;
      return v.subseq(start, v.length());
    }
    return type_error(first, Symbol.SEQUENCE);
  }

  static final public LispObject SUBSEQ_execute(LispObject first, LispObject second, LispObject third) {
    final int start = Fixnum.getValue(second);
    if (start < 0) {
      FastStringBuffer sb = new FastStringBuffer("Bad start index (");
      sb.append(start);
      sb.append(").");
      error(new TypeError(sb.toString()));
    }
    int end;
    if (third != NIL) {
      end = Fixnum.getValue(third);
      if (start > end) {
        FastStringBuffer sb = new FastStringBuffer("Start index (");
        sb.append(start);
        sb.append(") is greater than end index (");
        sb.append(end);
        sb.append(") for SUBSEQ.");
        error(new TypeError(sb.toString()));
      }
    } else {
      end = -1;
    }
    if (first.listp()) {
      return list_subseq(first, start, end);
    }
    if (first instanceof AbstractVector) {
      final AbstractVector v = (AbstractVector) first;
      if (end < 0) {
        end = v.length();
      }
      return v.subseq(start, end);
    }
    return type_error(first, Symbol.SEQUENCE);
  }

  // ### list
  private static final Primitive LIST = new Primitive(Symbol.LIST, "&rest objects") {

    public LispObject execute() {
      return LIST_execute();
    }

    public LispObject execute(LispObject arg) {
      return LIST_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return LIST_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return LIST_execute(first, second, third);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
      return LIST_execute(first, second, third, fourth);
    }

    public LispObject execute(LispObject[] args) {
      return LIST_execute(args);
    }
  };

  static final public LispObject LIST_execute() {
    return NIL;
  }

  static final public LispObject LIST_execute(LispObject arg) {
    return new Cons(arg);
  }

  static final public LispObject LIST_execute(LispObject first, LispObject second) {
    return new Cons(first, new Cons(second));
  }

  static final public LispObject LIST_execute(LispObject first, LispObject second, LispObject third) {
    return new Cons(first, new Cons(second, new Cons(third)));
  }

  static final public LispObject LIST_execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
    return new Cons(first, new Cons(second, new Cons(third, new Cons(fourth))));
  }

  static final public LispObject LIST_execute(LispObject[] args) {
    LispObject result = NIL;
    for (int i = args.length; i-- > 0;) {
      result = new Cons(args[i], result);
    }
    return result;
  }

  // ### list*
  private static final Primitive LIST_STAR = new Primitive(Symbol.LIST_STAR, "&rest objects") {

    public LispObject execute() {
      return LIST_STAR_execute();
    }

    public LispObject execute(LispObject arg) {
      return LIST_STAR_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return LIST_STAR_execute(first, second);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return LIST_STAR_execute(first, second, third);
    }

    public LispObject execute(LispObject first, LispObject second, LispObject third, LispObject fourth) {
      return LIST_STAR_execute(first, second, third, fourth);
    }

    public LispObject execute(LispObject[] args) {
      return LIST_STAR_execute(args);
    }
  };

  static final public LispObject LIST_STAR_execute() throws InlineThrows {
    return wrongNumberOfArgumetnts();
  }

  static final public LispObject LIST_STAR_execute(LispObject arg) {
    return arg;
  }

  static final public LispObject LIST_STAR_execute(LispObject first, LispObject second) {
    return new Cons(first, second);
  }

  static final public LispObject LIST_STAR_execute(LispObject first, LispObject second, LispObject third) {
    return new Cons(first, new Cons(second, third));
  }

  static final public LispObject LIST_STAR_execute(LispObject first, LispObject second, LispObject third,
      LispObject fourth) {
    return new Cons(first, new Cons(second, new Cons(third, fourth)));
  }

  static final public LispObject LIST_STAR_execute(LispObject[] args) {
    int i = args.length - 1;
    LispObject result = args[i];
    while (i-- > 0) {
      result = new Cons(args[i], result);
    }
    return result;
  }

  // ### nreconc
  private static final Primitive NRECONC = new Primitive(Symbol.NRECONC, "list tail") {

    public LispObject execute(LispObject list, LispObject obj) {
      return NRECONC_execute(list, obj);
    }
  };

  static final public LispObject NRECONC_execute(LispObject list, LispObject obj) {
    if (list instanceof Cons) {
      LispObject list3 = list.cdr();
      if (list3 instanceof Cons) {
        if (list3.cdr() instanceof Cons) {
          LispObject list1 = list3;
          LispObject list2 = NIL;
          do {
            LispObject h = list3.cdr();
            list3.setCdr(list2);
            list2 = list3;
            list3 = h;
          } while (list3.cdr() instanceof Cons);
          list.setCdr(list2);
          list1.setCdr(list3);
        }
        LispObject h = list.car();
        list.setCar(list3.car());
        list3.setCar(h);
        list3.setCdr(obj);
      } else if (list3 == NIL) {
        list.setCdr(obj);
      } else {
        type_error(list3, Symbol.LIST);
      }
      return list;
    } else if (list == NIL) {
      return obj;
    } else {
      return type_error(list, Symbol.LIST);
    }
  }

  // ### reverse
  private static final Primitive REVERSE = new Primitive(Symbol.REVERSE, "sequence") {

    public LispObject execute(LispObject arg) {
      return REVERSE_execute(arg);
    }
  };

  static final public LispObject REVERSE_execute(LispObject arg) {
    return arg.reverse();
  }

  // ### delete-eq item sequence => result-sequence
  private static final Primitive DELETE_EQ = new Primitive("delete-eq", PACKAGE_SYS, true, "item sequence") {

    public LispObject execute(LispObject item, LispObject sequence) {
      return DELETE_EQ_execute(item, sequence);
    }
  };

  static final public LispObject DELETE_EQ_execute(LispObject item, LispObject sequence) {
    if (sequence instanceof AbstractVector) {
      return ((AbstractVector) sequence).deleteEq(item);
    } else {
      return LIST_DELETE_EQ.execute(item, sequence);
    }
  }

  // ### delete-eql item seqluence => result-seqluence
  private static final Primitive DELETE_EQL = new Primitive("delete-eql", PACKAGE_SYS, true, "item sequence") {

    public LispObject execute(LispObject item, LispObject sequence) {
      return DELETE_EQL_execute(item, sequence);
    }
  };

  static final public LispObject DELETE_EQL_execute(LispObject item, LispObject sequence) {
    if (sequence instanceof AbstractVector) {
      return ((AbstractVector) sequence).deleteEql(item);
    } else {
      return LIST_DELETE_EQL.execute(item, sequence);
    }
  }

  // ### list-delete-eq item list => result-list
  private static final Primitive LIST_DELETE_EQ = new Primitive("list-delete-eq", PACKAGE_SYS, true, "item list") {

    public LispObject execute(LispObject item, LispObject list) {
      return LIST_DELETE_EQ_execute(item, list);
    }
  };

  static final public LispObject LIST_DELETE_EQ_execute(LispObject item, LispObject list) {
    if (list instanceof Cons) {
      LispObject tail = list;
      LispObject splice = list;
      while (tail instanceof Cons) {
        LispObject car = tail.car();
        if (car == item) {
          if (tail.cdr() != NIL) {
            LispObject temp = tail;
            tail.setCar(temp.cadr());
            tail.setCdr(temp.cddr());
          } else {
            // Last item.
            if (tail == list) {
              return NIL;
            }
            splice.setCdr(NIL);
            return list;
          }
        } else {
          splice = tail;
          tail = tail.cdr();
        }
      }
      if (tail == NIL) {
        return list;
      } else {
        return type_error(tail, Symbol.LIST);
      }
    } else if (list == NIL) {
      return list;
    } else {
      return type_error(list, Symbol.LIST);
    }
  }

  // ### list-delete-eql item list => result-list
  private static final Primitive LIST_DELETE_EQL = new Primitive("list-delete-eql", PACKAGE_SYS, true, "item list") {

    public LispObject execute(LispObject item, LispObject list) {
      return LIST_DELETE_EQL_execute(item, list);
    }
  };

  static final public LispObject LIST_DELETE_EQL_execute(LispObject item, LispObject list) {
    if (list instanceof Cons) {
      LispObject tail = list;
      LispObject splice = list;
      while (tail instanceof Cons) {
        LispObject car = tail.car();
        if (car.eql(item)) {
          if (tail.cdr() != NIL) {
            LispObject temp = tail;
            tail.setCar(temp.cadr());
            tail.setCdr(temp.cddr());
          } else {
            // Last item.
            if (tail == list) {
              return NIL;
            }
            splice.setCdr(NIL);
            return list;
          }
        } else {
          splice = tail;
          tail = tail.cdr();
        }
      }
      if (tail == NIL) {
        return list;
      } else {
        return type_error(tail, Symbol.LIST);
      }
    } else if (list == NIL) {
      return list;
    } else {
      return type_error(list, Symbol.LIST);
    }
  }

  // ### vector-delete-eq item vector => result-vector
  private static final Primitive VECTOR_DELETE_EQ = new Primitive("vector-delete-eq", PACKAGE_SYS, true, "item vector") {

    public LispObject execute(LispObject item, LispObject vector) {
      return VECTOR_DELETE_EQ_execute(item, vector);
    }
  };

  static final public LispObject VECTOR_DELETE_EQ_execute(LispObject item, LispObject vector) {
    checkVector(vector).deleteEq(item);
    return vector;
  }

  // ### vector-delete-eql item vector => result-vector
  private static final Primitive VECTOR_DELETE_EQL = new Primitive("vector-delete-eql", PACKAGE_SYS, true,
      "item vector") {

    public LispObject execute(LispObject item, LispObject vector) {
      return VECTOR_DELETE_EQL_execute(item, vector);
    }
  };

  static final public LispObject VECTOR_DELETE_EQL_execute(LispObject item, LispObject vector) {
    checkVector(vector).deleteEql(item);
    return vector;
  }

  // ### %set-elt
  // %setelt sequence index newval => newval
  private static final Primitive _SET_ELT = new Primitive("%set-elt", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second, LispObject third) {
      return _SET_ELT_execute(first, second, third);
    }
  };

  static final public LispObject _SET_ELT_execute(LispObject first, LispObject second, LispObject third) {
    if (first instanceof AbstractVector) {
      ((AbstractVector) first).aset(Fixnum.getValue(second), third);
      return third;
    }
    if (first instanceof Cons) {
      int index = Fixnum.getValue(second);
      if (index < 0) {
        error(new TypeError());
      }
      LispObject list = first;
      int i = 0;
      while (true) {
        if (i == index) {
          list.setCar(third);
          return third;
        }
        list = list.cdr();
        if (list == NIL) {
          error(new TypeError());
        }
        ++i;
      }
    }
    return type_error(first, Symbol.SEQUENCE);
  }

  // ### %make-list
  private static final Primitive _MAKE_LIST = new Primitive("%make-list", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _MAKE_LIST_execute(first, second);
    }
  };

  static final public LispObject _MAKE_LIST_execute(LispObject first, LispObject second) {
    int size = Fixnum.getValue(first);
    if (size < 0) {
      return type_error(first, list(Symbol.INTEGER, Fixnum.ZERO, Symbol.MOST_POSITIVE_FIXNUM.getSymbolValue()));
    }
    LispObject result = NIL;
    for (int i = size; i-- > 0;) {
      result = new Cons(second, result);
    }
    return result;
  }

  // ### %member item list key test test-not => tail
  private static final Primitive _MEMBER = new Primitive("%member", PACKAGE_SYS, true) {

    public LispObject execute(LispObject item, LispObject list, LispObject key, LispObject test, LispObject testNot) {
      return _MEMBER_execute(item, list, key, test, testNot);
    }
  };

  static final public LispObject _MEMBER_execute(LispObject item, LispObject list, LispObject key, LispObject test,
      LispObject testNot) {
    LispObject tail = checkList(list);
    if (test != NIL && testNot != NIL) {
      error(new LispError("MEMBER: test and test-not both supplied"));
    }
    if (testNot == NIL) {
      if (test == NIL || test == Symbol.EQL) {
        test = EQL;
      }
    }
    if (key == NIL) {
      if (test == EQL) {
        while (tail instanceof Cons) {
          if (item.eql(((Cons) tail).car)) {
            return tail;
          }
          tail = ((Cons) tail).cdr;
        }
      } else if (test != NIL) {
        while (tail instanceof Cons) {
          LispObject candidate = ((Cons) tail).car;
          if (test.execute(item, candidate) != NIL) {
            return tail;
          }
          tail = ((Cons) tail).cdr;
        }
      } else {
        // test == NIL
        while (tail instanceof Cons) {
          LispObject candidate = ((Cons) tail).car;
          if (testNot.execute(item, candidate) == NIL) {
            return tail;
          }
          tail = ((Cons) tail).cdr;
        }
      }
    } else {
      // key != NIL
      while (tail instanceof Cons) {
        LispObject candidate = key.execute(((Cons) tail).car);
        if (test != NIL) {
          if (test.execute(item, candidate) != NIL) {
            return tail;
          }
        } else {
          if (testNot.execute(item, candidate) == NIL) {
            return tail;
          }
        }
        tail = ((Cons) tail).cdr;
      }
    }
    if (tail != NIL) {
      type_error(tail, Symbol.LIST);
    }
    return NIL;
  }

  // ### funcall-key function-or-nil element
  private static final Primitive FUNCALL_KEY = new Primitive("funcall-key", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second) {
      return FUNCALL_KEY_execute(first, second);
    }
  };

  static final public LispObject FUNCALL_KEY_execute(LispObject first, LispObject second) {
    if (first != NIL) {
      return LispThread.currentThread().execute(first, second);
    }
    return second;
  }

  // ### coerce-to-function
  private static final Primitive COERCE_TO_FUNCTION = new Primitive("coerce-to-function", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return COERCE_TO_FUNCTION_execute(arg);
    }
  };

  static final public LispObject COERCE_TO_FUNCTION_execute(LispObject arg) {
    return coerceToFunction(arg);
  }

  // ### make-closure lambda-form environment => closure
  private static final Primitive MAKE_CLOSURE = new Primitive("make-closure", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return MAKE_CLOSURE_execute(first, second);
    }
  };

  static final public LispObject MAKE_CLOSURE_execute(LispObject first, LispObject second) {
    if (first instanceof Cons && ((Cons) first).car == Symbol.LAMBDA) {
      final Environment env;
      if (second == NIL) {
        env = new Environment();
      } else {
        env = checkEnvironment(second);
      }
      return new Closure(first, env);
    }
    return error(new TypeError("The argument to MAKE-CLOSURE is not a lambda form."));
  }

  // ### streamp
  private static final Primitive STREAMP = new Primitive(Symbol.STREAMP, "object") {

    public LispObject execute(LispObject arg) {
      return STREAMP_execute(arg);
    }
  };

  static final public LispObject STREAMP_execute(LispObject arg) {
    return arg instanceof Stream ? T : NIL;
  }

  // ### integerp
  private static final Primitive INTEGERP = new Primitive(Symbol.INTEGERP, "object") {

    public LispObject execute(LispObject arg) {
      return INTEGERP_execute(arg);
    }
  };

  static final public LispObject INTEGERP_execute(LispObject arg) {
    return arg.INTEGERP();
  }

  // ### evenp
  private static final Primitive EVENP = new Primitive(Symbol.EVENP, "integer") {

    public LispObject execute(LispObject arg) {
      return EVENP_execute(arg);
    }
  };

  static final public LispObject EVENP_execute(LispObject arg) {
    return arg.EVENP();
  }

  // ### oddp
  private static final Primitive ODDP = new Primitive(Symbol.ODDP, "integer") {

    public LispObject execute(LispObject arg) {
      return ODDP_execute(arg);
    }
  };

  static final public LispObject ODDP_execute(LispObject arg) {
    return arg.ODDP();
  }

  // ### numberp
  private static final Primitive NUMBERP = new Primitive(Symbol.NUMBERP, "object") {

    public LispObject execute(LispObject arg) {
      return NUMBERP_execute(arg);
    }
  };

  static final public LispObject NUMBERP_execute(LispObject arg) {
    return arg.NUMBERP();
  }

  // ### realp
  private static final Primitive REALP = new Primitive(Symbol.REALP, "object") {

    public LispObject execute(LispObject arg) {
      return REALP_execute(arg);
    }
  };

  static final public LispObject REALP_execute(LispObject arg) {
    return arg.REALP();
  }

  // ### rationalp
  private static final Primitive RATIONALP = new Primitive(Symbol.RATIONALP, "object") {

    public LispObject execute(LispObject arg) {
      return RATIONALP_execute(arg);
    }
  };

  static final public LispObject RATIONALP_execute(LispObject arg) {
    return arg.RATIONALP();
  }

  // ### complex
  private static final Primitive COMPLEX = new Primitive(Symbol.COMPLEX, "realpart &optional imagpart") {

    public LispObject execute(LispObject arg) {
      return COMPLEX_execute(arg);
    }

    public LispObject execute(LispObject first, LispObject second) {
      return COMPLEX_execute(first, second);
    }
  };

  static final public LispObject COMPLEX_execute(LispObject arg) {
    if (arg instanceof SingleFloat) {
      return Complex.getInstance(arg, SingleFloat.ZERO);
    }
    if (arg instanceof DoubleFloat) {
      return Complex.getInstance(arg, DoubleFloat.ZERO);
    }
    if (arg.realp()) {
      return arg;
    }
    return type_error(arg, Symbol.REAL);
  }

  static final public LispObject COMPLEX_execute(LispObject first, LispObject second) {
    return Complex.getInstance(first, second);
  }

  // ### complexp
  private static final Primitive COMPLEXP = new Primitive(Symbol.COMPLEXP, "object") {

    public LispObject execute(LispObject arg) {
      return COMPLEXP_execute(arg);
    }
  };

  static final public LispObject COMPLEXP_execute(LispObject arg) {
    return arg.COMPLEXP();
  }

  // ### numerator
  private static final Primitive NUMERATOR = new Primitive(Symbol.NUMERATOR, "rational") {

    public LispObject execute(LispObject arg) {
      return NUMERATOR_execute(arg);
    }
  };

  static final public LispObject NUMERATOR_execute(LispObject arg) {
    return arg.NUMERATOR();
  }

  // ### denominator
  private static final Primitive DENOMINATOR = new Primitive(Symbol.DENOMINATOR, "rational") {

    public LispObject execute(LispObject arg) {
      return DENOMINATOR_execute(arg);
    }
  };

  static final public LispObject DENOMINATOR_execute(LispObject arg) {
    return arg.DENOMINATOR();
  }

  // ### realpart
  private static final Primitive REALPART = new Primitive(Symbol.REALPART, "number") {

    public LispObject execute(LispObject arg) {
      return REALPART_execute(arg);
    }
  };

  static final public LispObject REALPART_execute(LispObject arg) {
    if (arg instanceof Complex) {
      return ((Complex) arg).getRealPart();
    }
    if (arg.numberp()) {
      return arg;
    }
    return type_error(arg, Symbol.NUMBER);
  }

  // ### imagpart
  private static final Primitive IMAGPART = new Primitive(Symbol.IMAGPART, "number") {

    public LispObject execute(LispObject arg) {
      return IMAGPART_execute(arg);
    }
  };

  static final public LispObject IMAGPART_execute(LispObject arg) {
    if (arg instanceof Complex) {
      return ((Complex) arg).getImaginaryPart();
    }
    return arg.multiplyBy(Fixnum.ZERO);
  }

  // ### integer-length
  private static final Primitive INTEGER_LENGTH = new Primitive(Symbol.INTEGER_LENGTH, "integer") {

    public LispObject execute(LispObject arg) {
      return INTEGER_LENGTH_execute(arg);
    }
  };

  static final public LispObject INTEGER_LENGTH_execute(LispObject arg) {
    if (arg instanceof Fixnum) {
      int n = ((Fixnum) arg).value;
      if (n < 0) {
        n = ~n;
      }
      int count = 0;
      while (n > 0) {
        n = n >>> 1;
        ++count;
      }
      return Fixnum.getInstance(count);
    }
    if (arg instanceof Bignum) {
      return Fixnum.getInstance(((Bignum) arg).value.bitLength());
    }
    return type_error(arg, Symbol.INTEGER);
  }

  // ### gcd-2
  private static final Primitive GCD_2 = new Primitive("gcd-2", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second) {
      return GCD_2_execute(first, second);
    }
  };

  static final public LispObject GCD_2_execute(LispObject first, LispObject second) {
    BigInteger n1;
    BigInteger n2;
    if (first instanceof Fixnum) {
      n1 = BigInteger.valueOf(((Fixnum) first).value);
    } else if (first instanceof Bignum) {
      n1 = ((Bignum) first).value;
    } else {
      return type_error(first, Symbol.INTEGER);
    }
    if (second instanceof Fixnum) {
      n2 = BigInteger.valueOf(((Fixnum) second).value);
    } else if (second instanceof Bignum) {
      n2 = ((Bignum) second).value;
    } else {
      return type_error(second, Symbol.INTEGER);
    }
    return number(n1.gcd(n2));
  }

  // ### identity-hash-code
  private static final Primitive IDENTITY_HASH_CODE = new Primitive("identity-hash-code", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return IDENTITY_HASH_CODE_execute(arg);
    }
  };

  static final public LispObject IDENTITY_HASH_CODE_execute(LispObject arg) {
    return Fixnum.getInstance(System.identityHashCode(arg));
  }

  // ### simple-vector-search pattern vector => position
  // Searches vector for pattern.
  private static final Primitive SIMPLE_VECTOR_SEARCH = new Primitive("simple-vector-search", PACKAGE_SYS, false) {

    public LispObject execute(LispObject first, LispObject second) {
      return SIMPLE_VECTOR_SEARCH_execute(first, second);
    }
  };

  static final public LispObject SIMPLE_VECTOR_SEARCH_execute(LispObject first, LispObject second) {
    AbstractVector v = checkVector(second);
    if (first.length() == 0) {
      return Fixnum.ZERO;
    }
    final int patternLength = first.length();
    final int limit = v.length() - patternLength;
    if (first instanceof AbstractVector) {
      AbstractVector pattern = (AbstractVector) first;
      LispObject element = pattern.AREF(0);
      for (int i = 0; i <= limit; i++) {
        if (v.AREF(i).eql(element)) {
          // Found match for first element of pattern.
          boolean match = true;
          // We've already checked the first element.
          int j = i + 1;
          for (int k = 1; k < patternLength; k++) {
            if (v.AREF(j).eql(pattern.AREF(k))) {
              ++j;
            } else {
              match = false;
              break;
            }
          }
          if (match) {
            return Fixnum.getInstance(i);
          }
        }
      }
    } else {
      // Pattern is a list.
      LispObject element = first.car();
      for (int i = 0; i <= limit; i++) {
        if (v.AREF(i).eql(element)) {
          // Found match for first element of pattern.
          boolean match = true;
          // We've already checked the first element.
          int j = i + 1;
          for (LispObject rest = first.cdr(); rest != NIL; rest = rest.cdr()) {
            if (v.AREF(j).eql(rest.car())) {
              ++j;
            } else {
              match = false;
              break;
            }
          }
          if (match) {
            return Fixnum.getInstance(i);
          }
        }
      }
    }
    return NIL;
  }

  // ### uptime
  private static final Primitive UPTIME = new Primitive("uptime", PACKAGE_EXT, true) {

    public LispObject execute() {
      return UPTIME_execute();
    }
  };

  static final public LispObject UPTIME_execute() {
    return number(System.currentTimeMillis() - Main.startTimeMillis);
  }

  // ### built-in-function-p
  private static final Primitive BUILT_IN_FUNCTION_P = new Primitive("built-in-function-p", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return BUILT_IN_FUNCTION_P_execute(arg);
    }
  };

  static final public LispObject BUILT_IN_FUNCTION_P_execute(LispObject arg) {
    return checkSymbol(arg).isBuiltInFunction() ? T : NIL;
  }

  // ### inspected-parts
  private static final Primitive INSPECTED_PARTS = new Primitive("inspected-parts", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return INSPECTED_PARTS_execute(arg);
    }
  };

  static final public LispObject INSPECTED_PARTS_execute(LispObject arg) {
    return arg.getParts();
  }

  // ### inspected-description
  private static final Primitive INSPECTED_DESCRIPTION = new Primitive("inspected-description", PACKAGE_SYS, false) {

    public LispObject execute(LispObject arg) {
      return INSPECTED_DESCRIPTION_execute(arg);
    }
  };

  static final public LispObject INSPECTED_DESCRIPTION_execute(LispObject arg) {
    return arg.getDescription();
  }

  // ### %class-name
  private static final Primitive _CLASS_NAME = new Primitive("%class-name", PACKAGE_SYS, true, "class") {

    public LispObject execute(LispObject arg) {
      return _CLASS_NAME_execute(arg);
    }
  };

  static final public LispObject _CLASS_NAME_execute(LispObject arg) {
    return checkClass(arg).symbol;
  }

  // ### %set-class-name
  private static final Primitive _SET_CLASS_NAME = new Primitive("%set-class-name", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_NAME_execute(first, second);
    }
  };

  @NoInline
  static final public LispObject _SET_CLASS_NAME_execute(LispObject first, LispObject second) {
    checkClass(first).symbol = checkSymbol(second);
    return second;
  }

  // ### class-layout
  private static final Primitive CLASS_LAYOUT = new Primitive("class-layout", PACKAGE_SYS, true, "class") {

    public LispObject execute(LispObject arg) {
      return CLASS_LAYOUT_execute(arg);
    }
  };

  @NoInline
  static final public LispObject CLASS_LAYOUT_execute(LispObject arg) {
    Layout layout = checkClass(arg).getClassLayout();
    return layout != null ? layout : NIL;
  }

  // ### %set-class-layout
  private static final Primitive _SET_CLASS_LAYOUT = new Primitive("%set-class-layout", PACKAGE_SYS, true,
      "class layout") {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_LAYOUT_execute(first, second);
    }
  };

  static final public LispObject _SET_CLASS_LAYOUT_execute(LispObject first, LispObject second) {
    if (second instanceof Layout) {
      checkClass(first).setClassLayout((Layout) second);
      return second;
    }
    return type_error(second, Symbol.LAYOUT);
  }

  // ### class-direct-superclasses
  private static final Primitive CLASS_DIRECT_SUPERCLASSES = new Primitive("class-direct-superclasses", PACKAGE_SYS,
      true) {

    public LispObject execute(LispObject arg) {
      return CLASS_DIRECT_SUPERCLASSES_execute(arg);
    }
  };

  static final public LispObject CLASS_DIRECT_SUPERCLASSES_execute(LispObject arg) {
    return checkClass(arg).getDirectSuperclasses();
  }

  // ### %set-class-direct-superclasses
  private static final Primitive _SET_CLASS_DIRECT_SUPERCLASSES = new Primitive("%set-class-direct-superclasses",
      PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_DIRECT_SUPERCLASSES_execute(first, second);
    }
  };

  static final public LispObject _SET_CLASS_DIRECT_SUPERCLASSES_execute(LispObject first, LispObject second) {
    checkClass(first).setDirectSuperclasses(second);
    return second;
  }

  // ### class-direct-subclasses
  private static final Primitive CLASS_DIRECT_SUBCLASSES = new Primitive("class-direct-subclasses", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return CLASS_DIRECT_SUBCLASSES_execute(arg);
    }
  };

  static final public LispObject CLASS_DIRECT_SUBCLASSES_execute(LispObject arg) {
    return checkClass(arg).getDirectSubclasses();
  }

  // ### %set-class-direct-subclasses
  private static final Primitive _SET_CLASS_DIRECT_SUBCLASSES = new Primitive("%set-class-direct-subclasses",
      PACKAGE_SYS, true, "class direct-subclasses") {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_DIRECT_SUBCLASSES_execute(first, second);
    }
  };

  static final public LispObject _SET_CLASS_DIRECT_SUBCLASSES_execute(LispObject first, LispObject second) {
    checkClass(first).setDirectSubclasses(second);
    return second;
  }

  // ### %class-precedence-list
  private static final Primitive _CLASS_PRECEDENCE_LIST = new Primitive("%class-precedence-list", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return _CLASS_PRECEDENCE_LIST_execute(arg);
    }
  };

  static final public LispObject _CLASS_PRECEDENCE_LIST_execute(LispObject arg) {
    return checkClass(arg).getCPL();
  }

  // ### set-class-precedence-list
  private static final Primitive SET_CLASS_PRECEDENCE_LIST = new Primitive("set-class-precedence-list", PACKAGE_SYS,
      true) {

    public LispObject execute(LispObject first, LispObject second) {
      return SET_CLASS_PRECEDENCE_LIST_execute(first, second);
    }
  };

  static final public LispObject SET_CLASS_PRECEDENCE_LIST_execute(LispObject first, LispObject second) {
    checkClass(first).classPrecedenceList = second;
    return second;
  }

  // ### class-direct-methods
  private static final Primitive CLASS_DIRECT_METHODS = new Primitive("class-direct-methods", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return CLASS_DIRECT_METHODS_execute(arg);
    }
  };

  @NoInline
  static final public LispObject CLASS_DIRECT_METHODS_execute(LispObject arg) {
    return checkClass(arg).directMethods;
  }

  // ### %set-class-direct-methods
  private static final Primitive _SET_CLASS_DIRECT_METHODS = new Primitive("%set-class-direct-methods", PACKAGE_SYS,
      true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_DIRECT_METHODS_execute(first, second);
    }
  };

  static final public LispObject _SET_CLASS_DIRECT_METHODS_execute(LispObject first, LispObject second) {
    checkClass(first).directMethods = second;
    return second;
  }

  // ### class-documentation
  private static final Primitive CLASS_DOCUMENTATION = new Primitive("class-documentation", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return CLASS_DOCUMENTATION_execute(arg);
    }
  };

  static final public LispObject CLASS_DOCUMENTATION_execute(LispObject arg) {
    return checkClass(arg).documentation;
  }

  // ### %set-class-documentation
  private static final Primitive _SET_CLASS_DOCUMENTATION = new Primitive("%set-class-documentation", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_DOCUMENTATION_execute(first, second);
    }
  };

  static final public LispObject _SET_CLASS_DOCUMENTATION_execute(LispObject first, LispObject second) {
    checkClass(first).documentation = second;
    return second;
  }

  // ### class-finalized-p
  private static final Primitive CLASS_FINALIZED_P = new Primitive("class-finalized-p", PACKAGE_SYS, true) {

    public LispObject execute(LispObject arg) {
      return CLASS_FINALIZED_P_execute(arg);
    }
  };

  static final public LispObject CLASS_FINALIZED_P_execute(LispObject arg) {
    return checkClass(arg).isFinalized() ? T : NIL;
  }

  // ### %set-class-finalized-p
  private static final Primitive _SET_CLASS_FINALIZED_P = new Primitive("%set-class-finalized-p", PACKAGE_SYS, true) {

    public LispObject execute(LispObject first, LispObject second) {
      return _SET_CLASS_FINALIZED_P_execute(first, second);
    }
  };

  static final public LispObject _SET_CLASS_FINALIZED_P_execute(LispObject first, LispObject second) {
    checkClass(first).setFinalized(second != NIL);
    return second;
  }

  // ### classp
  private static final Primitive CLASSP = new Primitive("classp", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return CLASSP_execute(arg);
    }
  };

  static final public LispObject CLASSP_execute(LispObject arg) {
    return arg instanceof LispClass ? T : NIL;
  }

  // ### char-to-utf8 char => octets
  private static final Primitive CHAR_TO_UTF8 = new Primitive("char-to-utf8", PACKAGE_EXT, true) {

    public LispObject execute(LispObject arg) {
      return CHAR_TO_UTF8_execute(arg);
    }
  };

  static final public LispObject CHAR_TO_UTF8_execute(LispObject arg) {
    final LispCharacter c;
    c = checkCharacter(arg);
    char[] chars = new char[1];
    chars[0] = c.value;
    String s = new String(chars);
    final byte[] bytes;
    try {
      bytes = s.getBytes("UTF8");
    } catch (java.io.UnsupportedEncodingException e) {
      return error(new LispError("UTF8 is not a supported encoding."));
    }
    LispObject[] objects = new LispObject[bytes.length];
    for (int i = bytes.length; i-- > 0;) {
      int n = bytes[i];
      if (n < 0) {
        n += 256;
      }
      objects[i] = Fixnum.getInstance(n);
    }
    return new SimpleVector(objects);
  }

  // ### %documentation
  private static final Primitive _DOCUMENTATION = new Primitive("%documentation", PACKAGE_SYS, true, "object doc-type") {

    public LispObject execute(LispObject object, LispObject docType) {
      return _DOCUMENTATION_execute(object, docType);
    }
  };

  static final public LispObject _DOCUMENTATION_execute(LispObject object, LispObject docType) {
    LispObject doc = object.getDocumentation(docType);
    if (doc == NIL) {
      if (docType == Symbol.FUNCTION && object instanceof Symbol) {
        LispObject function = object.getSymbolFunction();
        if (function != null) {
          doc = function.getDocumentation(docType);
        }
      }
    }
    return doc;
  }

  // ### %set-documentation
  private static final Primitive _SET_DOCUMENTATION = new Primitive("%set-documentation", PACKAGE_SYS, true,
      "object doc-type documentation") {

    public LispObject execute(LispObject object, LispObject docType, LispObject documentation) {
      return _SET_DOCUMENTATION_execute(object, docType, documentation);
    }
  };

  static final public LispObject _SET_DOCUMENTATION_execute(LispObject object, LispObject docType,
      LispObject documentation) {
    object.setDocumentation(docType, documentation);
    return documentation;
  }

  // ### %putf
  private static final Primitive _PUTF = new Primitive("%putf", PACKAGE_SYS, true, "plist indicator new-value") {

    public LispObject execute(LispObject plist, LispObject indicator, LispObject newValue) {
      return _PUTF_execute(plist, indicator, newValue);
    }
  };

  static final public LispObject _PUTF_execute(LispObject plist, LispObject indicator, LispObject newValue) {
    return putf(plist, indicator, newValue);
  }

  // ### function-plist
  private static final Primitive FUNCTION_PLIST = new Primitive("function-plist", PACKAGE_SYS, true, "function") {

    public LispObject execute(LispObject arg) {
      return FUNCTION_PLIST_execute(arg);
    }
  };

  static final public LispObject FUNCTION_PLIST_execute(LispObject arg) {
    return checkFunction(arg).getPropertyList();
  }

  // ### make-keyword
  private static final Primitive MAKE_KEYWORD = new Primitive("make-keyword", PACKAGE_SYS, true, "symbol") {

    public LispObject execute(LispObject arg) {
      return MAKE_KEYWORD_execute(arg);
    }
  };

  static final public LispObject MAKE_KEYWORD_execute(LispObject arg) {
    return PACKAGE_KEYWORD.intern(checkSymbol(arg).name);
  }

  // ### standard-object-p object => generalized-boolean
  private static final Primitive STANDARD_OBJECT_P = new Primitive("standard-object-p", PACKAGE_SYS, true, "object") {

    public LispObject execute(LispObject arg) {
      return STANDARD_OBJECT_P_execute(arg);
    }
  };

  static final public LispObject STANDARD_OBJECT_P_execute(LispObject arg) {
    return arg instanceof StandardObject ? T : NIL;
  }

  // ### copy-tree
  private static final Primitive COPY_TREE = new Primitive(Symbol.COPY_TREE, "object") {

    public LispObject execute(LispObject arg) {
      return COPY_TREE_execute(arg);
    }
  };

  @InlinableMethod
  static final public LispObject COPY_TREE_execute(LispObject arg) {
    if (arg instanceof Cons) {
      Cons cons = (Cons) arg;
      return new Cons(COPY_TREE_execute(cons.car), COPY_TREE_execute(cons.cdr));
    } else {
      return arg;
    }
  }

  /**
   * Description of the Method
   * 
   * @param arg
   *          Description of Parameter
   */
  private static void checkRedefinition(LispObject arg) {
    final LispThread thread = LispThread.currentThread();
    if (_WARN_ON_REDEFINITION_.symbolValue(thread) != NIL) {
      if (arg instanceof Symbol) {
        LispObject oldDefinition = arg.getSymbolFunction();
        if (oldDefinition != null && !(oldDefinition instanceof Autoload)) {
          LispObject oldSource = Extensions.SOURCE_PATHNAME.execute(arg);
          LispObject currentSource = _SOURCE_.symbolValue(thread);
          if (currentSource == NIL) {
            currentSource = Keyword.TOP_LEVEL;
          }
          if (oldSource != NIL) {
            if (currentSource.equal(oldSource)) {
              return;
            }
            // OK
          }
          if (currentSource == Keyword.TOP_LEVEL) {
            Symbol.STYLE_WARN.execute(new SimpleString("redefining ~S at top level"), arg);

          } else {
            SpecialBindingsMark mark = thread.markSpecialBindings();
            thread.bindSpecial(Symbol._PACKAGE_, PACKAGE_CL);
            try {
              Symbol.STYLE_WARN.execute(new SimpleString("redefining ~S in ~S"), arg, currentSource);
            } finally {
              thread.resetSpecialBindings(mark);
            }
          }
        }
      }
    }
  }

  /**
   * Description of the Method
   * 
   * @param list
   *          Description of Parameter
   * @param start
   *          Description of Parameter
   * @param end
   *          Description of Parameter
   * @return Description of the Returned Value
   */
  private static final LispObject list_subseq(LispObject list, int start, int end) {
    int index = 0;
    LispObject result = NIL;
    while (list != NIL) {
      if (end >= 0 && index == end) {
        return result.nreverse();
      }
      if (index++ >= start) {
        result = new Cons(list.car(), result);
      }
      list = list.cdr();
    }
    return result.nreverse();
  }

  static {
    InlinedPrimitiveRegistry.inlineStaticsNow(Primitives.class);
  }

}
