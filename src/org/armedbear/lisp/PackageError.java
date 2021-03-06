/*
 * PackageError.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: PackageError.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

public final class PackageError extends LispError
{
    public PackageError(LispObject initArgs) throws ConditionThrowable
    {
        super(StandardClass.PACKAGE_ERROR);
        initialize(initArgs);
    }

    @Override
    protected void initialize(LispObject initArgs) throws ConditionThrowable
    {
        super.initialize(initArgs);

        if (initArgs.isList() && initArgs.CAR().isString()) {
           setFormatControl(initArgs.CAR().getStringValue());
           // When printing an error string, presumably, if the string contains
           // a symbol, we'll want to complain about its full name, not the accessible
           // name, because it may omit an (important) package name part.
           // Two problems: (1) symbols can be contained in sublists
           //               (2) symbols may not be printed, but used otherwise.
           for (LispObject arg = initArgs.CDR(); arg != NIL; arg = arg.CDR()) {
              if (arg.CAR() instanceof Symbol)
                 arg.setCar(new SimpleString(((Symbol)arg.CAR()).getQualifiedName()));
           }
           setFormatArguments(initArgs.CDR());
           setPackage(NIL);

           return;
        }

        LispObject pkg = NIL;
        LispObject first, second;
        while (initArgs != NIL) {
            first = initArgs.CAR();
            initArgs = initArgs.CDR();
            second = initArgs.CAR();
            initArgs = initArgs.CDR();
            if (first == Keyword.PACKAGE)
                pkg = second;
        }
        setPackage(pkg);
    }

    public PackageError(String message) throws ConditionThrowable
    {
        super(StandardClass.PACKAGE_ERROR);
        setFormatControl(message);
    }

    @Override
    public LispObject typeOf()
    {
        return SymbolConstants.PACKAGE_ERROR;
    }

    @Override
    public LispObject classOf()
    {
        return StandardClass.PACKAGE_ERROR;
    }

    @Override
    public LispObject typep(LispObject type) throws ConditionThrowable
    {
        if (type == SymbolConstants.PACKAGE_ERROR)
            return T;
        if (type == StandardClass.PACKAGE_ERROR)
            return T;
        return super.typep(type);
    }

    public LispObject getPackage()
    {
    	final Layout layout = getLayout();
        Debug.assertTrue(layout != null);
        int index = layout.getSlotIndex(SymbolConstants.PACKAGE);
        Debug.assertTrue(index >= 0);
        return getSlot(index);
    }

    public void setPackage(LispObject pkg)
    {
    	final Layout layout = getLayout();
        Debug.assertTrue(layout != null);
        int index = layout.getSlotIndex(SymbolConstants.PACKAGE);
        Debug.assertTrue(index >= 0);
        setSlot(index,pkg);
    }
}
