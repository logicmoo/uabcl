/*
 * Ratio.java
 *
 * Copyright (C) 2003-2005 Peter Graves
 * $Id: Ratio.java 11579 2009-01-24 10:24:34Z ehuelsmann $
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

public final class Ratio extends NumericLispObject
{
    private BigInteger numerator;
    private BigInteger denominator;

    @Override
    public Object javaInstance() throws ConditionThrowable {
    	// TODO Auto-generated method stub
    	return this;
    }
    
    public Ratio(BigInteger numerator, BigInteger denominator)
    {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public BigInteger numerator()
    {
        return numerator;
    }

    @Override
    public LispObject NUMERATOR()
    {
        return number(numerator);
    }

    public BigInteger denominator()
    {
        return denominator;
    }

    @Override
    public LispObject DENOMINATOR()
    {
        return number(denominator);
    }

    @Override
    public LispObject typeOf()
    {
        return SymbolConstants.RATIO;
    }

    @Override
    public LispObject classOf()
    {
        return BuiltInClass.RATIO;
    }

    @Override
    public LispObject typep(LispObject type) throws ConditionThrowable
    {
        if (type == SymbolConstants.RATIO)
            return T;
        if (type == SymbolConstants.RATIONAL)
            return T;
        if (type == SymbolConstants.REAL)
            return T;
        if (type == SymbolConstants.NUMBER)
            return T;
        if (type == BuiltInClass.RATIO)
            return T;
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
    public boolean eql(LispObject obj)
    {
        if (this == obj)
            return true;
        if (obj instanceof Ratio) {
            return (numerator.equals(((Ratio)obj).numerator) &&
                    denominator.equals(((Ratio)obj).denominator));
        }
        return false;
    }

    @Override
    public boolean equal(LispObject obj)
    {
        return eql(obj);
    }

    @Override
    public boolean equalp(LispObject obj)
    {
        if (obj instanceof Ratio) {
            return numerator.equals(((Ratio)obj).numerator) &&
                denominator.equals(((Ratio)obj).denominator);
        }
        if (obj  instanceof SingleFloat) {
            return floatValue() == obj.floatValue();
        }
        if (obj instanceof DoubleFloat) {
            return doubleValue() == obj.doubleValue();
        }
        return false;
    }

    @Override
    public LispObject ABS()
    {
        if (numerator.signum() > 0 && denominator.signum() > 0)
            return this;
        if (numerator.signum() < 0 && denominator.signum() < 0)
            return this;
        return new Ratio(numerator.negate(), denominator);
    }

    @Override
    public boolean isPositive()
    {
        return numerator.signum() == denominator.signum();
    }

    @Override
    public boolean isNegative()
    {
        return numerator.signum() != denominator.signum();
    }

    @Override
    public boolean isZero()
    {
        return false;
    }

    @Override
    public float floatValue()
    {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue()
    {
        double result = numerator.doubleValue() / denominator.doubleValue();
        if (result != 0 && !Double.isNaN(result) && !Double.isInfinite(result))
            return result;
        final boolean negative = numerator.signum() < 0;
        final BigInteger num = negative ? numerator.negate() : numerator;
        final BigInteger den = denominator;
        final int numLen = num.bitLength();
        final int denLen = den.bitLength();
        int length = Math.min(numLen, denLen);
        if (length <= 1)
            return result;
        BigInteger n = num;
        BigInteger d = den;
        final int digits = 54;
        if (length > digits) {
            n = n.shiftRight(length - digits);
            d = d.shiftRight(length - digits);
            length -= digits;
        } else {
            n = n.shiftRight(1);
            d = d.shiftRight(1);
            --length;
        }
        for (int i = 0; i < length; i++) {
            result = n.doubleValue() / d.doubleValue();
            if (result != 0 && !Double.isNaN(result) && !Double.isInfinite(result))
                break;
            n = n.shiftRight(1);
            d = d.shiftRight(1);
        }
        return negative ? -result : result;
    }

    @Override
    public final LispObject incr() throws ConditionThrowable
    {
        return new Ratio(numerator.add(denominator), denominator);
    }

    @Override
    public final LispObject decr() throws ConditionThrowable
    {
        return new Ratio(numerator.subtract(denominator), denominator);
    }

    @Override
    public LispObject add(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n =
                numerator.add(BigInteger.valueOf(obj.intValue()).multiply(denominator));
            return number(n, denominator);
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue();
            return number(numerator.add(n.multiply(denominator)),
                denominator);
        }
        if (obj instanceof Ratio) {
            BigInteger n = ((Ratio)obj).numerator;
            BigInteger d = ((Ratio)obj).denominator;
            if (denominator.equals(d))
                return number(numerator.add(n), denominator);
            BigInteger common = denominator.multiply(d);
            return number(numerator.multiply(d).add(n.multiply(denominator)),
                common);
        }
        if (obj  instanceof SingleFloat) {
            return NumericLispObject.createSingleFloat(floatValue() + obj.floatValue());
        }
        if (obj instanceof DoubleFloat) {
            return NumericLispObject.createDoubleFloat(doubleValue() + obj.doubleValue());
        }
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(add(c.getRealPart()), c.getImaginaryPart());
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public LispObject subtract(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n =
                numerator.subtract(BigInteger.valueOf(obj.intValue()).multiply(denominator));
            return number(n, denominator);
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue();
            return number(numerator.subtract(n.multiply(denominator)),
                denominator);
        }
        if (obj instanceof Ratio) {
            BigInteger n = ((Ratio)obj).numerator;
            BigInteger d = ((Ratio)obj).denominator;
            if (denominator.equals(d))
                return number(numerator.subtract(n), denominator);
            BigInteger common = denominator.multiply(d);
            return number(numerator.multiply(d).subtract(n.multiply(denominator)),
                common);
        }
        if (obj  instanceof SingleFloat) {
            return NumericLispObject.createSingleFloat(floatValue() - obj.floatValue());
        }
        if (obj instanceof DoubleFloat) {
            return NumericLispObject.createDoubleFloat(doubleValue() - obj.doubleValue());
        }
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(subtract(c.getRealPart()),
                                       Fixnum.ZERO.subtract(c.getImaginaryPart()));
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n = obj.bigIntegerValue();
            return number(numerator.multiply(n), denominator);
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue();
            return number(numerator.multiply(n), denominator);
        }
        if (obj instanceof Ratio) {
            BigInteger n = ((Ratio)obj).numerator;
            BigInteger d = ((Ratio)obj).denominator;
            return number(numerator.multiply(n), denominator.multiply(d));
        }
        if (obj  instanceof SingleFloat) {
            return NumericLispObject.createSingleFloat(floatValue() * obj.floatValue());
        }
        if (obj instanceof DoubleFloat) {
            return NumericLispObject.createDoubleFloat(doubleValue() * obj.doubleValue());
        }
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(multiplyBy(c.getRealPart()),
                                       multiplyBy(c.getImaginaryPart()));
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public LispObject divideBy(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n = obj.bigIntegerValue();
            return number(numerator, denominator.multiply(n));
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue();
            return number(numerator, denominator.multiply(n));
        }
        if (obj instanceof Ratio) {
            BigInteger n = ((Ratio)obj).numerator;
            BigInteger d = ((Ratio)obj).denominator;
            return number(numerator.multiply(d), denominator.multiply(n));
        }
        if (obj  instanceof SingleFloat) {
            if (obj.isZero())
                return error(new DivisionByZero());
            return NumericLispObject.createSingleFloat(floatValue() / obj.floatValue());
        }
        if (obj instanceof DoubleFloat) {
            if (obj.isZero())
                return error(new DivisionByZero());
            return NumericLispObject.createDoubleFloat(doubleValue() / obj.doubleValue());
        }
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            // numerator
            LispObject realPart = this.multiplyBy(c.getRealPart());
            LispObject imagPart =
                Fixnum.ZERO.subtract(this).multiplyBy(c.getImaginaryPart());
            // denominator
            LispObject d =
                c.getRealPart().multiplyBy(c.getRealPart());
            d = d.add(c.getImaginaryPart().multiplyBy(c.getImaginaryPart()));
            return Complex.getInstance(realPart.divideBy(d),
                                       imagPart.divideBy(d));
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public boolean isEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj instanceof Ratio)
            return (numerator.equals(((Ratio)obj).numerator) &&
                    denominator.equals(((Ratio)obj).denominator));
        if (obj .floatp())
            return isEqualTo(obj.rational());
        if (obj.isNumber())
            return false;
        error(new TypeError(obj, SymbolConstants.NUMBER));
        // Not reached.
        return false;
    }

    @Override
    public boolean isNotEqualTo(LispObject obj) throws ConditionThrowable
    {
        return !isEqualTo(obj);
    }

    @Override
    public boolean isLessThan(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n2 = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n2) < 0;
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n) < 0;
        }
        if (obj instanceof Ratio) {
            BigInteger n1 = numerator.multiply(((Ratio)obj).denominator);
            BigInteger n2 = ((Ratio)obj).numerator.multiply(denominator);
            return n1.compareTo(n2) < 0;
        }
        if (obj .floatp())
            return isLessThan(obj.rational());
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public boolean isGreaterThan(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n2 = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n2) > 0;
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n) > 0;
        }
        if (obj instanceof Ratio) {
            BigInteger n1 = numerator.multiply(((Ratio)obj).denominator);
            BigInteger n2 = ((Ratio)obj).numerator.multiply(denominator);
            return n1.compareTo(n2) > 0;
        }
        if (obj .floatp())
            return isGreaterThan(obj.rational());
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public boolean isLessThanOrEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n2 = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n2) <= 0;
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n) <= 0;
        }
        if (obj instanceof Ratio) {
            BigInteger n1 = numerator.multiply(((Ratio)obj).denominator);
            BigInteger n2 = ((Ratio)obj).numerator.multiply(denominator);
            return n1.compareTo(n2) <= 0;
        }
        if (obj .floatp())
            return isLessThanOrEqualTo(obj.rational());
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public boolean isGreaterThanOrEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum) {
            BigInteger n2 = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n2) >= 0;
        }
        if (obj  instanceof Bignum) {
            BigInteger n = obj.bigIntegerValue().multiply(denominator);
            return numerator.compareTo(n) >= 0;
        }
        if (obj instanceof Ratio) {
            BigInteger n1 = numerator.multiply(((Ratio)obj).denominator);
            BigInteger n2 = ((Ratio)obj).numerator.multiply(denominator);
            return n1.compareTo(n2) >= 0;
        }
        if (obj .floatp())
            return isGreaterThanOrEqualTo(obj.rational());
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public LispObject truncate(LispObject obj) throws ConditionThrowable
    {
        // "When rationals and floats are combined by a numerical function,
        // the rational is first converted to a float of the same format."
        // 12.1.4.1
        if (obj  instanceof SingleFloat)
            return NumericLispObject.createSingleFloat(floatValue()).truncate(obj);
        if (obj instanceof DoubleFloat)
            return NumericLispObject.createDoubleFloat(doubleValue()).truncate(obj);
        BigInteger n, d;
	try {
	  if (obj  instanceof Fixnum) {
            n = obj.bigIntegerValue();
            d = BigInteger.ONE;
	  } else if (obj  instanceof Bignum) {
            n = obj.bigIntegerValue();
            d = BigInteger.ONE;
	  } else if (obj instanceof Ratio) {
            n = ((Ratio)obj).numerator();
            d = ((Ratio)obj).denominator();
	  } else {
            return error(new TypeError(obj, SymbolConstants.NUMBER));
	  }
	  // Invert and multiply.
	  BigInteger num = numerator.multiply(d);
	  BigInteger den = denominator.multiply(n);
	  BigInteger quotient = num.divide(den);
	  // Multiply quotient by divisor.
	  LispObject product = number(quotient.multiply(n), d);
	  // Subtract to get remainder.
	  LispObject remainder = subtract(product);
          return LispThread.currentThread().setValues(number(quotient), remainder);
        }
        catch (ArithmeticException e) {
            if (obj.isZero())
                return error(new DivisionByZero());
            return error(new ArithmeticError(e.getMessage()));
        }
    }

    @Override
    public int clHash()
    {
        return numerator.hashCode() ^ denominator.hashCode();
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        final LispThread thread = LispThread.currentThread();
        int base = SymbolConstants.PRINT_BASE.symbolValue(thread).intValue();
        StringBuffer sb = new StringBuffer(numerator.toString(base));
        sb.append('/');
        sb.append(denominator.toString(base));
        String s = sb.toString().toUpperCase();
        if (SymbolConstants.PRINT_RADIX.symbolValue(thread) != NIL) {
            sb.setLength(0);
            switch (base) {
                case 2:
                    sb.append("#b");
                    sb.append(s);
                    break;
                case 8:
                    sb.append("#o");
                    sb.append(s);
                    break;
                case 10:
                    sb.append("#10r");
                    sb.append(s);
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
