/*
 * CompiledClosure.java
 *
 * Copyright (C) 2004-2005 Peter Graves
 * $Id: CompiledClosure.java 11889 2009-05-17 11:36:40Z vvoutilainen $
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

abstract public class CompiledClosure extends Closure
        implements Cloneable
{

  public ClosureBinding[] ctx;

  public CompiledClosure(LispObject lambdaList)
    throws ConditionThrowable
  {
    super(list(SymbolConstants.LAMBDA, lambdaList), null);
  }

  final public CompiledClosure setContext(ClosureBinding[] context)
  {
    ctx = context;
    return this;
  }

  final public CompiledClosure dup()
  {
      CompiledClosure result = null;
      try {
	  result = (CompiledClosure)super.clone();
      } catch (Exception e) {
    	  e.printStackTrace();
      }
      return result;
  }

  @Override
  public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
  {
    if (typeSpecifier == SymbolConstants.COMPILED_FUNCTION)
      return T;
    return super.typep(typeSpecifier);
  }

  private final LispObject notImplemented() throws ConditionThrowable
  {
    return error(new WrongNumberOfArgumentsException(this));
  }


  // Zero args.
  public LispObject execute() throws ConditionThrowable
  {
    return execute(ZERO_LISPOBJECTS);
  }

  // One arg.
  public LispObject execute( LispObject first)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[1];
    args[0] = first;
    return execute(args);
  }

  // Two args.
  public LispObject execute( LispObject first,
                            LispObject second)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[2];
    args[0] = first;
    args[1] = second;
    return execute(args);
  }

  // Three args.
  public LispObject execute( LispObject first,
                            LispObject second, LispObject third)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[3];
    args[0] = first;
    args[1] = second;
    args[2] = third;
    return execute(args);
  }

  // Four args.
  public LispObject execute( LispObject first,
                            LispObject second, LispObject third,
                            LispObject fourth)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[4];
    args[0] = first;
    args[1] = second;
    args[2] = third;
    args[3] = fourth;
    return execute(args);
  }

  // Five args.
  public LispObject execute( LispObject first,
                            LispObject second, LispObject third,
                            LispObject fourth, LispObject fifth)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[5];
    args[0] = first;
    args[1] = second;
    args[2] = third;
    args[3] = fourth;
    args[4] = fifth;
    return execute(args);
  }

  // Six args.
  public LispObject execute( LispObject first,
                            LispObject second, LispObject third,
                            LispObject fourth, LispObject fifth,
                            LispObject sixth)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[6];
    args[0] = first;
    args[1] = second;
    args[2] = third;
    args[3] = fourth;
    args[4] = fifth;
    args[5] = sixth;
    return execute(args);
  }

  // Seven args.
  public LispObject execute( LispObject first,
                            LispObject second, LispObject third,
                            LispObject fourth, LispObject fifth,
                            LispObject sixth, LispObject seventh)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[7];
    args[0] = first;
    args[1] = second;
    args[2] = third;
    args[3] = fourth;
    args[4] = fifth;
    args[5] = sixth;
    args[6] = seventh;
    return execute(args);
  }

  // Eight args.
  public LispObject execute( LispObject first,
                            LispObject second, LispObject third,
                            LispObject fourth, LispObject fifth,
                            LispObject sixth, LispObject seventh,
                            LispObject eighth)
    throws ConditionThrowable
  {
    LispObject[] args = new LispObject[8];
    args[0] = first;
    args[1] = second;
    args[2] = third;
    args[3] = fourth;
    args[4] = fifth;
    args[5] = sixth;
    args[6] = seventh;
    args[7] = eighth;
    return execute(args);
  }

  // Arg array.
  public LispObject execute(LispObject[] args)
    throws ConditionThrowable
  {
    return notImplemented();
  }

  // ### load-compiled-function
  private static final Primitive LOAD_COMPILED_FUNCTION =
      new Primitive("load-compiled-function", PACKAGE_SYS, true, "pathname")
  {
    @Override
    public LispObject execute(LispObject arg) throws ConditionThrowable
    {
      String namestring = null;
      if (arg instanceof Pathname)
        namestring = ((Pathname)arg).getNamestring();
      else if (arg instanceof AbstractString)
        namestring = arg.getStringValue();
      if (namestring != null)
        return loadCompiledFunction(namestring);
      return error(new LispError("Unable to load " + arg.writeToString()));
    }
  };

  // ### varlist
  private static final Primitive VARLIST =
      new Primitive("varlist", PACKAGE_SYS, false)
  {
    @Override
    public LispObject execute(LispObject arg) throws ConditionThrowable
    {
      if (arg instanceof Closure)
        return ((Closure)arg).getVariableList();
      return type_error(arg, SymbolConstants.COMPILED_FUNCTION);
    }
  };
}
