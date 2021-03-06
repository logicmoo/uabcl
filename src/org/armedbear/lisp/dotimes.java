/*
 * dotimes.java
 *
 * Copyright (C) 2003-2006 Peter Graves
 * $Id: dotimes.java 12167 2009-09-29 21:18:55Z ehuelsmann $
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
import static org.armedbear.lisp.Lisp.*;

public final class dotimes extends SpecialOperator
{
  private dotimes()
  {
    super(SymbolConstants.DOTIMES);
  }

  @Override
  public LispObject execute(LispObject args, Environment env)
    throws ConditionThrowable
  {
    LispObject bodyForm = args.CDR();
    args = args.CAR();
    Symbol var = checkSymbol(args.CAR());
    LispObject countForm = args.CADR();
    final LispThread thread = LispThread.currentThread();
    LispObject resultForm = args.CDR().CDR().CAR();
    SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;

    LispObject bodyAndDecls = parseBody(bodyForm, false);
    LispObject specials = parseSpecials(bodyAndDecls.NTH(1));
    bodyForm = bodyAndDecls.CAR();

    LispObject blockId = new BlockLispObject();
    try
      {
        Environment ext = new Environment(env);
        ext.addBlock(NIL, blockId);

        LispObject limit = eval(countForm, ext, thread);
        LispObject localTags = preprocessTagBody(bodyForm, ext);

        LispObject result;
        // Establish a reusable binding.
        final Object binding;
        if (specials != NIL && memq(var, specials))
          {
            thread.bindSpecial(var, null);
            binding = thread.getSpecialBinding(var);
            ext.declareSpecial(var);
          }
        else if (var.isSpecialVariable())
          {
            thread.bindSpecial(var, null);
            binding = thread.getSpecialBinding(var);
          }
        else
          {
            ext.bindLispSymbol(var, null);
            binding = ext.getBinding(var);
          }
        while (specials != NIL)
          {
            ext.declareSpecial(checkSymbol(specials.CAR()));
            specials = specials.CDR();
          }
        if (limit instanceof Fixnum)
          {
            int count = ((Fixnum)limit).intValue();
            int i;
            for (i = 0; i < count; i++)
              {
                if (binding instanceof SpecialBinding)
                  ((SpecialBinding)binding).value = Fixnum.makeFixnum(i);
                else
                  ((Binding)binding).value = Fixnum.makeFixnum(i);

                processTagBody(bodyForm, localTags, ext);

                if (interrupted)
                  handleInterrupt();
              }
            if (binding instanceof SpecialBinding)
              ((SpecialBinding)binding).value = Fixnum.makeFixnum(i);
            else
              ((Binding)binding).value = Fixnum.makeFixnum(i);
            result = eval(resultForm, ext, thread);
          }
        else if (limit instanceof Bignum)
          {
            LispObject i = Fixnum.ZERO;
            while (i.isLessThan(limit))
              {
                if (binding instanceof SpecialBinding)
                  ((SpecialBinding)binding).value = i;
                else
                  ((Binding)binding).value = i;

                processTagBody(bodyForm, localTags, ext);

                i = i.incr();
                if (interrupted)
                  handleInterrupt();
              }
            if (binding instanceof SpecialBinding)
              ((SpecialBinding)binding).value = i;
            else
              ((Binding)binding).value = i;
            result = eval(resultForm, ext, thread);
          }
        else
          return error(new TypeError(limit, SymbolConstants.INTEGER));
        return result;
      }
    catch (Return ret)
      {
        if (ret.getBlock() == blockId)
          {
            return ret.getResult();
          }
        throw ret;
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }

  private static final dotimes DOTIMES = new dotimes();
}
