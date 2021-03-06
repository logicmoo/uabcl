/*
 * Fixnum.java
 *
 * Copyright (C) 2002-2006 Peter Graves
 * $Id: Fixnum.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

public final class Fixnum extends LispInteger
{
  public static final int MAX_POS_CACHE = 256;//just like before - however never set this to less than 256
  public static final Fixnum[] constants = new Fixnum[MAX_POS_CACHE];
  static
  {
    for (int i = 0; i < MAX_POS_CACHE; i++)
      constants[i] = new Fixnum(i);
  }

  public static final Fixnum ZERO      = constants[0];
  public static final Fixnum ONE       = constants[1];
  public static final Fixnum TWO       = constants[2];
  public static final Fixnum THREE     = constants[3];

  public static final Fixnum MINUS_ONE = new Fixnum(-1);

  public static Fixnum makeFixnum(int n)
  {
    return (n >= 0 && n < MAX_POS_CACHE) ? constants[n] :(n==-1?MINUS_ONE:new Fixnum(n));
  }

  private final int value;

  // set to private to hunt down sneaky creators
  private Fixnum(int value)
  {
    this.value = value;
  }
  
	public boolean isFixnum() {
		return true;
	}

  @Override
  public Object javaInstance()
  {
    return Integer.valueOf(intValue());
  }

  @Override
  public Object javaInstance(Class c)
  {
    String cn = c.getName();
    if (cn.equals("java.lang.Byte") || cn.equals("byte"))
      return Byte.valueOf((byte)intValue());
    if (cn.equals("java.lang.Short") || cn.equals("short"))
      return Short.valueOf((short)intValue());
    if (cn.equals("java.lang.Long") || cn.equals("long"))
      return Long.valueOf((long)intValue());
    return javaInstance();
  }

  @Override
  public LispObject typeOf()
  {
    if (intValue() == 0 || intValue() == 1)
      return SymbolConstants.BIT;
    if (intValue() > 1)
      return list(SymbolConstants.INTEGER, ZERO, Fixnum.makeFixnum(Integer.MAX_VALUE));
    return SymbolConstants.FIXNUM;
  }

  @Override
  public LispObject classOf()
  {
    return BuiltInClass.FIXNUM;
  }

  @Override
  public LispObject getDescription()
  {
    StringBuffer sb = new StringBuffer("The fixnum ");
    sb.append(intValue());
    return new SimpleString(sb);
  }

  @Override
  public LispObject typep(LispObject type) throws ConditionThrowable
  {
    if (type instanceof Symbol)
      {
        if (type == SymbolConstants.FIXNUM)
          return T;
        if (type == SymbolConstants.INTEGER)
          return T;
        if (type == SymbolConstants.RATIONAL)
          return T;
        if (type == SymbolConstants.REAL)
          return T;
        if (type == SymbolConstants.NUMBER)
          return T;
        if (type == SymbolConstants.SIGNED_BYTE)
          return T;
        if (type == SymbolConstants.UNSIGNED_BYTE)
          return intValue() >= 0 ? T : NIL;
        if (type == SymbolConstants.BIT)
          return (intValue() == 0 || intValue() == 1) ? T : NIL;
      }
    else if (type instanceof LispClass)
      {
        if (type == BuiltInClass.FIXNUM)
          return T;
        if (type == BuiltInClass.INTEGER)
          return T;
        if (type == BuiltInClass.RATIONAL)
          return T;
        if (type == BuiltInClass.REAL)
          return T;
        if (type == BuiltInClass.NUMBER)
          return T;
      }
    else if (type instanceof Cons)
      {
        if (type.equal(UNSIGNED_BYTE_8))
          return (intValue() >= 0 && intValue() <= 255) ? T : NIL;
        if (type.equal(UNSIGNED_BYTE_16))
          return (intValue() >= 0 && intValue() <= 65535) ? T : NIL;
        if (type.equal(UNSIGNED_BYTE_32))
          return intValue() >= 0 ? T : NIL;
      }
    return super.typep(type);
  }

  @Override
  public LispObject NUMBERP()
  {
    return T;
  }

  @Override
  public boolean isNumber()
  {
    return true;
  }

  @Override
  public LispObject INTEGERP()
  {
    return T;
  }

  @Override
  public boolean isInteger()
  {
    return true;
  }

  @Override
  public boolean rationalp()
  {
    return true;
  }

  @Override
  public boolean realp()
  {
    return true;
  }

  @Override
  public boolean eql(int n)
  {
    return intValue() == n;
  }

  @Override
  public boolean eql(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj  instanceof Fixnum)
      {
        if (intValue() == obj.intValue())
          return true;
      }
    return false;
  }

  @Override
  public boolean equal(int n)
  {
    return intValue() == n;
  }

  @Override
  public boolean equal(LispObject obj)
  {
    if (this == obj)
      return true;
    if (obj  instanceof Fixnum)
      {
        if (intValue() == obj.intValue())
          return true;
      }
    return false;
  }

  @Override
  public boolean equalp(int n)
  {
    return intValue() == n;
  }

  @Override
  public boolean equalp(LispObject obj)
  {
    if (obj  instanceof Fixnum)
      return intValue() == obj.intValue();
    if (obj  instanceof SingleFloat)
      return intValue() == obj.floatValue();
    if (obj instanceof DoubleFloat)
      return intValue() == obj.doubleValue();
    return false;
  }

  @Override
  public LispObject ABS()
  {
    if (intValue() >= 0)
      return this;
    return LispInteger.getInteger(-(long)intValue());
  }

  @Override
  public LispObject NUMERATOR()
  {
    return this;
  }

  @Override
  public LispObject DENOMINATOR()
  {
    return ONE;
  }

  @Override
  public boolean isEven() throws ConditionThrowable
  {
    return (intValue() & 0x01) == 0;
  }

  @Override
  public boolean isOdd() throws ConditionThrowable
  {
    return (intValue() & 0x01) != 0;
  }

  @Override
  public boolean isPositive()
  {
    return intValue() > 0;
  }

  @Override
  public boolean isNegative()
  {
    return intValue() < 0;
  }

  @Override
  public boolean isZero()
  {
    return intValue() == 0;
  }

//  public static int getValue(LispObject obj) throws ConditionThrowable
//  {
//	    return obj.intValue();
////          if (obj  instanceof Fixnum) return ((Fixnum)obj).value;
////          type_error(obj, SymbolConstants.FIXNUM);
////      // Not reached.
////          return 0;
//  }

  @Override
  public float floatValue() {
    return (float)intValue();
  }

  @Override
  public double doubleValue() {
    return (double)intValue();
  }

//  public static int getInt(LispObject obj) throws ConditionThrowable
//  {
//          if (obj  instanceof Fixnum) return obj.intValue();
//          type_error(obj, SymbolConstants.FIXNUM);
//      // Not reached.
//          return 0;
//  }

  public static BigInteger getBigInteger(LispObject obj) throws ConditionThrowable
  {
	  return obj.bigIntegerValue();
       //   if (obj  instanceof Fixnum) return BigInteger.valueOf(obj.intValue());
        //  type_error(obj, SymbolConstants.FIXNUM);
      // Not reached.
      //    return null;
  }

  @Override
  public int intValue()
  {
    return value;
  }

  @Override
  public long longValue()
  {
    return (long) intValue();
  }

  public final BigInteger bigIntegerValue()
  {
    return BigInteger.valueOf(intValue());
  }

  @Override
  public final LispObject incr()
  {
    return LispInteger.getInteger(1 + (long)intValue());
  }

  @Override
  public final LispObject decr()
  {
    return LispInteger.getInteger(-1 + (long)intValue());
  }

  @Override
  public LispObject negate()
  {
    return LispInteger.getInteger((-(long)intValue()));
  }

  @Override
  public LispObject add(int n)
  {
    return LispInteger.getInteger((long) intValue() + n);
  }

  @Override
  public LispObject add(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      {
        long result = (long) intValue() + obj.intValue();
        return LispInteger.getInteger(result);
      }
    if (obj  instanceof Bignum)
      return number(bigIntegerValue().add(obj.bigIntegerValue()));
    if (obj instanceof Ratio)
      {
        BigInteger numerator = ((Ratio)obj).numerator();
        BigInteger denominator = ((Ratio)obj).denominator();
        return number(bigIntegerValue().multiply(denominator).add(numerator),
                      denominator);
      }
    if (obj  instanceof SingleFloat)
      return NumericLispObject.createSingleFloat(intValue() + obj.floatValue());
    if (obj instanceof DoubleFloat)
      return NumericLispObject.createDoubleFloat(intValue() + obj.doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return Complex.getInstance(add(c.getRealPart()), c.getImaginaryPart());
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public LispObject subtract(int n)
  {
    return LispInteger.getInteger((long)intValue() - n);
  }

  @Override
  public LispObject subtract(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return number((long) intValue() - obj.intValue());
    if (obj  instanceof Bignum)
      return number(bigIntegerValue().subtract(obj.bigIntegerValue()));
    if (obj instanceof Ratio)
      {
        BigInteger numerator = ((Ratio)obj).numerator();
        BigInteger denominator = ((Ratio)obj).denominator();
        return number(
          bigIntegerValue().multiply(denominator).subtract(numerator),
          denominator);
      }
    if (obj  instanceof SingleFloat)
      return NumericLispObject.createSingleFloat(intValue() - obj.floatValue());
    if (obj instanceof DoubleFloat)
      return NumericLispObject.createDoubleFloat(intValue() - obj.doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return Complex.getInstance(subtract(c.getRealPart()),
                                   ZERO.subtract(c.getImaginaryPart()));
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public LispObject multiplyBy(int n)
  {
    long result = (long) intValue() * n;
    return LispInteger.getInteger(result);
  }

  @Override
  public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      {
        long result = (long) intValue() * obj.intValue();
        return LispInteger.getInteger(result);
      }
    if (obj  instanceof Bignum)
      return number(bigIntegerValue().multiply(obj.bigIntegerValue()));
    if (obj instanceof Ratio)
      {
        BigInteger numerator = ((Ratio)obj).numerator();
        BigInteger denominator = ((Ratio)obj).denominator();
        return number(
          bigIntegerValue().multiply(numerator),
          denominator);
      }
    if (obj  instanceof SingleFloat)
      return NumericLispObject.createSingleFloat(intValue() * obj.floatValue());
    if (obj instanceof DoubleFloat)
      return NumericLispObject.createDoubleFloat(intValue() * obj.doubleValue());
    if (obj instanceof Complex)
      {
        Complex c = (Complex) obj;
        return Complex.getInstance(multiplyBy(c.getRealPart()),
                                   multiplyBy(c.getImaginaryPart()));
      }
    return type_error(obj, SymbolConstants.NUMBER);
  }

  @Override
  public LispObject divideBy(LispObject obj) throws ConditionThrowable
  {
    try
      {
        if (obj  instanceof Fixnum)
          {
            final int divisor = obj.intValue();
            // (/ MOST-NEGATIVE-FIXNUM -1) is a bignum.
            if (intValue() > Integer.MIN_VALUE)
              if (intValue() % divisor == 0)
                return Fixnum.makeFixnum(intValue() / divisor);
            return number(BigInteger.valueOf(intValue()),
                          BigInteger.valueOf(divisor));
          }
        if (obj  instanceof Bignum)
          return number(bigIntegerValue(), obj.bigIntegerValue());
        if (obj instanceof Ratio)
          {
            BigInteger numerator = ((Ratio)obj).numerator();
            BigInteger denominator = ((Ratio)obj).denominator();
            return number(bigIntegerValue().multiply(denominator),
                          numerator);
          }
        if (obj  instanceof SingleFloat)
          return NumericLispObject.createSingleFloat(intValue() / obj.floatValue());
        if (obj instanceof DoubleFloat)
          return NumericLispObject.createDoubleFloat(intValue() / obj.doubleValue());
        if (obj instanceof Complex)
          {
            Complex c = (Complex) obj;
            LispObject realPart = c.getRealPart();
            LispObject imagPart = c.getImaginaryPart();
            LispObject denominator =
              realPart.multiplyBy(realPart).add(imagPart.multiplyBy(imagPart));
            return Complex.getInstance(multiplyBy(realPart).divideBy(denominator),
                                       Fixnum.ZERO.subtract(multiplyBy(imagPart).divideBy(denominator)));
          }
        return type_error(obj, SymbolConstants.NUMBER);
      }
    catch (ArithmeticException e)
      {
        if (obj.isZero())
          return error(new DivisionByZero());
        return error(new ArithmeticError(e.getMessage()));
      }
  }

  @Override
  public boolean isEqualTo(int n)
  {
    return intValue() == n;
  }

  @Override
  public boolean isEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return intValue() == obj.intValue();
    if (obj  instanceof SingleFloat)
      return isEqualTo(obj.rational());
    if (obj instanceof DoubleFloat)
      return intValue() == obj.doubleValue();
    if (obj instanceof Complex)
      return obj.isEqualTo(this);
    if (obj.isNumber())
      return false;
    type_error(obj, SymbolConstants.NUMBER);
    // Not reached.
    return false;
  }

  @Override
  public boolean isNotEqualTo(int n)
  {
    return intValue() != n;
  }

  @Override
  public boolean isNotEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return intValue() != obj.intValue();
    // obj is not a fixnum.
    if (obj  instanceof SingleFloat)
      return isNotEqualTo(obj.rational());
    if (obj instanceof DoubleFloat)
      return intValue() != obj.doubleValue();
    if (obj instanceof Complex)
      return obj.isNotEqualTo(this);
    if (obj.isNumber())
      return true;
    type_error(obj, SymbolConstants.NUMBER);
    // Not reached.
    return false;
  }

  @Override
  public boolean isLessThan(int n)
  {
    return intValue() < n;
  }

  @Override
  public boolean isLessThan(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return intValue() < obj.intValue();
    if (obj  instanceof Bignum)
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) < 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) < 0;
      }
    if (obj .floatp())
      return isLessThan(obj.rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public boolean isGreaterThan(int n) throws ConditionThrowable
  {
    return intValue() > n;
  }

  @Override
  public boolean isGreaterThan(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return intValue() > obj.intValue();
    if (obj  instanceof Bignum)
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) > 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) > 0;
      }
    if (obj .floatp())
      return isGreaterThan(obj.rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public boolean isLessThanOrEqualTo(int n)
  {
    return intValue() <= n;
  }

  @Override
  public boolean isLessThanOrEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return intValue() <= obj.intValue();
    if (obj  instanceof Bignum)
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) <= 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) <= 0;
      }
    if (obj .floatp())
      return isLessThanOrEqualTo(obj.rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public boolean isGreaterThanOrEqualTo(int n)
  {
    return intValue() >= n;
  }

  @Override
  public boolean isGreaterThanOrEqualTo(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return intValue() >= obj.intValue();
    if (obj  instanceof Bignum)
      return bigIntegerValue().compareTo(obj.bigIntegerValue()) >= 0;
    if (obj instanceof Ratio)
      {
        BigInteger n = bigIntegerValue().multiply(((Ratio)obj).denominator());
        return n.compareTo(((Ratio)obj).numerator()) >= 0;
      }
    if (obj .floatp())
      return isGreaterThanOrEqualTo(obj.rational());
    type_error(obj, SymbolConstants.REAL);
    // Not reached.
    return false;
  }

  @Override
  public LispObject truncate(LispObject obj) throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    final LispObject value1, value2;
    try
      {
        if (obj  instanceof Fixnum)
          {
            int divisor = obj.intValue();
            int quotient = intValue() / divisor;
            int remainder = intValue() % divisor;
            value1 = Fixnum.makeFixnum(quotient);
            value2 = remainder == 0 ? Fixnum.ZERO : Fixnum.makeFixnum(remainder);
          }
        else if (obj  instanceof Bignum)
          {
            BigInteger val = bigIntegerValue();
            BigInteger divisor = obj.bigIntegerValue();
            BigInteger[] results = val.divideAndRemainder(divisor);
            BigInteger quotient = results[0];
            BigInteger remainder = results[1];
            value1 = number(quotient);
            value2 = (remainder.signum() == 0) ? Fixnum.ZERO : number(remainder);
          }
        else if (obj instanceof Ratio)
          {
            Ratio divisor = (Ratio) obj;
            LispObject quotient =
              multiplyBy(divisor.DENOMINATOR()).truncate(divisor.NUMERATOR());
            LispObject remainder =
              subtract(quotient.multiplyBy(divisor));
            value1 = quotient;
            value2 = remainder;
          }
        else if (obj  instanceof SingleFloat)
          {
            // "When rationals and floats are combined by a numerical function,
            // the rational is first converted to a float of the same format."
            // 12.1.4.1
            return NumericLispObject.createSingleFloat((float)intValue()).truncate(obj);
          }
        else if (obj instanceof DoubleFloat)
          {
            // "When rationals and floats are combined by a numerical function,
            // the rational is first converted to a float of the same format."
            // 12.1.4.1
            return NumericLispObject.createDoubleFloat((double)intValue()).truncate(obj);
          }
        else
          return type_error(obj, SymbolConstants.REAL);
      }
    catch (ArithmeticException e)
      {
        if (obj.isZero())
          return error(new DivisionByZero());
        else
          return error(new ArithmeticError(e.getMessage()));
      }
    return thread.setValues(value1, value2);
  }

  @Override
  public LispObject MOD(LispObject divisor) throws ConditionThrowable
  {
    if (divisor  instanceof Fixnum)
      return MOD(divisor.intValue());
    return super.MOD(divisor);
  }

  @Override
  public LispObject MOD(int divisor) throws ConditionThrowable
  {
    final int r;
    try
      {
        r = intValue() % divisor;
      }
    catch (ArithmeticException e)
      {
        return error(new ArithmeticError("Division by zero."));
      }
    if (r == 0)
      return Fixnum.ZERO;
    if (divisor < 0)
      {
        if (intValue() > 0)
          return Fixnum.makeFixnum(r + divisor);
      }
    else
      {
        if (intValue() < 0)
          return Fixnum.makeFixnum(r + divisor);
      }
    return Fixnum.makeFixnum(r);
  }

  @Override
  public LispObject ash(int shift)
  {
    if (intValue() == 0)
      return this;
    if (shift == 0)
      return this;
    long n = intValue();
    if (shift <= -32)
      {
        // Right shift.
        return n >= 0 ? Fixnum.ZERO : Fixnum.MINUS_ONE;
      }
    if (shift < 0)
      return Fixnum.makeFixnum((int)(n >> -shift));
    if (shift <= 32)
      {
        n = n << shift;
        return LispInteger.getInteger(n);
      }
    // BigInteger.shiftLeft() succumbs to a stack overflow if shift
    // is Integer.MIN_VALUE, so...
    if (shift == Integer.MIN_VALUE)
      return n >= 0 ? Fixnum.ZERO : Fixnum.MINUS_ONE;
    return number(BigInteger.valueOf(intValue()).shiftLeft(shift));
  }

  @Override
  public LispObject ash(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return ash(obj.intValue());
    if (obj  instanceof Bignum)
      {
        if (intValue() == 0)
          return this;
        BigInteger n = BigInteger.valueOf(intValue());
        BigInteger shift = obj.bigIntegerValue();
        if (shift.signum() > 0)
          return error(new LispError("Can't represent result of left shift."));
        if (shift.signum() < 0)
          return n.signum() >= 0 ? Fixnum.ZERO : Fixnum.MINUS_ONE;
        Debug.bug(); // Shouldn't happen.
      }
    return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LOGNOT()
  {
    return Fixnum.makeFixnum(~intValue());
  }

  @Override
  public LispObject LOGAND(int n) throws ConditionThrowable
  {
    return Fixnum.makeFixnum(intValue() & n);
  }

  @Override
  public LispObject LOGAND(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return Fixnum.makeFixnum(intValue() & obj.intValue());
    if (obj  instanceof Bignum)
      {
        if (intValue() >= 0)
          {
            int n2 = (obj.bigIntegerValue()).intValue();
            return Fixnum.makeFixnum(intValue() & n2);
          }
        else
          {
            BigInteger n1 = bigIntegerValue();
            BigInteger n2 = obj.bigIntegerValue();
            return number(n1.and(n2));
          }
      }
    return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LOGIOR(int n) throws ConditionThrowable
  {
    return Fixnum.makeFixnum(intValue() | n);
  }

  @Override
  public LispObject LOGIOR(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return Fixnum.makeFixnum(intValue() | obj.intValue());
    if (obj  instanceof Bignum)
      {
        BigInteger n1 = bigIntegerValue();
        BigInteger n2 = obj.bigIntegerValue();
        return number(n1.or(n2));
      }
    return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LOGXOR(int n) throws ConditionThrowable
  {
    return Fixnum.makeFixnum(intValue() ^ n);
  }

  @Override
  public LispObject LOGXOR(LispObject obj) throws ConditionThrowable
  {
    if (obj  instanceof Fixnum)
      return Fixnum.makeFixnum(intValue() ^ obj.intValue());
    if (obj  instanceof Bignum)
      {
        BigInteger n1 = bigIntegerValue();
        BigInteger n2 = obj.bigIntegerValue();
        return number(n1.xor(n2));
      }
    return type_error(obj, SymbolConstants.INTEGER);
  }

  @Override
  public LispObject LDB(int size, int position)
  {
    long n = (long) intValue() >> position;
    long mask = (1L << size) - 1;
    return number(n & mask);
  }

  final static BigInteger BIGINTEGER_TWO = new BigInteger ("2");

  /** Computes fixnum^bignum, returning a fixnum or a bignum.
    */
  public LispObject pow(LispObject obj) throws ConditionThrowable
  {
    BigInteger y = obj.bigIntegerValue();

    if (y.compareTo (BigInteger.ZERO) < 0)
      return (Fixnum.makeFixnum(1)).divideBy(this.pow(Bignum.getInteger(y.negate())));

    if (y.compareTo(BigInteger.ZERO) == 0)
      // No need to test base here; CLHS says 0^0 == 1.
      return Fixnum.makeFixnum(1);
      
    int x = this.intValue();

    if (x == 0)
      return Fixnum.makeFixnum(0);

    if (x == 1)
      return Fixnum.makeFixnum(1);

    BigInteger xy = BigInteger.ONE;
    BigInteger term = BigInteger.valueOf((long) x);

    while (! y.equals(BigInteger.ZERO))
    {
      if (y.testBit(0))
        xy = xy.multiply(term);

      term = term.multiply(term);
      y = y.shiftLeft(1);
    }

    return Bignum.getInteger(xy);
  }

  @Override
  public int clHash()
  {
    return intValue();
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    int base = SymbolConstants.PRINT_BASE.symbolValue(thread).intValue();
    String s = Integer.toString(intValue(), base).toUpperCase();
    if (SymbolConstants.PRINT_RADIX.symbolValue(thread) != NIL)
      {
        FastStringBuffer sb = new FastStringBuffer();
        switch (base)
          {
          case 2:
            sb.append("#b");
            sb.append(s);
            break;
          case 8:
            sb.append("#o");
            sb.append(s);
            break;
          case 10:
            sb.append(s);
            sb.append('.');
            break;
          case 16:
            sb.append("#x");
            sb.append(s);
            break;
          default:
            sb.append('#');
            sb.append(String.valueOf(base));
            sb.append('r');
            sb.append(s);
            break;
          }
        s = sb.toString();
      }
    return s;
  }
}
