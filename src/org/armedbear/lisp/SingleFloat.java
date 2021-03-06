/*
 * SingleFloat.java
 *
 * Copyright (C) 2003-2007 Peter Graves
 * $Id: SingleFloat.java 12018 2009-06-16 20:46:06Z ehuelsmann $
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

public final class SingleFloat extends NumericLispObject
{
	public boolean isSingleFloat() {
		return true;
	}
	
    public static final SingleFloat ZERO       = NumericLispObject.createSingleFloat(0f);
    public static final SingleFloat MINUS_ZERO = NumericLispObject.createSingleFloat(-0.0f);
    public static final SingleFloat ONE        = NumericLispObject.createSingleFloat(1f);
    public static final SingleFloat MINUS_ONE  = NumericLispObject.createSingleFloat(-1f);

    public static final SingleFloat SINGLE_FLOAT_POSITIVE_INFINITY =
        NumericLispObject.createSingleFloat(Float.POSITIVE_INFINITY);

    public static final SingleFloat SINGLE_FLOAT_NEGATIVE_INFINITY =
        NumericLispObject.createSingleFloat(Float.NEGATIVE_INFINITY);

    static {
        SymbolConstants.SINGLE_FLOAT_POSITIVE_INFINITY.initializeConstant(SINGLE_FLOAT_POSITIVE_INFINITY);
        SymbolConstants.SINGLE_FLOAT_NEGATIVE_INFINITY.initializeConstant(SINGLE_FLOAT_NEGATIVE_INFINITY);
    }

    public static SingleFloat getSingleFloat(float f) {
        if (f == 0.0f)
            return ZERO;
        else if (f == -0.0f )
            return MINUS_ZERO;
        else if (f == 1)
            return ONE;
        else if (f == -1)
            return MINUS_ONE;
        else
            return NumericLispObject.createSingleFloat(f);
    }

    private final float value;

    // TODO make the btye-code use the factory
    public SingleFloat(float value)
    {
        this.value = value;
    }

    @Override
    public LispObject typeOf()
    {
        return SymbolConstants.SINGLE_FLOAT;
    }

    @Override
    public LispObject classOf()
    {
        return BuiltInClass.SINGLE_FLOAT;
    }

    @Override
    public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
    {
        if (typeSpecifier == SymbolConstants.FLOAT)
            return T;
        if (typeSpecifier == SymbolConstants.REAL)
            return T;
        if (typeSpecifier == SymbolConstants.NUMBER)
            return T;
        if (typeSpecifier == SymbolConstants.SINGLE_FLOAT)
            return T;
        if (typeSpecifier == SymbolConstants.SHORT_FLOAT)
            return T;
        if (typeSpecifier == BuiltInClass.FLOAT)
            return T;
        if (typeSpecifier == BuiltInClass.SINGLE_FLOAT)
            return T;
        return super.typep(typeSpecifier);
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
    public boolean realp()
    {
        return true;
    }

    @Override
    public boolean eql(LispObject obj)
    {
        if (this == obj)
            return true;
        if (obj  instanceof SingleFloat) {
            if (floatValue() == 0) {
                // "If an implementation supports positive and negative zeros
                // as distinct values, then (EQL 0.0 -0.0) returns false."
                float f = obj.floatValue();
                int bits = Float.floatToRawIntBits(f);
                return bits == Float.floatToRawIntBits(floatValue());
            }
            if (floatValue() == obj.floatValue())
                return true;
        }
        return false;
    }

    @Override
    public boolean equal(LispObject obj)
    {
        if (this == obj)
            return true;
        if (obj  instanceof SingleFloat) {
            if (floatValue() == 0) {
                // same as EQL
                float f = obj.floatValue();
                int bits = Float.floatToRawIntBits(f);
                return bits == Float.floatToRawIntBits(floatValue());
            }
            if (floatValue() == obj.floatValue())
                return true;
        }
        return false;
    }

    @Override
    public boolean equalp(int n)
    {
        // "If two numbers are the same under =."
        return floatValue() == n;
    }

    @Override
    public boolean equalp(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof SingleFloat)
            return floatValue() == obj.floatValue();
        if (obj instanceof DoubleFloat)
            return floatValue() == obj.doubleValue();
        if (obj  instanceof Fixnum)
            return floatValue() == obj.intValue();
        if (obj  instanceof Bignum)
            return floatValue() == obj.floatValue();
        if (obj instanceof Ratio)
            return floatValue() == obj.floatValue();
        return false;
    }

    @Override
    public LispObject ABS()
    {
        if (floatValue() > 0)
            return this;
        if (floatValue() == 0) // 0.0 or -0.0
            return ZERO;
        return NumericLispObject.createSingleFloat(- floatValue());
    }

    @Override
    public boolean isPositive()
    {
        return floatValue() > 0;
    }

    @Override
    public boolean isNegative()
    {
        return floatValue() < 0;
    }

    @Override
    public boolean isZero()
    {
        return floatValue() == 0;
    }

    @Override
    public LispObject FLOATP()
    {
        return T;
    }

    @Override
    public boolean floatp()
    {
        return true;
    }

//    public static double getValue(LispObject obj) throws ConditionThrowable
//    {
//        if (obj  instanceof SingleFloat)
//            return obj.floatValue();
//        type_error(obj, SymbolConstants.FLOAT);
//        // not reached
//        return 0.0D;
//    }

    public final float getValue()
    {
        return floatValue();
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return floatValue();
    }

    @Override
    public Object javaInstance()
    {
        return Float.valueOf(floatValue());
    }

    @Override
    public Object javaInstance(Class c)
    {
        String cn = c.getName();
        if (cn.equals("java.lang.Float") || cn.equals("float"))
            return Float.valueOf(floatValue());
        return javaInstance();
    }

    @Override
    public final LispObject incr()
    {
        return NumericLispObject.createSingleFloat(floatValue() + 1);
    }

    @Override
    public final LispObject decr()
    {
        return NumericLispObject.createSingleFloat(floatValue() - 1);
    }

    @Override
    public LispObject add(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return NumericLispObject.createSingleFloat(floatValue() + obj.intValue());
        if (obj  instanceof SingleFloat)
            return NumericLispObject.createSingleFloat(floatValue() + obj.floatValue());
        if (obj instanceof DoubleFloat)
            return NumericLispObject.createDoubleFloat(floatValue() + obj.doubleValue());
        if (obj  instanceof Bignum)
            return NumericLispObject.createSingleFloat(floatValue() + obj.floatValue());
        if (obj instanceof Ratio)
            return NumericLispObject.createSingleFloat(floatValue() + obj.floatValue());
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(add(c.getRealPart()), c.getImaginaryPart());
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public LispObject negate()
    {
        if (floatValue() == 0) {
            int bits = Float.floatToRawIntBits(floatValue());
            return (bits < 0) ? ZERO : MINUS_ZERO;
        }
        return NumericLispObject.createSingleFloat(-floatValue());
    }

    @Override
    public LispObject subtract(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return NumericLispObject.createSingleFloat(floatValue() - obj.intValue());
        if (obj  instanceof SingleFloat)
            return NumericLispObject.createSingleFloat(floatValue() - obj.floatValue());
        if (obj instanceof DoubleFloat)
            return NumericLispObject.createDoubleFloat(floatValue() - obj.doubleValue());
        if (obj  instanceof Bignum)
            return NumericLispObject.createSingleFloat(floatValue() - obj.floatValue());
        if (obj instanceof Ratio)
            return NumericLispObject.createSingleFloat(floatValue() - obj.floatValue());
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            return Complex.getInstance(subtract(c.getRealPart()),
                                       ZERO.subtract(c.getImaginaryPart()));
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public LispObject multiplyBy(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return NumericLispObject.createSingleFloat(floatValue() * obj.intValue());
        if (obj  instanceof SingleFloat)
            return NumericLispObject.createSingleFloat(floatValue() * obj.floatValue());
        if (obj instanceof DoubleFloat)
            return NumericLispObject.createDoubleFloat(floatValue() * obj.doubleValue());
        if (obj  instanceof Bignum)
            return NumericLispObject.createSingleFloat(floatValue() * obj.floatValue());
        if (obj instanceof Ratio)
            return NumericLispObject.createSingleFloat(floatValue() * obj.floatValue());
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
        if (obj  instanceof Fixnum)
            return NumericLispObject.createSingleFloat(floatValue() / obj.intValue());
        if (obj  instanceof SingleFloat)
            return NumericLispObject.createSingleFloat(floatValue() / obj.floatValue());
        if (obj instanceof DoubleFloat)
            return NumericLispObject.createDoubleFloat(floatValue() / obj.doubleValue());
        if (obj  instanceof Bignum)
            return NumericLispObject.createSingleFloat(floatValue() / obj.floatValue());
        if (obj instanceof Ratio)
            return NumericLispObject.createSingleFloat(floatValue() / obj.floatValue());
        if (obj instanceof Complex) {
            Complex c = (Complex) obj;
            LispObject re = c.getRealPart();
            LispObject im = c.getImaginaryPart();
            LispObject denom = re.multiplyBy(re).add(im.multiplyBy(im));
            LispObject resX = multiplyBy(re).divideBy(denom);
            LispObject resY =
                multiplyBy(Fixnum.MINUS_ONE).multiplyBy(im).divideBy(denom);
            return Complex.getInstance(resX, resY);
        }
        return error(new TypeError(obj, SymbolConstants.NUMBER));
    }

    @Override
    public boolean isEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return rational().isEqualTo(obj);
        if (obj  instanceof SingleFloat)
            return floatValue() == obj.floatValue();
        if (obj instanceof DoubleFloat)
            return floatValue() == obj.doubleValue();
        if (obj  instanceof Bignum)
            return rational().isEqualTo(obj);
        if (obj instanceof Ratio)
            return rational().isEqualTo(obj);
        if (obj instanceof Complex)
            return obj.isEqualTo(this);
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
        if (obj  instanceof Fixnum)
            return rational().isLessThan(obj);
        if (obj  instanceof SingleFloat)
            return floatValue() < obj.floatValue();
        if (obj instanceof DoubleFloat)
            return floatValue() < obj.doubleValue();
        if (obj  instanceof Bignum)
            return rational().isLessThan(obj);
        if (obj instanceof Ratio)
            return rational().isLessThan(obj);
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public boolean isGreaterThan(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return rational().isGreaterThan(obj);
        if (obj  instanceof SingleFloat)
            return floatValue() > obj.floatValue();
        if (obj instanceof DoubleFloat)
            return floatValue() > obj.doubleValue();
        if (obj  instanceof Bignum)
            return rational().isGreaterThan(obj);
        if (obj instanceof Ratio)
            return rational().isGreaterThan(obj);
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public boolean isLessThanOrEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return rational().isLessThanOrEqualTo(obj);
        if (obj  instanceof SingleFloat)
            return floatValue() <= obj.floatValue();
        if (obj instanceof DoubleFloat)
            return floatValue() <= obj.doubleValue();
        if (obj  instanceof Bignum)
            return rational().isLessThanOrEqualTo(obj);
        if (obj instanceof Ratio)
            return rational().isLessThanOrEqualTo(obj);
        error(new TypeError(obj, SymbolConstants.REAL));
        // Not reached.
        return false;
    }

    @Override
    public boolean isGreaterThanOrEqualTo(LispObject obj) throws ConditionThrowable
    {
        if (obj  instanceof Fixnum)
            return rational().isGreaterThanOrEqualTo(obj);
        if (obj  instanceof SingleFloat)
            return floatValue() >= obj.floatValue();
        if (obj instanceof DoubleFloat)
            return floatValue() >= obj.doubleValue();
        if (obj  instanceof Bignum)
            return rational().isGreaterThanOrEqualTo(obj);
        if (obj instanceof Ratio)
            return rational().isGreaterThanOrEqualTo(obj);
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
        if (obj  instanceof Fixnum) {
            return truncate(NumericLispObject.createSingleFloat((float)obj.intValue()));
        }
        if (obj  instanceof Bignum) {
            return truncate(NumericLispObject.createSingleFloat(obj.floatValue()));
        }
        if (obj instanceof Ratio) {
            return truncate(NumericLispObject.createSingleFloat(obj.floatValue()));
        }
        if (obj  instanceof SingleFloat) {
            final LispThread thread = LispThread.currentThread();
            float divisor = obj.floatValue();
            float quotient = floatValue() / divisor;
            if (floatValue() != 0)
                MathFunctions.OverUnderFlowCheck(quotient);
            if (quotient >= Integer.MIN_VALUE && quotient <= Integer.MAX_VALUE) {
                int q = (int) quotient;
                return thread.setValues(Fixnum.makeFixnum(q),
                                        NumericLispObject.createSingleFloat(floatValue() - q * divisor));
            }
            // We need to convert the quotient to a bignum.
            int bits = Float.floatToRawIntBits(quotient);
            int s = ((bits >> 31) == 0) ? 1 : -1;
            int e = (int) ((bits >> 23) & 0xff);
            long m;
            if (e == 0)
                m = (bits & 0x7fffff) << 1;
            else
                m = (bits & 0x7fffff) | 0x800000;
            LispObject significand = number(m);
            Fixnum exponent = Fixnum.makeFixnum(e - 150);
            Fixnum sign = Fixnum.makeFixnum(s);
            LispObject result = significand;
            result =
                result.multiplyBy(MathFunctions.EXPT.execute(Fixnum.TWO, exponent));
            result = result.multiplyBy(sign);
            // Calculate remainder.
            LispObject product = result.multiplyBy(obj);
            LispObject remainder = subtract(product);
            return thread.setValues(result, remainder);
        }
        if (obj instanceof DoubleFloat) {
            final LispThread thread = LispThread.currentThread();
            double divisor = obj.doubleValue();
            double quotient = floatValue() / divisor;
            if (floatValue() != 0)
                MathFunctions.OverUnderFlowCheck(quotient);
            if (quotient >= Integer.MIN_VALUE && quotient <= Integer.MAX_VALUE) {
                int q = (int) quotient;
                return thread.setValues(Fixnum.makeFixnum(q),
                                        NumericLispObject.createDoubleFloat(floatValue() - q * divisor));
            }
            // We need to convert the quotient to a bignum.
            long bits = Double.doubleToRawLongBits((double)quotient);
            int s = ((bits >> 63) == 0) ? 1 : -1;
            int e = (int) ((bits >> 52) & 0x7ffL);
            long m;
            if (e == 0)
                m = (bits & 0xfffffffffffffL) << 1;
            else
                m = (bits & 0xfffffffffffffL) | 0x10000000000000L;
            LispObject significand = number(m);
            Fixnum exponent = Fixnum.makeFixnum(e - 1075);
            Fixnum sign = Fixnum.makeFixnum(s);
            LispObject result = significand;
            result =
                result.multiplyBy(MathFunctions.EXPT.execute(Fixnum.TWO, exponent));
            result = result.multiplyBy(sign);
            // Calculate remainder.
            LispObject product = result.multiplyBy(obj);
            LispObject remainder = subtract(product);
            return thread.setValues(result, remainder);
        }
        return error(new TypeError(obj, SymbolConstants.REAL));
    }

    @Override
    public int clHash()
    {
        return Float.floatToIntBits(floatValue());
    }

    @Override
    public int psxhash()
    {
        if ((floatValue() % 1) == 0)
            return (((int)floatValue()) & 0x7fffffff);
        else
            return (clHash() & 0x7fffffff);
    }

    @Override
    public String writeToString() throws ConditionThrowable
    {
        if (floatValue() == Float.POSITIVE_INFINITY) {
            StringBuffer sb = new StringBuffer("#.");
            sb.append(SymbolConstants.SINGLE_FLOAT_POSITIVE_INFINITY.writeToString());
            return sb.toString();
        }
        if (floatValue() == Float.NEGATIVE_INFINITY) {
            StringBuffer sb = new StringBuffer("#.");
            sb.append(SymbolConstants.SINGLE_FLOAT_NEGATIVE_INFINITY.writeToString());
            return sb.toString();
        }

        LispThread thread = LispThread.currentThread();
        boolean printReadably = SymbolConstants.PRINT_READABLY.symbolValue(thread) != NIL;

        if (floatValue() != floatValue()) {
            if (printReadably)
                return "#.(progn \"Comment: create a NaN.\" (/ 0.0s0 0.0s0))";
            else
                return "#<SINGLE-FLOAT NaN>";
        }
        String s1 = String.valueOf(floatValue());
        if (printReadably ||
            !memq(SymbolConstants.READ_DEFAULT_FLOAT_FORMAT.symbolValue(thread),
                  list(SymbolConstants.SINGLE_FLOAT, SymbolConstants.SHORT_FLOAT)))
        {
            if (s1.indexOf('E') >= 0)
                return s1.replace('E', 'f');
            else
                return s1.concat("f0");
        } else
            return s1;
    }

    public LispObject rational() throws ConditionThrowable
    {
        final int bits = Float.floatToRawIntBits(floatValue());
        int sign = ((bits >> 31) == 0) ? 1 : -1;
        int storedExponent = ((bits >> 23) & 0xff);
        long mantissa;
        if (storedExponent == 0)
            mantissa = (bits & 0x7fffff) << 1;
        else
            mantissa = (bits & 0x7fffff) | 0x800000;
        if (mantissa == 0)
            return Fixnum.ZERO;
        if (sign < 0)
            mantissa = -mantissa;
        // Subtract bias.
        final int exponent = storedExponent - 127;
        BigInteger numerator, denominator;
        if (exponent < 0) {
            numerator = BigInteger.valueOf(mantissa);
            denominator = BigInteger.valueOf(1).shiftLeft(23 - exponent);
        } else {
            numerator = BigInteger.valueOf(mantissa).shiftLeft(exponent);
            denominator = BigInteger.valueOf(0x800000); // (ash 1 23)
        }
        return number(numerator, denominator);
    }
}
