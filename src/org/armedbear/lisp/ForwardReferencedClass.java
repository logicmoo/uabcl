/*
 * ForwardReferencedClass.java
 *
 * Copyright (C) 2004-2005 Peter Graves
 * $Id: ForwardReferencedClass.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

public class ForwardReferencedClass extends LispClass
{
    public ForwardReferencedClass(Symbol name)
    {
        super(name);
    }

    @Override
    public LispObject typeOf()
    {
        return SymbolConstants.FORWARD_REFERENCED_CLASS;
    }

    @Override
    public LispObject classOf()
    {
        return StandardClass.FORWARD_REFERENCED_CLASS;
    }

    @Override
    public LispObject typep(LispObject type) throws ConditionThrowable
    {
        if (type == SymbolConstants.FORWARD_REFERENCED_CLASS)
            return T;
        if (type == StandardClass.FORWARD_REFERENCED_CLASS)
            return T;
        return super.typep(type);
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        StringBuffer sb =
            new StringBuffer(SymbolConstants.FORWARD_REFERENCED_CLASS.writeToString());
        if (symbol != null) {
            sb.append(' ');
            sb.append(symbol.writeToString());
        }
        return unreadableString(sb.toString());
    }

    // ### make-forward-referenced-class
    private static final Primitive MAKE_FORWARD_REFERENCED_CLASS =
        new Primitive("make-forward-referenced-class", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject arg)
            throws ConditionThrowable
        {
            if (arg instanceof Symbol) {
                Symbol name = (Symbol) arg;
                ForwardReferencedClass c = new ForwardReferencedClass(name);
                addLispClass(name, c);
                return c;
            }
                return error(new TypeError(arg.writeToString() +
                                            " is not a valid class name."));
        }
    };
}
