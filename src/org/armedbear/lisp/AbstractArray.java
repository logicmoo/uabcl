/*
 * AbstractArray.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: AbstractArray.java 11711 2009-03-15 15:51:40Z ehuelsmann $
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

public abstract class AbstractArray extends AbstractLispObject implements LispArray
{
	abstract public LispObject AREF(int index) throws ConditionThrowable;
	 
    @Override
    public LispObject typep(LispObject type) throws ConditionThrowable
    {
        if (type == SymbolConstants.ARRAY)
            return T;
        if (type == BuiltInClass.ARRAY)
            return T;
        return super.typep(type);
    }

    @Override
    public boolean equalp(LispObject obj) throws ConditionThrowable
    {
        if (obj instanceof LispArray) {
            LispArray a = (LispArray) obj;
            if (getRank() != a.getRank())
                return false;
            for (int i = getRank(); i-- > 0;) {
                if (getDimension(i) != a.getDimension(i))
                    return false;
            }
            for (int i = getTotalSize(); i--> 0;) {
                if (!AREF(i).equalp(a.AREF(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    public boolean isDisplaced()
    {
        return false;
    }

    public LispObject arrayDisplacement() throws ConditionThrowable
    {
        return LispThread.currentThread().setValues(NIL, Fixnum.ZERO);
    }

    public boolean hasFillPointer()
    {
        return false;
    }

    public int getFillPointer() throws ConditionThrowable
    {
        noFillPointer();
        return -1; // Not reached.
    }

    public void setFillPointer(LispObject fillPointer) throws ConditionThrowable
    {
        setFillPointer(fillPointer.intValue());
    }

    public void setFillPointer(int fillPointer) throws ConditionThrowable
    {
        noFillPointer();
    }

    public boolean isAdjustable()
    {
        return true;
    }

    public abstract int getRank();

    public abstract LispObject getDimensions();

    public abstract int getDimension(int n) throws ConditionThrowable;

    public abstract LispObject getElementType();

    public abstract int getTotalSize();

    @Override
    public abstract void aset(int index, LispObject newValue)
        throws ConditionThrowable;

    // FIXME Detect overflow!
    protected static final int computeTotalSize(int[] dimensions)
    {
        int size = 1;
        for (int i = dimensions.length; i-- > 0;)
            size *= dimensions[i];
        return size;
    }

    public int getRowMajorIndex(LispObject[] subscripts)
        throws ConditionThrowable
    {
        int[] subs = new int[subscripts.length];
        for (int i = 0; i < subscripts.length; i++) {
        	subs[i] = Lisp.checkFixnum(subscripts[i]).intValue();
        }
        return getRowMajorIndex(subs);
    }

    public int getRowMajorIndex(int[] subscripts) throws ConditionThrowable
    {
        final int rank = getRank();
        if (rank != subscripts.length) {
            // ### i18n
            final String errorMsg =
                "Wrong number of subscripts (%d) for array of rank %d.";
            error(new ProgramError(String.format(errorMsg, subscripts.length, rank)));
        }
        int sum = 0;
        int size = 1;
        for (int i = rank; i-- > 0;) {
            final int dim = getDimension(i);
            final int lastSize = size;
            size *= dim;
            final int n = subscripts[i];
            if (n < 0 || n >= dim) {
                // ### i18n
                final String errorMsg =
                    "Invalid index %d for array %s.";
                error(new ProgramError(String.format(errorMsg, n, writeToString())));
            }
            sum += n * lastSize;
        }
        return sum;
    }

    public LispObject get(int[] subscripts) throws ConditionThrowable
    {
        return AREF(getRowMajorIndex(subscripts));
    }

    public void set(int[] subscripts, LispObject newValue)
        throws ConditionThrowable
    {
        aset(getRowMajorIndex(subscripts), newValue);
    }

    public abstract void fillVoid(LispObject obj) throws ConditionThrowable;

    public String writeToString(int[] dimv) throws ConditionThrowable
    {
        StringBuilder sb = new StringBuilder();
        LispThread thread = LispThread.currentThread();
        LispObject printReadably = SymbolConstants.PRINT_READABLY.symbolValue(thread);
        if (printReadably != NIL || SymbolConstants.PRINT_ARRAY.symbolValue(thread) != NIL) {
            int maxLevel = Integer.MAX_VALUE;
            if (printReadably != NIL) {
                for (int i = 0; i < dimv.length - 1; i++) {
                    if (dimv[i] == 0) {
                        for (int j = i + 1; j < dimv.length; j++) {
                            if (dimv[j] != 0) {
                                error(new PrintNotReadable(list(Keyword.OBJECT,
                                                                  this)));
                                return null; // Not reached.
                            }
                        }
                    }
                }
            } else {
                LispObject printLevel = SymbolConstants.PRINT_LEVEL.symbolValue(thread);
                if (printLevel  instanceof Fixnum)
                    maxLevel = printLevel.intValue();
            }
            LispObject currentPrintLevel =
                _CURRENT_PRINT_LEVEL_.symbolValue(thread);
            int currentLevel = currentPrintLevel.intValue();
            if (currentLevel >= maxLevel)
                return "#";
            sb.append('#');
            sb.append(dimv.length);
            sb.append('A');
            appendContents(dimv, 0, sb, thread);
            return sb.toString();
        }
        sb.append('(');
        if (this instanceof SimpleArray_T)
            sb.append("SIMPLE-");
        sb.append("ARRAY " + getElementType().writeToString() + " (");
        for (int i = 0; i < dimv.length; i++) {
            sb.append(dimv[i]);
            if (i < dimv.length - 1)
                sb.append(' ');
        }
        sb.append("))");
        return unreadableString(sb.toString());
    }

    // Helper for writeToString().
    private void appendContents(int[] dimensions, int index, StringBuilder sb,
                                LispThread thread)
        throws ConditionThrowable
    {
        if (dimensions.length == 0) {
            if (SymbolConstants.PRINT_CIRCLE.symbolValue(thread) != NIL) {
                StringOutputStream stream = new StringOutputStream();
                thread.execute(SymbolConstants.OUTPUT_OBJECT.getSymbolFunction(),
                               AREF(index), stream);
                sb.append(stream.getStringOutputString().getStringValue());
            } else
                sb.append(AREF(index).writeToString());
        } else {
            final LispObject printReadably =
                SymbolConstants.PRINT_READABLY.symbolValue(thread);
            int maxLength = Integer.MAX_VALUE;
            int maxLevel = Integer.MAX_VALUE;
            if (printReadably == NIL) {
                final LispObject printLength =
                    SymbolConstants.PRINT_LENGTH.symbolValue(thread);
                if (printLength  instanceof Fixnum)
                    maxLength = printLength.intValue();
                final LispObject printLevel =
                    SymbolConstants.PRINT_LEVEL.symbolValue(thread);
                if (printLevel  instanceof Fixnum)
                    maxLevel = printLevel.intValue();
            }
            LispObject currentPrintLevel =
                _CURRENT_PRINT_LEVEL_.symbolValue(thread);
            int currentLevel = currentPrintLevel.intValue();
            if (currentLevel < maxLevel) {
                SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
                thread.bindSpecial(_CURRENT_PRINT_LEVEL_, currentPrintLevel.incr());
                try {
                    sb.append('(');
                    int[] dims = new int[dimensions.length - 1];
                    for (int i = 1; i < dimensions.length; i++)
                        dims[i-1] = dimensions[i];
                    int count = 1;
                    for (int i = 0; i < dims.length; i++)
                        count *= dims[i];
                    final int length = dimensions[0];
                    final int limit = Math.min(length, maxLength);
                    for (int i = 0; i < limit; i++) {
                        appendContents(dims, index, sb, thread);
                        if (i < limit - 1 || limit < length)
                            sb.append(' ');
                        index += count;
                    }
                    if (limit < length)
                        sb.append("...");
                    sb.append(')');
                }
                finally {
                    thread.lastSpecialBinding = lastSpecialBinding;
                }
            } else
                sb.append('#');
        }
    }

    // For EQUALP hash tables.
    @Override
    public int psxhash()
    {
        try {
            long result = 128387; // Chosen at random.
            final int rank = getRank();
            int limit = rank < 4 ? rank : 4;
            for (int i = 0; i < limit; i++)
                result = mix(result, getDimension(i));
            final int length = getTotalSize();
            limit = length < 4 ? length : 4;
            for (int i = 0; i < length; i++)
                result = mix(result, AREF(i).psxhash());
            return (int) (result & 0x7fffffff);
        }
        catch (Throwable t) {
            // Shouldn't happen.
            Debug.trace(t);
            return 0;
        }
    }

    /** Returns a newly allocated array or the current array with
     * adjusted dimensions.
     *
     * @param dims
     * @param initialElement @c null if none
     * @param initialContents @c null if none
     * @return @c this or a new array
     * @throws org.armedbear.lisp.ConditionThrowable
     */
    public abstract LispArray adjustArray(int[] dims,
                                              LispObject initialElement,
                                              LispObject initialContents)
        throws ConditionThrowable;

    /**
     *
     * @param dims
     * @param displacedTo
     * @param displacement
     * @return
     * @throws org.armedbear.lisp.ConditionThrowable
     */
    public abstract LispArray adjustArray(int[] dims,
                                              LispArray displacedTo,
                                              int displacement)
        throws ConditionThrowable;

	  
    abstract public LispObject typeOf();
	  
    // errors for arrays
	public LispObject badRowMajorIndex(int index) {
		return error(new TypeError("Bad row major index " + index + " in type " + typeOf() + " for " + this));		
	}

	public void badInitialContents() {
		error(new LispError("Bad initial contents for array."));		
	}
}
