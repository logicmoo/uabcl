/*
 * SpecialOperator.java
 *
 * Copyright (C) 2002-2005 Peter Graves
 * $Id: SpecialOperator.java 12079 2009-07-31 19:45:54Z ehuelsmann $
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
public class SpecialOperator extends Operator
{
    private int callCount;
    private int hotCount;

    public SpecialOperator(Symbol symbol)
    {
        symbol.setSymbolFunction(this);
        setLambdaName(symbol);
    }

    public SpecialOperator(Symbol symbol, String arglist)
    {
        symbol.setSymbolFunction(this);
        setLambdaName(symbol);
        setLambdaList(new SimpleString(arglist));
    }

    public SpecialOperator(String name, LispPackage pkg, boolean exported,
                           String arglist)
    {
        try {
            Symbol symbol;
            if (exported)
                symbol = pkg.internAndExport(name.toUpperCase());
            else
                symbol = pkg.intern(name.toUpperCase());
            symbol.setSymbolFunction(this);
            setLambdaName(symbol);
            setLambdaList(new SimpleString(arglist));
        }
        catch (ConditionThrowable t) {
            Debug.assertTrue(false);
        }
    }

    @Override
    public LispObject execute() throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject arg) throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth, LispObject sixth)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth, LispObject sixth,
                              LispObject seventh)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject first, LispObject second,
                              LispObject third, LispObject fourth,
                              LispObject fifth, LispObject sixth,
                              LispObject seventh, LispObject eighth)
        throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public LispObject execute(LispObject[] args) throws ConditionThrowable
    {
        return error(new UndefinedFunction(getLambdaName()));
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        StringBuffer sb = new StringBuffer("#<SPECIAL-OPERATOR ");
        sb.append(lambdaName.writeToString());
        sb.append(">");
        return sb.toString();
    }

    // Profiling.
    @Override
    public final int getCallCount()
    {
        return callCount;
    }

    @Override
    public final void setCallCount(int n)
    {
        callCount = n;
    }

    @Override
    public final void incrementCallCount()
    {
        ++callCount;
    }

    @Override
    public final int getHotCount()
    {
        return hotCount;
    }

    @Override
    public final void setHotCount(int n)
    {
        callCount = n;
    }

    @Override
    public final void incrementHotCount()
    {
        ++hotCount;
    }
}
