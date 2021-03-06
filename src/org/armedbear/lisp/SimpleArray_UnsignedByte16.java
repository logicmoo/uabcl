/*
 * SimpleArray_UnsignedByte16.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: SimpleArray_UnsignedByte16.java 11714 2009-03-23 20:05:37Z ehuelsmann $
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

public final class SimpleArray_UnsignedByte16 extends AbstractArray
{
    private final int[] dimv;
    private final int totalSize;
    private final int[] data;

    public SimpleArray_UnsignedByte16(int[] dimv)
    {
        this.dimv = dimv;
        totalSize = computeTotalSize(dimv);
        data = new int[totalSize];
    }

    public SimpleArray_UnsignedByte16(int[] dimv, LispObject initialContents)
        throws ConditionThrowable
    {
        this.dimv = dimv;
        final int rank = dimv.length;
        LispObject rest = initialContents;
        for (int i = 0; i < rank; i++) {
            dimv[i] = rest.size();
            rest = rest.elt(0);
        }
        totalSize = computeTotalSize(dimv);
        data = new int[totalSize];
        setInitialContents(0, dimv, initialContents, 0);
    }

    public SimpleArray_UnsignedByte16(int rank, LispObject initialContents)
        throws ConditionThrowable
    {
        if (rank < 2)
            Debug.assertTrue(false);
        dimv = new int[rank];
        LispObject rest = initialContents;
        for (int i = 0; i < rank; i++) {
            dimv[i] = rest.size();
            if (rest == NIL || rest.size() == 0)
                break;
            rest = rest.elt(0);
        }
        totalSize = computeTotalSize(dimv);
        data = new int[totalSize];
        setInitialContents(0, dimv, initialContents, 0);
    }

    private int setInitialContents(int axis, int[] dims, LispObject contents,
                                   int index)
        throws ConditionThrowable
    {
        if (dims.length == 0) {
            try {
                data[index] = coerceLispObjectToJavaByte(contents);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                badInitialContents();
                return -1;
            }
            ++index;
        } else {
            int dim = dims[0];
            if (dim != contents.size()) {
                badInitialContents();
                return -1;
            }
            int[] newDims = new int[dims.length-1];
            for (int i = 1; i < dims.length; i++)
                newDims[i-1] = dims[i];
            if (contents.isList()) {
                for (int i = contents.size();i-- > 0;) {
                    LispObject content = contents.CAR();
                    index =
                        setInitialContents(axis + 1, newDims, content, index);
                    contents = contents.CDR();
                }
            } else {
                LispVector v = checkVector(contents);
                final int length = v.size();
                for (int i = 0; i < length; i++) {
                    LispObject content = v.AREF(i);
                    index =
                        setInitialContents(axis + 1, newDims, content, index);
                }
            }
        }
        return index;
    }

    @Override
    public LispObject typeOf()
    {
        return list(SymbolConstants.SIMPLE_ARRAY, UNSIGNED_BYTE_16, getDimensions());
    }

    @Override
    public LispObject classOf()
    {
        return BuiltInClass.SIMPLE_ARRAY;
    }

    @Override
    public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
    {
        if (typeSpecifier == SymbolConstants.SIMPLE_ARRAY)
            return T;
        if (typeSpecifier == BuiltInClass.SIMPLE_ARRAY)
            return T;
        return super.typep(typeSpecifier);
    }

    @Override
    public int getRank()
    {
        return dimv.length;
    }

    @Override
    public LispObject getDimensions()
    {
        LispObject result = NIL;
        for (int i = dimv.length; i-- > 0;)
            result = makeCons(Fixnum.makeFixnum(dimv[i]), result);
        return result;
    }

    @Override
    public int getDimension(int n) throws ConditionThrowable
    {
        try {
            return dimv[n];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            error(new TypeError("Bad array dimension " + n + "."));
            return -1;
        }
    }

    @Override
    public LispObject getElementType()
    {
        return UNSIGNED_BYTE_16;
    }

    @Override
    public int getTotalSize()
    {
        return totalSize;
    }

    @Override
    public boolean isAdjustable()
    {
        return false;
    }

    @Override
    public int aref(int index) throws ConditionThrowable
    {
        try {
            return data[index];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            badRowMajorIndex(index);
            // Not reached.
            return 0;
        }
    }

    @Override
    public LispObject AREF(int index) throws ConditionThrowable
    {
        try {
            return Fixnum.makeFixnum(data[index]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return badRowMajorIndex(index);
        }
    }

    @Override
    public void aset(int index, LispObject obj) throws ConditionThrowable
    {
        try {
            data[index] = obj.intValue();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            badRowMajorIndex(index);
        }
    }

    @Override
    public int getRowMajorIndex(int[] subscripts) throws ConditionThrowable
    {
        final int rank = dimv.length;
        if (rank != subscripts.length) {
            StringBuffer sb = new StringBuffer("Wrong number of subscripts (");
            sb.append(subscripts.length);
            sb.append(") for array of rank ");
            sb.append(rank);
            sb.append('.');
            error(new ProgramError(sb.toString()));
        }
        int sum = 0;
        int size = 1;
        for (int i = rank; i-- > 0;) {
            final int dim = dimv[i];
            final int lastSize = size;
            size *= dim;
            int n = subscripts[i];
            if (n < 0 || n >= dim) {
                StringBuffer sb = new StringBuffer("Invalid index ");
                sb.append(n);
                sb.append(" for array ");
                sb.append(this);
                sb.append('.');
                error(new ProgramError(sb.toString()));
            }
            sum += n * lastSize;
        }
        return sum;
    }

    @Override
    public LispObject get(int[] subscripts) throws ConditionThrowable
    {
        try {
            return Fixnum.makeFixnum(data[getRowMajorIndex(subscripts)]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return error(new TypeError("Bad row major index " +
                                        getRowMajorIndex(subscripts) + "."));
        }
    }

    @Override
    public void set(int[] subscripts, LispObject obj)
        throws ConditionThrowable
    {
        try {
            data[getRowMajorIndex(subscripts)] = obj.intValue();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            error(new TypeError("Bad row major index " +
                                 getRowMajorIndex(subscripts) + "."));
        }
    }

    @Override
    public void fillVoid(LispObject obj) throws ConditionThrowable
    {
        int n = obj.intValue();
        for (int i = totalSize; i-- > 0;)
            data[i] = n;
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        if (SymbolConstants.PRINT_READABLY.symbolValue() != NIL) {
            error(new PrintNotReadable(list(Keyword.OBJECT, this)));
            // Not reached.
            return null;
        }
        return writeToString(dimv);
    }

    public LispArray adjustArray(int[] dimv, LispObject initialElement,
                                     LispObject initialContents)
        throws ConditionThrowable
    {
        if (initialContents != null)
            return new SimpleArray_UnsignedByte16(dimv, initialContents);
        for (int i = 0; i < dimv.length; i++) {
            if (dimv[i] != this.dimv[i]) {
                SimpleArray_UnsignedByte16 newArray =
                    new SimpleArray_UnsignedByte16(dimv);
                if (initialElement != null)
                    newArray.fillVoid(initialElement);
                copyArray(this, newArray);
                return newArray;
            }
        }
        // New dimensions are identical to old dimensions.
        return this;
    }

    // Copy a1 to a2 for index tuples that are valid for both arrays.
    private static void copyArray(LispArray a1, LispArray a2)
        throws ConditionThrowable
    {
        Debug.assertTrue(a1.getRank() == a2.getRank());
        int[] subscripts = new int[a1.getRank()];
        int axis = 0;
        copySubArray(a1, a2, subscripts, axis);
    }

    private static void copySubArray(LispArray a1, LispArray a2,
                                     int[] subscripts, int axis)
        throws ConditionThrowable
    {
        if (axis < subscripts.length) {
            final int limit =
                Math.min(a1.getDimension(axis), a2.getDimension(axis));
            for (int i = 0; i < limit; i++) {
                subscripts[axis] = i;
                copySubArray(a1, a2, subscripts, axis + 1);
            }
        } else {
            int i1 = a1.getRowMajorIndex(subscripts);
            int i2 = a2.getRowMajorIndex(subscripts);
            a2.aset(i2, a1.AREF(i1));
        }
    }

    public LispArray adjustArray(int[] dimv, LispArray displacedTo,
                                     int displacement)
    {
        return new ComplexArray(dimv, displacedTo, displacement);
    }
}
