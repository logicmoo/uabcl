/*
 * Closure.java
 *
 * Copyright (C) 2002-2008 Peter Graves
 * Copyright (C) 2008 Ville Voutilainen
 * $Id: Closure.java 11778 2009-04-23 20:46:27Z vvoutilainen $
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

import java.util.ArrayList;

public class Closure extends Function
{
  // Parameter types.
  static final int REQUIRED = 0;
  static final int OPTIONAL = 1;
  static final int KEYWORD  = 2;
  static final int REST     = 3;
  static final int AUX      = 4;

  // States.
  private static final int STATE_REQUIRED = REQUIRED;
  private static final int STATE_OPTIONAL = OPTIONAL;
  private static final int STATE_KEYWORD  = KEYWORD;
  private static final int STATE_REST     = REST;
  private static final int STATE_AUX      = AUX;

  private static final Parameter[] emptyParameterArray;
  static 
    {
        emptyParameterArray = new Parameter[0];
    }
  private Parameter[] requiredParameters = emptyParameterArray;
  private Parameter[] optionalParameters = emptyParameterArray;
  private Parameter[] keywordParameters = emptyParameterArray;
  private Parameter[] auxVars = emptyParameterArray;
  private final LispObject body;
  private final LispObject executionBody;
  private final Environment environment;
  private final boolean andKey;
  private final boolean allowOtherKeys;
  private Symbol restVar;
  private Symbol envVar;
  private int arity;

  private int minArgs;
  private int maxArgs;

  private static final Symbol[] emptySymbolArray;
  static 
    {
        emptySymbolArray = new Symbol[0];
    }
  private Symbol[] variables = emptySymbolArray;
  private LispObject specials = NIL;

  private boolean bindInitForms;

  public Closure(LispObject lambdaExpression, Environment env)
    throws ConditionThrowable
  {
    this(null, lambdaExpression, env);
  }

  public Closure(final LispObject name, final LispObject lambdaExpression,
                 final Environment env)
    throws ConditionThrowable
  {
    super(name, lambdaExpression.CADR());
    final LispObject lambdaList = lambdaExpression.CADR();
    setLambdaList(lambdaList);
    if (!(lambdaList == NIL || lambdaList instanceof Cons))
      error(new LispError("The lambda list " + lambdaList.writeToString() +
                           " is invalid."));
    boolean _andKey = false;
    boolean _allowOtherKeys = false;
    if (lambdaList instanceof Cons)
      {
        final int length = lambdaList.size();
        ArrayList<Parameter> required = null;
        ArrayList<Parameter> optional = null;
        ArrayList<Parameter> keywords = null;
        ArrayList<Parameter> aux = null;
        int state = STATE_REQUIRED;
        LispObject remaining = lambdaList;
        while (remaining != NIL)
          {
            LispObject obj = remaining.CAR();
            if (obj instanceof Symbol)
              {
                if (state == STATE_AUX)
                  {
                    if (aux == null)
                      aux = new ArrayList<Parameter>();
                    aux.add(new Parameter((Symbol)obj, NIL, AUX));
                  }
                else if (obj == SymbolConstants.AND_OPTIONAL)
                  {
                    state = STATE_OPTIONAL;
                    arity = -1;
                  }
                else if (obj == SymbolConstants.AND_REST || obj == SymbolConstants.AND_BODY)
                  {
                    state = STATE_REST;
                    arity = -1;
                    maxArgs = -1;
                    remaining = remaining.CDR();
                    if (remaining == NIL)
                      {
                        error(new LispError(
                          "&REST/&BODY must be followed by a variable."));
                      }
                    Debug.assertTrue(restVar == null);
                    final LispObject remainingcar =  remaining.CAR();
                    if (remainingcar instanceof Symbol)
                      {
                        restVar = (Symbol) remainingcar;
                      }
                    else
                      {
                        error(new LispError(
                          "&REST/&BODY must be followed by a variable."));
                      }
                  }
                else if (obj == SymbolConstants.AND_ENVIRONMENT)
                  {
                    remaining = remaining.CDR();
                    envVar = (Symbol) remaining.CAR();
                    arity = -1; // FIXME
                  }
                else if (obj == SymbolConstants.AND_KEY)
                  {
                    state = STATE_KEYWORD;
                    _andKey = true;
                    arity = -1;
                  }
                else if (obj == SymbolConstants.AND_ALLOW_OTHER_KEYS)
                  {
                    _allowOtherKeys = true;
                    maxArgs = -1;
                  }
                else if (obj == SymbolConstants.AND_AUX)
                  {
                    // All remaining specifiers are aux variable specifiers.
                    state = STATE_AUX;
                    arity = -1; // FIXME
                  }
                else
                  {
                    if (state == STATE_OPTIONAL)
                      {
                        if (optional == null)
                          optional = new ArrayList<Parameter>();
                        optional.add(new Parameter((Symbol)obj, NIL, OPTIONAL));
                        if (maxArgs >= 0)
                          ++maxArgs;
                      }
                    else if (state == STATE_KEYWORD)
                      {
                        if (keywords == null)
                          keywords = new ArrayList<Parameter>();
                        keywords.add(new Parameter((Symbol)obj, NIL, KEYWORD));
                        if (maxArgs >= 0)
                          maxArgs += 2;
                      }
                    else
                      {
                        Debug.assertTrue(state == STATE_REQUIRED);
                        if (required == null)
                          required = new ArrayList<Parameter>();
                        required.add(new Parameter((Symbol)obj));
                        if (maxArgs >= 0)
                          ++maxArgs;
                      }
                  }
              }
            else if (obj instanceof Cons)
              {
                if (state == STATE_AUX)
                  {
                    Symbol sym = checkSymbol(obj.CAR());
                    LispObject initForm = obj.CADR();
                    Debug.assertTrue(initForm != null);
                    if (aux == null)
                      aux = new ArrayList<Parameter>();
                    aux.add(new Parameter(sym, initForm, AUX));
                  }
                else if (state == STATE_OPTIONAL)
                  {
                    Symbol sym = checkSymbol(obj.CAR());
                    LispObject initForm = obj.CADR();
                    LispObject svar = obj.CDR().CDR().CAR();
                    if (optional == null)
                      optional = new ArrayList<Parameter>();
                    optional.add(new Parameter(sym, initForm, svar, OPTIONAL));
                    if (maxArgs >= 0)
                      ++maxArgs;
                  }
                else if (state == STATE_KEYWORD)
                  {
                    Symbol keyword;
                    Symbol var;
                    LispObject initForm = NIL;
                    LispObject svar = NIL;
                    LispObject first = obj.CAR();
                    if (first instanceof Cons)
                      {
                        keyword = checkSymbol(first.CAR());
                        var = checkSymbol(first.CADR());
                      }
                    else
                      {
                        var = checkSymbol(first);
                        keyword =
                          PACKAGE_KEYWORD.intern(var.getSymbolName());
                      }
                    obj = obj.CDR();
                    if (obj != NIL)
                      {
                        initForm = obj.CAR();
                        obj = obj.CDR();
                        if (obj != NIL)
                          svar = obj.CAR();
                      }
                    if (keywords == null)
                      keywords = new ArrayList<Parameter>();
                    keywords.add(new Parameter(keyword, var, initForm, svar));
                    if (maxArgs >= 0)
                      maxArgs += 2;
                  }
                else
                  invalidParameter(obj);
              }
            else
              invalidParameter(obj);
            remaining = remaining.CDR();
          }
        if (arity == 0)
          arity = length;
        if (required != null)
          {
            requiredParameters = new Parameter[required.size()];
            required.toArray(requiredParameters);
          }
        if (optional != null)
          {
            optionalParameters = new Parameter[optional.size()];
            optional.toArray(optionalParameters);
          }
        if (keywords != null)
          {
            keywordParameters = new Parameter[keywords.size()];
            keywords.toArray(keywordParameters);
          }
        if (aux != null)
          {
            auxVars = new Parameter[aux.size()];
            aux.toArray(auxVars);
          }
      }
    else
      {
        // Lambda list is empty.
        Debug.assertTrue(lambdaList == NIL);
        arity = 0;
        maxArgs = 0;
      }
    this.body = lambdaExpression.CDDR();
    LispObject bodyAndDecls = parseBody(this.body, false);
    this.executionBody = bodyAndDecls.CAR();
    this.specials = parseSpecials(bodyAndDecls.NTH(1));

    this.environment = env;
    this.andKey = _andKey;
    this.allowOtherKeys = _allowOtherKeys;
    minArgs = requiredParameters.length;
    if (arity >= 0)
      Debug.assertTrue(arity == minArgs);
    variables = processVariables();
  }

  private final void processParameters(ArrayList<Symbol> vars,
                                       final Parameter[] parameters)
  {
    for (Parameter parameter : parameters)
      {
        vars.add(parameter.var);
        if (parameter.svar != NIL)
          vars.add((Symbol)parameter.svar);
        if (!bindInitForms)
          if (!parameter.initForm.constantp())
            bindInitForms = true;
      }
  }

  // Also sets bindInitForms.
  private final Symbol[] processVariables()
  {
    ArrayList<Symbol> vars = new ArrayList<Symbol>();
    for (Parameter parameter : requiredParameters)
      vars.add(parameter.var);
    processParameters(vars, optionalParameters);
    if (restVar != null)
      {
        vars.add(restVar);
      }
    processParameters(vars, keywordParameters);
    Symbol[] array = new Symbol[vars.size()];
    vars.toArray(array);
    return array;
  }

  private static final void invalidParameter(LispObject obj)
    throws ConditionThrowable
  {
    error(new LispError(obj.writeToString() +
                         " may not be used as a variable in a lambda list."));
  }

  @Override
  public LispObject typep(LispObject typeSpecifier) throws ConditionThrowable
  {
    if (typeSpecifier == SymbolConstants.COMPILED_FUNCTION)
      return NIL;
    return super.typep(typeSpecifier);
  }

  public final LispObject getVariableList()
  {
    LispObject result = NIL;
    for (int i = variables.length; i-- > 0;)
      result = makeCons(variables[i], result);
    return result;
  }

  // Returns body as a list.
  public final LispObject getBody()
  {
    return body;
  }

  public final Environment getEnvironment()
  {
    return environment;
  }

  @Override
  public LispObject execute() throws ConditionThrowable
  {
    if (arity == 0)
      {
        return progn(executionBody, environment, 
                     LispThread.currentThread());
      }
    else
      return execute(new LispObject[0]);
  }
    
  private final LispObject bindParametersAndExecute(LispObject... objects)
  throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    final SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    Environment ext = new Environment(environment);
    bindRequiredParameters(ext, thread, objects);
    if (arity != minArgs)
      {
        bindParameterDefaults(optionalParameters, ext, thread);
        if (restVar != null)
          bindArg(specials, restVar, NIL, ext, thread);
        bindParameterDefaults(keywordParameters, ext, thread);
      }
    bindAuxVars(ext, thread);
    declareFreeSpecials(ext);
    try
      {
        return progn(executionBody, ext, thread);
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }

  private final void bindRequiredParameters(Environment ext,
                                            LispThread thread,
                                            LispObject[] objects)
  throws ConditionThrowable
  {
    // &whole and &environment before anything
    if (envVar != null)
      bindArg(specials, envVar, environment, ext, thread);
    for (int i = 0; i < objects.length; ++i)
      {
        bindArg(specials, requiredParameters[i].var, objects[i], ext, thread);
      }
  }

  public final LispObject invokeArrayExecute(LispObject... objects)
  throws ConditionThrowable
  {
    return execute(objects);
  }

  @Override
  public LispObject execute(LispObject arg) throws ConditionThrowable
  {
    if (minArgs == 1)
      {
        return bindParametersAndExecute(arg);
      }
    else
      {
        return invokeArrayExecute(arg);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second)
    throws ConditionThrowable
  {
    if (minArgs == 2)
      {
        return bindParametersAndExecute(first, second);
      }
    else
      {
        return invokeArrayExecute(first, second);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third)
    throws ConditionThrowable
  {
    if (minArgs == 3)
      {
        return bindParametersAndExecute(first, second, third);
      }
    else
      {
        return invokeArrayExecute(first, second, third);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth)
    throws ConditionThrowable
  {
    if (minArgs == 4)
      {
        return bindParametersAndExecute(first, second, third, fourth);
      }
    else
      {
        return invokeArrayExecute(first, second, third, fourth);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth)
    throws ConditionThrowable
  {
    if (minArgs == 5)
      {
        return bindParametersAndExecute(first, second, third, fourth,
                                        fifth);
      }
    else
      {
        return invokeArrayExecute(first, second, third, fourth, fifth);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth)
    throws ConditionThrowable
  {
    if (minArgs == 6)
      {
        return bindParametersAndExecute(first, second, third, fourth,
                                        fifth, sixth);
      }
    else
      {
        return invokeArrayExecute(first, second, third, fourth, fifth,
                                  sixth);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth,
                            LispObject seventh)
    throws ConditionThrowable
  {
    if (minArgs == 7)
      {
        return bindParametersAndExecute(first, second, third, fourth,
                               fifth, sixth, seventh);
      }
    else
      {
        return invokeArrayExecute(first, second, third, fourth, fifth,
                                  sixth, seventh);
      }
  }

  @Override
  public LispObject execute(LispObject first, LispObject second,
                            LispObject third, LispObject fourth,
                            LispObject fifth, LispObject sixth,
                            LispObject seventh, LispObject eighth)
    throws ConditionThrowable
  {
    if (minArgs == 8)
      {
        return bindParametersAndExecute(first, second, third, fourth,
                               fifth, sixth, seventh, eighth);
      }
    else
      {
        return invokeArrayExecute(first, second, third, fourth, fifth,
                                  sixth, seventh, eighth);
      }
  }

  private final void declareFreeSpecials(Environment ext)
    throws ConditionThrowable
  {
    LispObject s = specials;
    special:
    while (s != NIL) {
      Symbol special = (Symbol)s.CAR();
      s = s.CDR();
      for (Symbol var : variables)
	if (special == var)
          continue special;
      for (Parameter parameter : auxVars)
        if (special == parameter.var)
          continue special;
      ext.declareSpecial(special);
    }
  }

  @Override
  public LispObject execute(LispObject[] args) throws ConditionThrowable
  {
    final LispThread thread = LispThread.currentThread();
    SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    Environment ext = new Environment(environment);
    if (optionalParameters.length == 0 && keywordParameters.length == 0)
      args = fastProcessArgs(args);
    else
      args = processArgs(args, thread);
    Debug.assertTrue(args.length == variables.length);
    if (envVar != null)
      {
        bindArg(specials, envVar, environment, ext, thread);
      }
    for (int i = 0; i < variables.length; i++)
      {
        Symbol sym = variables[i];
        bindArg(specials, sym, args[i], ext, thread);
      }
    bindAuxVars(ext, thread);
    declareFreeSpecials(ext);
    try
      {
        return progn(executionBody, ext, thread);
      }
    finally
      {
        thread.lastSpecialBinding = lastSpecialBinding;
      }
  }

  protected final LispObject[] processArgs(LispObject[] args, LispThread thread)
    throws ConditionThrowable
  {
    if (optionalParameters.length == 0 && keywordParameters.length == 0)
      return fastProcessArgs(args);
    final int argsLength = args.length;
    if (arity >= 0)
      {
        // Fixed arity.
        if (argsLength != arity)
          error(new WrongNumberOfArgumentsException(this));
        return args;
      }
    // Not fixed arity.
    if (argsLength < minArgs)
      error(new WrongNumberOfArgumentsException(this));
    final LispObject[] array = new LispObject[variables.length];
    int index = 0;
    // The bindings established here (if any) are lost when this function
    // returns. They are used only in the evaluation of initforms for
    // optional and keyword arguments.
    SpecialBinding lastSpecialBinding = thread.lastSpecialBinding;
    Environment ext = new Environment(environment);
    // Section 3.4.4: "...the &environment parameter is bound along with
    // &whole before any other variables in the lambda list..."
    try {
        if (bindInitForms)
          if (envVar != null)
            bindArg(specials, envVar, environment, ext, thread);
        // Required parameters.
        for (int i = 0; i < minArgs; i++)
          {
            if (bindInitForms)
              bindArg(specials, requiredParameters[i].var, args[i], ext, thread);
            array[index++] = args[i];
          }
        int i = minArgs;
        int argsUsed = minArgs;
        // Optional parameters.
        for (Parameter parameter : optionalParameters)
          {
            if (i < argsLength)
              {
                if (bindInitForms)
                  bindArg(specials, parameter.var, args[i], ext, thread);
                array[index++] = args[i];
                ++argsUsed;
                if (parameter.svar != NIL)
                  {
                    if (bindInitForms)
                      bindArg(specials, (Symbol)parameter.svar, T, ext, thread);
                    array[index++] = T;
                  }
              }
            else
              {
                // We've run out of arguments.
                LispObject value;
                if (parameter.initVal != null)
                  value = parameter.initVal;
                else
                  value = Lisp.eval(parameter.initForm, ext, thread);
                if (bindInitForms)
                  bindArg(specials, parameter.var, value, ext, thread);
                array[index++] = value;
                if (parameter.svar != NIL)
                  {
                    if (bindInitForms)
                      bindArg(specials, (Symbol)parameter.svar, NIL, ext, thread);
                    array[index++] = NIL;
                  }
              }
            ++i;
          }
        // &rest parameter.
        if (restVar != null)
          {
            LispObject rest = NIL;
            for (int j = argsLength; j-- > argsUsed;)
              rest = makeCons(args[j], rest);
            if (bindInitForms)
                bindArg(specials, restVar, rest, ext, thread);
            array[index++] = rest;
          }
        // Keyword parameters.
        if (keywordParameters.length > 0)
          {
            int argsLeft = argsLength - argsUsed;
            if (argsLeft == 0)
              {
                // No keyword arguments were supplied.
                // Bind all keyword parameters to their defaults.
                for (int k = 0; k < keywordParameters.length; k++)
                  {
                    Parameter parameter = keywordParameters[k];
                    LispObject value;
                    if (parameter.initVal != null)
                      value = parameter.initVal;
                    else
                      value = Lisp.eval(parameter.initForm, ext, thread);
                    if (bindInitForms)
                        bindArg(specials, parameter.var, value, ext, thread);
                    array[index++] = value;
                    if (parameter.svar != NIL)
                      {
                        if (bindInitForms)
                            bindArg(specials, (Symbol)parameter.svar, NIL, ext, thread);
                        array[index++] = NIL;
                      }
                  }
              }
            else
              {
                if ((argsLeft % 2) != 0)
                  error(new ProgramError("Odd number of keyword arguments."));
                LispObject allowOtherKeysValue = null;
                for (Parameter parameter : keywordParameters)
                  {
                    Symbol keyword = parameter.keyword;
                    LispObject value = null;
                    boolean unbound = true;
                    for (int j = argsUsed; j < argsLength; j += 2)
                      {
                        if (args[j] == keyword)
                          {
                            if (bindInitForms)
                                bindArg(specials, parameter.var, args[j+1], ext, thread);
                            value = array[index++] = args[j+1];
                            if (parameter.svar != NIL)
                              {
                                if (bindInitForms)
                                    bindArg(specials,(Symbol)parameter.svar, T, ext, thread);
                                array[index++] = T;
                              }
                            args[j] = null;
                            args[j+1] = null;
                            unbound = false;
                            break;
                          }
                      }
                    if (unbound)
                      {
                        if (parameter.initVal != null)
                          value = parameter.initVal;
                        else
                          value = Lisp.eval(parameter.initForm, ext, thread);
                        if (bindInitForms)
                            bindArg(specials, parameter.var, value, ext, thread);
                        array[index++] = value;
                        if (parameter.svar != NIL)
                          {
                            if (bindInitForms)
                                bindArg(specials, (Symbol)parameter.svar, NIL, ext, thread);
                            array[index++] = NIL;
                          }
                      }
                    if (keyword == Keyword.ALLOW_OTHER_KEYS)
                      {
                        if (allowOtherKeysValue == null)
                          allowOtherKeysValue = value;
                      }
                  }
                if (!allowOtherKeys)
                  {
                    if (allowOtherKeysValue == null || allowOtherKeysValue == NIL)
                      {
                        LispObject unrecognizedKeyword = null;
                        for (int j = argsUsed; j < argsLength; j += 2)
                          {
                            LispObject keyword = args[j];
                            if (keyword == null)
                              continue;
                            if (keyword == Keyword.ALLOW_OTHER_KEYS)
                              {
                                if (allowOtherKeysValue == null)
                                  {
                                    allowOtherKeysValue = args[j+1];
                                    if (allowOtherKeysValue != NIL)
                                      break;
                                  }
                                continue;
                              }
                            // Unused keyword argument.
                            boolean ok = false;
                            for (Parameter parameter : keywordParameters)
                              {
                                if (parameter.keyword == keyword)
                                  {
                                    // Found it!
                                    ok = true;
                                    break;
                                  }
                              }
                            if (ok)
                              continue;
                            // Unrecognized keyword argument.
                            if (unrecognizedKeyword == null)
                              unrecognizedKeyword = keyword;
                          }
                        if (unrecognizedKeyword != null)
                          {
                            if (!allowOtherKeys &&
                                (allowOtherKeysValue == null || allowOtherKeysValue == NIL))
                              error(new ProgramError("Unrecognized keyword argument " +
                                                      unrecognizedKeyword.writeToString()));
                          }
                      }
                  }
              }
          }
        else if (argsUsed < argsLength)
          {
            // No keyword parameters.
            if (argsUsed + 2 <= argsLength)
              {
                // Check for :ALLOW-OTHER-KEYS.
                LispObject allowOtherKeysValue = NIL;
                int n = argsUsed;
                while (n < argsLength)
                  {
                    LispObject keyword = args[n];
                    if (keyword == Keyword.ALLOW_OTHER_KEYS)
                      {
                        allowOtherKeysValue = args[n+1];
                        break;
                      }
                    n += 2;
                  }
                if (allowOtherKeys || allowOtherKeysValue != NIL)
                  {
                    // Skip keyword/value pairs.
                    while (argsUsed + 2 <= argsLength)
                      argsUsed += 2;
                  }
                else if (andKey)
                  {
                    LispObject keyword = args[argsUsed];
                    if (keyword == Keyword.ALLOW_OTHER_KEYS)
                      {
                        // Section 3.4.1.4: "Note that if &KEY is present, a
                        // keyword argument of :ALLOW-OTHER-KEYS is always
                        // permitted---regardless of whether the associated
                        // value is true or false."
                        argsUsed += 2;
                      }
                  }
              }
            if (argsUsed < argsLength)
              {
                if (restVar == null)
                  error(new WrongNumberOfArgumentsException(this));
              }
          }
    }
    finally {
        thread.lastSpecialBinding = lastSpecialBinding;
    }
    return array;
  }

  // No optional or keyword parameters.
  protected final LispObject[] fastProcessArgs(LispObject[] args)
    throws ConditionThrowable
  {
    final int argsLength = args.length;
    if (arity >= 0)
      {
        // Fixed arity.
        if (argsLength != arity)
          error(new WrongNumberOfArgumentsException(this));
        return args;
      }
    // Not fixed arity.
    if (argsLength < minArgs)
      error(new WrongNumberOfArgumentsException(this));
    final LispObject[] array = new LispObject[variables.length];
    int index = 0;
    // Required parameters.
    for (int i = 0; i < minArgs; i++)
      {
        array[index++] = args[i];
      }
    int argsUsed = minArgs;
    // &rest parameter.
    if (restVar != null)
      {
        LispObject rest = NIL;
        for (int j = argsLength; j-- > argsUsed;)
          rest = makeCons(args[j], rest);
        array[index++] = rest;
      }
    else if (argsUsed < argsLength)
      {
        // No keyword parameters.
        if (argsUsed + 2 <= argsLength)
          {
            // Check for :ALLOW-OTHER-KEYS.
            LispObject allowOtherKeysValue = NIL;
            int n = argsUsed;
            while (n < argsLength)
              {
                LispObject keyword = args[n];
                if (keyword == Keyword.ALLOW_OTHER_KEYS)
                  {
                    allowOtherKeysValue = args[n+1];
                    break;
                  }
                n += 2;
              }
            if (allowOtherKeys || allowOtherKeysValue != NIL)
              {
                // Skip keyword/value pairs.
                while (argsUsed + 2 <= argsLength)
                  argsUsed += 2;
              }
            else if (andKey)
              {
                LispObject keyword = args[argsUsed];
                if (keyword == Keyword.ALLOW_OTHER_KEYS)
                  {
                    // Section 3.4.1.4: "Note that if &key is present, a
                    // keyword argument of :allow-other-keys is always
                    // permitted---regardless of whether the associated
                    // value is true or false."
                    argsUsed += 2;
                  }
              }
          }
        if (argsUsed < argsLength)
          {
            if (restVar == null)
              error(new WrongNumberOfArgumentsException(this));
          }
      }
    return array;
  }

  private final void bindParameterDefaults(Parameter[] parameters,
                                           Environment env,
                                           LispThread thread)
    throws ConditionThrowable
  {
    for (Parameter parameter : parameters)
      {
        LispObject value;
        if (parameter.initVal != null)
          value = parameter.initVal;
        else
          value = Lisp.eval(parameter.initForm, env, thread);
        bindArg(specials, parameter.var, value, env, thread);
        if (parameter.svar != NIL)
	  bindArg(specials, (Symbol)parameter.svar, NIL, env, thread);
      }
  }

  private final void bindAuxVars(Environment env, LispThread thread)
    throws ConditionThrowable
  {
    // Aux variable processing is analogous to LET* processing.
    for (Parameter parameter : auxVars)
      {
        Symbol sym = parameter.var;
        LispObject value;

        if (parameter.initVal != null)
          value = parameter.initVal;
        else
          value = Lisp.eval(parameter.initForm, env, thread);

        bindArg(specials, sym, value, env, thread);
      }
  }

  // ### lambda-list-names
  private static final Primitive LAMBDA_LIST_NAMES =
      new Primitive("lambda-list-names", PACKAGE_SYS, true)
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
        Closure closure = new Closure(list(SymbolConstants.LAMBDA, arg, NIL), new Environment());
        return closure.getVariableList();
      }
    };
}
