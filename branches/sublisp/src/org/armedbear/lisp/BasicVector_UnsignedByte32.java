/*
 * BasicVector_UnsignedByte32.java
 *
 * Copyright (C) 2002-2006 Peter Graves
 * $Id: BasicVector_UnsignedByte32.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

// A basic vector is a specialized vector that is not displaced to another
// array, has no fill pointer, and is not expressly adjustable.
public final class BasicVector_UnsignedByte32 extends AbstractVector
{
  private int capacity;

  private long[] elements;

  public BasicVector_UnsignedByte32(int capacity)
  {
    elements = new long[capacity];
    this.capacity = capacity;
  }

  public BasicVector_UnsignedByte32(LispObject[] array)
    throws ConditionThrowable
  {
    capacity = array.length;
    elements = new long[capacity];
    for (int i = array.length; i-- > 0;)
      elements[i] = array[i].longValue();
  }

  @Override
  public LispObject typeOf()
  {
    return list(SymbolConstants.SIMPLE_ARRAY, UNSIGNED_BYTE_32,
                 new Cons(Fixnum.getInstance(capacity)));
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.VECTOR;
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type == SymbolConstants.SIMPLE_ARRAY)
      return T;
    if (type == BuiltInClass.SIMPLE_ARRAY)
      return T;
    return super.typep(type);
  }

  @Override
  public LispObject getElementType()
  {
    return UNSIGNED_BYTE_32;
  }

  @Override
  public boolean isSimpleVector()
  {
    return false;
  }

  @Override
  public boolean hasFillPointer()
  {
    return false;
  }

  @Override
  public boolean isAdjustable()
  {
    return false;
  }

  @Override
  public int capacity()
  {
    return capacity;
  }

  @Override
  public int seqLength()
  {
    return capacity;
  }

  @Override
  public LispObject elt(int index) throws ConditionThrowable
  {
    try
      {
        return number(elements[index]);
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        badIndex(index, capacity);
        return NIL; // Not reached.
      }
  }

  @Override
  public int aref(int index) throws ConditionThrowable
  {
    try
      {
        return (int) elements[index];
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        badIndex(index, elements.length);
        return -1; // Not reached.
      }
  }

  @Override
  public long aref_long(int index) throws ConditionThrowable
  {
    try
      {
        return elements[index];
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        badIndex(index, elements.length);
        return -1; // Not reached.
      }
  }

  @Override
  public LispObject AREF(int index) throws ConditionThrowable
  {
    try
      {
        return number(elements[index]);
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        badIndex(index, elements.length);
        return NIL; // Not reached.
      }
  }

  @Override
  public LispObject AREF(LispObject index) throws ConditionThrowable
  {
        final int idx = Fixnum.getValue(index);
    try
      {
        return number(elements[idx]);
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        badIndex(idx, elements.length);
        return NIL; // Not reached.
      }
  }

  @Override
  public void aset(int index, LispObject newValue) throws ConditionThrowable
  {
    try
      {
        elements[index] = newValue.longValue();
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        badIndex(index, capacity);
      }
  }

  @Override
  public LispObject subseq(int start, int end) throws ConditionThrowable
  {
    BasicVector_UnsignedByte32 v = new BasicVector_UnsignedByte32(end - start);
    int i = start, j = 0;
    try
      {
        while (i < end)
          v.elements[j++] = elements[i++];
        return v;
      }
    catch (ArrayIndexOutOfBoundsException e)
      {
        // FIXME
        return error(new TypeError("Array index out of bounds: " + i + "."));
      }
  }

  @Override
  public void fillVoid(LispObject obj) throws ConditionThrowable
  {
    for (int i = capacity; i-- > 0;)
      elements[i] = obj.longValue();
  }

  @Override
  public void shrink(int n) throws ConditionThrowable
  {
    if (n < capacity)
      {
        long[] newArray = new long[n];
        System.arraycopy(elements, 0, newArray, 0, n);
        elements = newArray;
        capacity = n;
        return;
      }
    if (n == capacity)
      return;
    error(new LispError());
  }

  @Override
  public LispObject reverse() throws ConditionThrowable
  {
    BasicVector_UnsignedByte32 result = new BasicVector_UnsignedByte32(capacity);
    int i, j;
    for (i = 0, j = capacity - 1; i < capacity; i++, j--)
      result.elements[i] = elements[j];
    return result;
  }

  @Override
  public LispObject nreverse() throws ConditionThrowable
  {
    int i = 0;
    int j = capacity - 1;
    while (i < j)
      {
        long temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
        ++i;
        --j;
      }
    return this;
  }

  @Override
  public AbstractVector adjustArray(int newCapacity,
                                     LispObject initialElement,
                                     LispObject initialContents)
    throws ConditionThrowable
  {
    if (initialContents != null)
      {
        LispObject[] newElements = new LispObject[newCapacity];
        if (initialContents.isList())
          {
            LispObject list = initialContents;
            for (int i = 0; i < newCapacity; i++)
              {
                newElements[i] = list.first();
                list = list.rest();
              }
          }
        else if (initialContents.isVector())
          {
            for (int i = 0; i < newCapacity; i++)
              newElements[i] = initialContents.elt(i);
          }
        else
          type_error(initialContents, SymbolConstants.SEQUENCE);
        return new BasicVector_UnsignedByte32(newElements);
      }
    if (capacity != newCapacity)
      {
        LispObject[] newElements = new LispObject[newCapacity];
        System.arraycopy(elements, 0, newElements, 0,
                         Math.min(capacity, newCapacity));
        if (initialElement != null)
            for (int i = capacity; i < newCapacity; i++)
                newElements[i] = initialElement;
        return new BasicVector_UnsignedByte32(newElements);
      }
    // No change.
    return this;
  }

  @Override
  public AbstractVector adjustArray(int newCapacity,
                                     AbstractArray displacedTo,
                                     int displacement)
  {
    return new ComplexVector(newCapacity, displacedTo, displacement);
  }
}
