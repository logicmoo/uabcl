/*
 * StringFunctions.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: StringFunctions.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

public final class StringFunctions extends LispFile
{
    // ### %string=
    // Case sensitive.
    private static final Primitive _STRING_EQUAL =
        new Primitive("%string=", PACKAGE_SYS, false)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third, LispObject fourth,
                                  LispObject fifth, LispObject sixth)
            throws ConditionThrowable
        {
            char[] array1 = first.STRING().getStringChars();
            char[] array2 = second.STRING().getStringChars();
            int start1, end1, start2, end2;
            start1 = third.intValue();
            if (fourth == NIL) {
                end1 = array1.length;
            } else {
                end1 = fourth.intValue();
            }
            start2 = fifth.intValue();
            if (sixth == NIL) {
                end2 = array2.length;
            } else {
                end2 = sixth.intValue();
            }
            if ((end1 - start1) != (end2 - start2))
                return NIL;
            try {
                for (int i = start1, j = start2; i < end1; i++, j++) {
                    if (array1[i] != array2[j])
                        return NIL;
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                // Shouldn't happen.
                Debug.trace(e);
                return NIL;
            }
            return T;
        }
    };

    // ### %%string=
    // Case sensitive.
    private static final Primitive __STRING_EQUAL =
        new Primitive("%%string=", PACKAGE_SYS, false)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
            char[] array1 = first.STRING().getStringChars();
            char[] array2 = second.STRING().getStringChars();
            if (array1.length != array2.length)
                return NIL;
            for (int i = array1.length; i-- > 0;) {
                if (array1[i] != array2[i])
                    return NIL;
            }
            return T;
        }
    };

    // ### %string/=
    // Case sensitive.
    private static final Primitive _STRING_NOT_EQUAL =
        new Primitive("%string/=", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    if (j == end2)
                        return NIL; // Strings are identical.
                    return Fixnum.makeFixnum(i);
                }
                if (j == end2) {
                    // Reached end of string2 before end of string1.
                    return Fixnum.makeFixnum(i);
                }
                if (array1[i] != array2[j])
                    return Fixnum.makeFixnum(i);
                ++i;
                ++j;
            }
        }
    };

    // ### %string-equal
    // Case insensitive.
    private static final Primitive _STRING_EQUAL_IGNORE_CASE =
        new Primitive("%string-equal", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third, LispObject fourth,
                                  LispObject fifth, LispObject sixth)
            throws ConditionThrowable
        {
            char[] array1 = first.STRING().getStringChars();
            char[] array2 = second.STRING().getStringChars();
            int start1 = third.intValue();
            int end1 = fourth.intValue();
            int start2 = fifth.intValue();
            int end2 = sixth.intValue();
            if ((end1 - start1) != (end2 - start2))
                return NIL;
            int i, j;
            for (i = start1, j = start2; i < end1; i++, j++) {
                char c1 = array1[i];
                char c2 = array2[j];
                if (c1 == c2)
                    continue;
                if (LispCharacter.toUpperCase(c1) == LispCharacter.toUpperCase(c2))
                    continue;
                if (LispCharacter.toLowerCase(c1) == LispCharacter.toLowerCase(c2))
                    continue;
                return NIL;
            }
            return T;
        }
    };

    // ### %string-not-equal
    // Case sensitive.
    private static final Primitive _STRING_NOT_EQUAL_IGNORE_CASE =
        new Primitive("%string-not-equal", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    if (j == end2)
                        return NIL; // Strings are identical.
                    return Fixnum.makeFixnum(i);
                }
                if (j == end2) {
                    // Reached end of string2.
                    return Fixnum.makeFixnum(i);
                }
                char c1 = array1[i];
                char c2 = array2[j];
                if (c1 == c2 ||
                    LispCharacter.toUpperCase(c1) == LispCharacter.toUpperCase(c2) ||
                    LispCharacter.toLowerCase(c1) == LispCharacter.toLowerCase(c2))
                {
                    ++i;
                    ++j;
                    continue;
                }
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string<
    // Case sensitive.
    private static final Primitive _STRING_LESS_THAN =
        new Primitive("%string<", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    if (j == end2)
                        return NIL; // Strings are identical.
                    return Fixnum.makeFixnum(i);
                }
                if (j == end2) {
                    // Reached end of string2.
                    return NIL;
                }
                char c1 = array1[i];
                char c2 = array2[j];
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 < c2)
                    return Fixnum.makeFixnum(i);
                // c1 > c2
                return NIL;
            }
        }
    };

    // ### %string<=
    // Case sensitive.
    private static final Primitive _STRING_GREATER_THAN =
        new Primitive("%string>", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    return NIL;
                }
                if (j == end2) {
                    // Reached end of string2.
                    return Fixnum.makeFixnum(i);
                }
                char c1 = array1[i];
                char c2 = array2[j];
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 < c2)
                    return NIL;
                // c1 > c2
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string<=
    // Case sensitive.
    private static final Primitive _STRING_LE =
        new Primitive("%string<=", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    return Fixnum.makeFixnum(i);
                }
                if (j == end2) {
                    // Reached end of string2.
                    return NIL;
                }
                char c1 = array1[i];
                char c2 = array2[j];
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 > c2)
                    return NIL;
                // c1 < c2
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string<=
    // Case sensitive.
    private static final Primitive _STRING_GE =
        new Primitive("%string>=", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    if (j == end2)
                        return Fixnum.makeFixnum(i); // Strings are identical.
                    return NIL;
                }
                if (j == end2) {
                    // Reached end of string2.
                    return Fixnum.makeFixnum(i);
                }
                char c1 = array1[i];
                char c2 = array2[j];
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 < c2)
                    return NIL;
                // c1 > c2
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string-lessp
    // Case insensitive.
    private static final Primitive _STRING_LESSP =
        new Primitive("%string-lessp", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    if (j == end2)
                        return NIL; // Strings are identical.
                    return Fixnum.makeFixnum(i);
                }
                if (j == end2) {
                    // Reached end of string2.
                    return NIL;
                }
                char c1 = LispCharacter.toUpperCase(array1[i]);
                char c2 = LispCharacter.toUpperCase(array2[j]);
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 > c2)
                    return NIL;
                // c1 < c2
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string-greaterp
    // Case insensitive.
    private static final Primitive _STRING_GREATERP =
        new Primitive("%string-greaterp", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    return NIL;
                }
                if (j == end2) {
                    // Reached end of string2.
                    return Fixnum.makeFixnum(i);
                }
                char c1 = LispCharacter.toUpperCase(array1[i]);
                char c2 = LispCharacter.toUpperCase(array2[j]);
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 < c2)
                    return NIL;
                // c1 > c2
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string-not-lessp
    // Case insensitive.
    private static final Primitive _STRING_NOT_LESSP =
        new Primitive("%string-not-lessp", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    if (j == end2)
                        return Fixnum.makeFixnum(i); // Strings are identical.
                    return NIL;
                }
                if (j == end2) {
                    // Reached end of string2.
                    return Fixnum.makeFixnum(i);
                }
                char c1 = LispCharacter.toUpperCase(array1[i]);
                char c2 = LispCharacter.toUpperCase(array2[j]);
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 > c2)
                    return Fixnum.makeFixnum(i);
                // c1 < c2
                return NIL;
            }
        }
    };

    // ### %string-not-greaterp
    // Case insensitive.
    private static final Primitive _STRING_NOT_GREATERP =
        new Primitive("%string-not-greaterp", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject[] args) throws ConditionThrowable
        {
            if (args.length != 6)
                return error(new WrongNumberOfArgumentsException(this));
            char[] array1 = args[0].STRING().getStringChars();
            char[] array2 = args[1].STRING().getStringChars();
            int start1 = args[2].intValue();
            int end1 = args[3].intValue();
            int start2 = args[4].intValue();
            int end2 = args[5].intValue();
            int i = start1;
            int j = start2;
            while (true) {
                if (i == end1) {
                    // Reached end of string1.
                    return Fixnum.makeFixnum(i);
                }
                if (j == end2) {
                    // Reached end of string2.
                    return NIL;
                }
                char c1 = LispCharacter.toUpperCase(array1[i]);
                char c2 = LispCharacter.toUpperCase(array2[j]);
                if (c1 == c2) {
                    ++i;
                    ++j;
                    continue;
                }
                if (c1 > c2)
                    return NIL;
                // c1 < c2
                return Fixnum.makeFixnum(i);
            }
        }
    };

    // ### %string-upcase
    private static final Primitive _STRING_UPCASE =
        new Primitive("%string-upcase", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            LispObject s = first.STRING();
            final int length = s.size();
            int start = (int) second.intValue();
            if (start < 0 || start > length)
                return error(new TypeError("Invalid start position " + start + "."));
            int end;
            if (third == NIL)
                end = length;
            else
                end = (int) third.intValue();
            if (end < 0 || end > length)
                return error(new TypeError("Invalid end position " + start + "."));
            if (start > end)
                return error(new TypeError("Start (" + start + ") is greater than end (" + end + ")."));
            FastStringBuffer sb = new FastStringBuffer(length);
            char[] array = s.getStringChars();
            int i;
            for (i = 0; i < start; i++)
                sb.append(array[i]);
            for (i = start; i < end; i++)
                sb.append(LispCharacter.toUpperCase(array[i]));
            for (i = end; i < length; i++)
                sb.append(array[i]);
            return new SimpleString(sb);
        }
    };

    // ### %string-downcase
    private static final Primitive _STRING_DOWNCASE =
        new Primitive("%string-downcase", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third) throws
        ConditionThrowable
        {
            LispObject s = first.STRING();
            final int length = s.size();
            int start = (int) second.intValue();
            if (start < 0 || start > length)
                return error(new TypeError("Invalid start position " + start + "."));
            int end;
            if (third == NIL)
                end = length;
            else
                end = (int) third.intValue();
            if (end < 0 || end > length)
                return error(new TypeError("Invalid end position " + start + "."));
            if (start > end)
                return error(new TypeError("Start (" + start + ") is greater than end (" + end + ")."));
            FastStringBuffer sb = new FastStringBuffer(length);
            char[] array = s.getStringChars();
            int i;
            for (i = 0; i < start; i++)
                sb.append(array[i]);
            for (i = start; i < end; i++)
                sb.append(LispCharacter.toLowerCase(array[i]));
            for (i = end; i < length; i++)
                sb.append(array[i]);
            return new SimpleString(sb);
        }
    };

    // ### %string-capitalize
    private static final Primitive _STRING_CAPITALIZE=
        new Primitive("%string-capitalize", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            LispObject s = first.STRING();
            final int length = s.size();
            int start = (int) second.intValue();
            if (start < 0 || start > length)
                return error(new TypeError("Invalid start position " + start + "."));
            int end;
            if (third == NIL)
                end = length;
            else
                end = (int) third.intValue();
            if (end < 0 || end > length)
                return error(new TypeError("Invalid end position " + start + "."));
            if (start > end)
                return error(new TypeError("Start (" + start + ") is greater than end (" + end + ")."));
            FastStringBuffer sb = new FastStringBuffer(length);
            char[] array = s.getStringChars();
            boolean lastCharWasAlphanumeric = false;
            int i;
            for (i = 0; i < start; i++)
                sb.append(array[i]);
            for (i = start; i < end; i++) {
                char c = array[i];
                if (Character.isLowerCase(c)) {
                    sb.append(lastCharWasAlphanumeric ? c : LispCharacter.toUpperCase(c));
                    lastCharWasAlphanumeric = true;
                } else if (Character.isUpperCase(c)) {
                    sb.append(lastCharWasAlphanumeric ? LispCharacter.toLowerCase(c) : c);
                    lastCharWasAlphanumeric = true;
                } else {
                    sb.append(c);
                    lastCharWasAlphanumeric = Character.isDigit(c);
                }
            }
            for (i = end; i < length; i++)
                sb.append(array[i]);
            return new SimpleString(sb);
        }
    };

    // ### %nstring-upcase
    private static final Primitive _NSTRING_UPCASE =
        new Primitive("%nstring-upcase", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            final AbstractString string = checkString(first);
            final int length = string.size();
            int start = (int) second.intValue();
            if (start < 0 || start > length)
                return error(new TypeError("Invalid start position " + start + "."));
            int end;
            if (third == NIL)
                end = length;
            else
                end = (int) third.intValue();
            if (end < 0 || end > length)
                return error(new TypeError("Invalid end position " + start + "."));
            if (start > end)
                return error(new TypeError("Start (" + start + ") is greater than end (" + end + ")."));
            for (int i = start; i < end; i++)
                string.setCharAt(i, LispCharacter.toUpperCase(string.charAt(i)));
            return string;
        }
    };

    // ### %nstring-downcase
    private static final Primitive _NSTRING_DOWNCASE =
        new Primitive("%nstring-downcase", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            final AbstractString string = checkString(first);
            final int length = string.size();
            int start = (int) second.intValue();
            if (start < 0 || start > length)
                return error(new TypeError("Invalid start position " + start + "."));
            int end;
            if (third == NIL)
                end = length;
            else
                end = (int) third.intValue();
            if (end < 0 || end > length)
                return error(new TypeError("Invalid end position " + start + "."));
            if (start > end)
                return error(new TypeError("Start (" + start + ") is greater than end (" + end + ")."));
            for (int i = start; i < end; i++)
                string.setCharAt(i, LispCharacter.toLowerCase(string.charAt(i)));
            return string;
        }
    };

    // ### %nstring-capitalize
    private static final Primitive _NSTRING_CAPITALIZE =
        new Primitive("%nstring-capitalize", PACKAGE_SYS, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            AbstractString string = checkString(first);
            final int length = string.size();
            int start = (int) second.intValue();
            if (start < 0 || start > length)
                return error(new TypeError("Invalid start position " + start + "."));
            int end;
            if (third == NIL)
                end = length;
            else
                end = (int) third.intValue();
            if (end < 0 || end > length)
                return error(new TypeError("Invalid end position " + start + "."));
            if (start > end)
                return error(new TypeError("Start (" + start + ") is greater than end (" + end + ")."));
            boolean lastCharWasAlphanumeric = false;
            for (int i = start; i < end; i++) {
                char c = string.charAt(i);
                if (Character.isLowerCase(c)) {
                    if (!lastCharWasAlphanumeric)
                        string.setCharAt(i, LispCharacter.toUpperCase(c));
                    lastCharWasAlphanumeric = true;
                } else if (Character.isUpperCase(c)) {
                    if (lastCharWasAlphanumeric)
                        string.setCharAt(i, LispCharacter.toLowerCase(c));
                    lastCharWasAlphanumeric = true;
                } else
                    lastCharWasAlphanumeric = Character.isDigit(c);
            }
            return string;
        }
    };

    // ### stringp
    public static final Primitive STRINGP = new Primitive("stringp", "object")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return arg.STRINGP();
        }
    };

    // ### simple-string-p
    public static final Primitive SIMPLE_STRING_P =
        new Primitive("simple-string-p", "object")
    {
        @Override
        public LispObject execute(LispObject arg) throws ConditionThrowable
        {
            return arg.SIMPLE_STRING_P();
        }
    };

    // ### %make-string
    // %make-string size initial-element element-type => string
    // Returns a simple string.
    private static final Primitive _MAKE_STRING =
        new Primitive("%make-string", PACKAGE_SYS, false)
    {
        @Override
        public LispObject execute(LispObject size, LispObject initialElement,
                                  LispObject elementType)
            throws ConditionThrowable
        {
            final int n = size.intValue();
            if (n < 0 || n >= ARRAY_DIMENSION_MAX) {
                FastStringBuffer sb = new FastStringBuffer();
                sb.append("The size specified for this string (");
                sb.append(n);
                sb.append(')');
                if (n >= ARRAY_DIMENSION_MAX) {
                    sb.append(" is >= ARRAY-DIMENSION-LIMIT (");
                    sb.append(ARRAY_DIMENSION_MAX);
                    sb.append(").");
                } else
                    sb.append(" is negative.");
                return error(new LispError(sb.toString()));
            }
            // Ignore elementType.
            SimpleString string = new SimpleString(n);
            if (initialElement != NIL) {
                // Initial element was specified.
                char c = checkCharacter(initialElement).charValue();
                string.fill(c);
            }
            return string;
        }
    };

    // ### char
    private static final Primitive CHAR =
        new Primitive(SymbolConstants.CHAR, "string index")
    {
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
                return first.CHAR(second.intValue());
        }
    };

    // ### schar
    private static final Primitive SCHAR =
        new Primitive(SymbolConstants.SCHAR, "string index")
    {
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
            return first.SCHAR(second.intValue());
        }
    };

    // ### set-char
    private static final Primitive SET_CHAR =
        new Primitive(SymbolConstants.SET_CHAR, "string index character")
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            checkString(first).setCharAt(second.intValue(),
                    third.charValue());
            return third;
        }
    };

    // ### set-schar
    private static final Primitive SET_SCHAR =
        new Primitive(SymbolConstants.SET_SCHAR, "string index character")
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            if (first instanceof SimpleString) {
                ((SimpleString)first).setCharAt(second.intValue(),
                                                third.charValue());
                return third;
            }
            return type_error(first, SymbolConstants.SIMPLE_STRING);
        }
    };

    // ### string-position
    private static final Primitive STRING_POSITION =
        new Primitive("string-position", PACKAGE_EXT, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second,
                                  LispObject third)
            throws ConditionThrowable
        {
            char c = first.charValue();
            AbstractString string = checkString(second);
            int start = third.intValue();
            for (int i = start, limit = string.size(); i < limit; i++) {
                if (string.charAt(i) == c)
                    return number(i);
            }
            return NIL;
        }
    };

    // ### string-find
    private static final Primitive STRING_FIND =
        new Primitive("string-find", PACKAGE_EXT, true, "char string")
    {
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
            if (first instanceof LispCharacter) {
                final char c = ((LispCharacter)first).value;
                AbstractString string = Lisp.checkString(second);
                final int limit = string.size();
                for (int i = 0; i < limit; i++) {
                    if (string.charAt(i) == c)
                        return first;
                }
            }
            return NIL;
        }
    };

    // ### simple-string-search pattern string => position
    // Searches string for a substring that matches pattern.
    private static final Primitive SIMPLE_STRING_SEARCH =
        new Primitive("simple-string-search", PACKAGE_EXT, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
            // FIXME Don't call getStringValue() here! (Just look at the chars.)
            int index = second.getStringValue().indexOf(first.getStringValue());
            return index >= 0 ? Fixnum.makeFixnum(index) : NIL;
        }
    };

    // ### simple-string-fill string character => string
    private static final Primitive STRING_FILL =
        new Primitive("simple-string-fill", PACKAGE_EXT, true)
    {
        @Override
        public LispObject execute(LispObject first, LispObject second)
            throws ConditionThrowable
        {
            if(first instanceof AbstractString) {
                AbstractString s = (AbstractString) first;
                s.fill(second.charValue());
                return first;
            }
            return type_error(first, SymbolConstants.SIMPLE_STRING);
        }
    };
    
}
