/*
 * logorc1.java
 *
 * Copyright (C) 2003-2004 Peter Graves
 * $Id: logorc1.java 11714 2009-03-23 20:05:37Z ehuelsmann $
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

import java.math.BigInteger;

// ### logorc1
// logorc1 integer-1 integer-2 => result-integer
// or complement of integer-1 with integer-2
public final class logorc1 extends Primitive
{
    private logorc1()
    {
        super("logorc1", "integer-1 integer-2");
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
    {
        if (first  instanceof Fixnum) {
            if (second  instanceof Fixnum)
                return Fixnum.makeFixnum(~first.intValue() |
                                  second.intValue());
            if (second  instanceof Bignum) {
                BigInteger n1 = first.bigIntegerValue();
                BigInteger n2 = second.bigIntegerValue();
                return number(n1.not().or(n2));
            }
            return error(new TypeError(second, SymbolConstants.INTEGER));
        }
        if (first  instanceof Bignum) {
            BigInteger n1 = first.bigIntegerValue();
            if (second  instanceof Fixnum) {
                BigInteger n2 = second.bigIntegerValue();
                return number(n1.not().or(n2));
            }
            if (second  instanceof Bignum) {
                BigInteger n2 = second.bigIntegerValue();
                return number(n1.not().or(n2));
            }
            return error(new TypeError(second, SymbolConstants.INTEGER));
        }
        return error(new TypeError(first, SymbolConstants.INTEGER));
    }

    private static final Primitive LOGORC1 = new logorc1();
}
