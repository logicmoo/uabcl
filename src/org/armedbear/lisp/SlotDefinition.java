/*
 * SlotDefinition.java
 *
 * Copyright (C) 2005 Peter Graves
 * $Id: SlotDefinition.java 11754 2009-04-12 10:53:39Z vvoutilainen $
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

public final class SlotDefinition extends AbstractStandardObject
{
	@Override
	public LispObject[] getSlots() {
		// TODO Auto-generated method stub
		return slots;
	}
	@Override
	public void setSlots(LispObject[] lispObjects) {
		slots = lispObjects;
	}
	  private LispObject[] slots;
	  
	  public int getInstanceSlotLength() throws ConditionThrowable {
			// TODO Auto-generated method stub
			return slots.length;
		}
	  public Layout getLayout() {
	    return layout;
	  }
	  public void setLayout(Layout checkLayout) {
		  layout = checkLayout;
	  }
	  public LispObject getSlot(int index) {
	      try
	      {
	        return slots[index];
	      }
	    catch (ArrayIndexOutOfBoundsException e)
	      {
	        return type_error(Fixnum.makeFixnum(index),
	                               list(SymbolConstants.INTEGER, Fixnum.ZERO,
	                                     Fixnum.makeFixnum(getInstanceSlotLength())));
	      }
	  }
	  public void setSlot(int index, LispObject value) {
	      try
	      {
	        slots[index] = value;
	      }
	    catch (ArrayIndexOutOfBoundsException e)
	      {
	        type_error(Fixnum.makeFixnum(index),
	                               list(SymbolConstants.INTEGER, Fixnum.ZERO,
	                                     Fixnum.makeFixnum(getInstanceSlotLength())));
	      }
	  }
  public SlotDefinition()
  {
	    super(StandardClass.SLOT_DEFINITION.getClassLayout());
	    slots = new LispObject[layout.getLength()];
	    for (int i = slots.length; i-- > 0;)
	      slots[i] = UNBOUND_VALUE;
	    slots[SlotDefinitionClass.SLOT_INDEX_LOCATION] = NIL;
  }

  public SlotDefinition(LispObject name, LispObject readers)
  {
    this();
    try
      {
        Debug.assertTrue(name instanceof Symbol);
        slots[SlotDefinitionClass.SLOT_INDEX_NAME] = name;
        slots[SlotDefinitionClass.SLOT_INDEX_INITFUNCTION] = NIL;
        slots[SlotDefinitionClass.SLOT_INDEX_INITARGS] =
          makeCons(PACKAGE_KEYWORD.intern(((Symbol)name).getName()));
        slots[SlotDefinitionClass.SLOT_INDEX_READERS] = readers;
        slots[SlotDefinitionClass.SLOT_INDEX_ALLOCATION] = Keyword.INSTANCE;
      }
    catch (Throwable t)
      {
        Debug.trace(t);
      }
  }

  public SlotDefinition(LispObject name, LispObject readers,
                        LispObject initForm)
  {
    this();
    try
      {
        Debug.assertTrue(name instanceof Symbol);
        slots[SlotDefinitionClass.SLOT_INDEX_NAME] = name;
        slots[SlotDefinitionClass.SLOT_INDEX_INITFUNCTION] = NIL;
        slots[SlotDefinitionClass.SLOT_INDEX_INITFORM] = initForm;
        slots[SlotDefinitionClass.SLOT_INDEX_INITARGS] =
          makeCons(PACKAGE_KEYWORD.intern(((Symbol)name).getName()));
        slots[SlotDefinitionClass.SLOT_INDEX_READERS] = readers;
        slots[SlotDefinitionClass.SLOT_INDEX_ALLOCATION] = Keyword.INSTANCE;
      }
    catch (Throwable t)
      {
        Debug.trace(t);
      }
  }
  
  public static SlotDefinition checkSlotDefination(LispObject obj) throws ConditionThrowable {
          if (obj instanceof SlotDefinition) return (SlotDefinition)obj;
      return (SlotDefinition)type_error(obj, SymbolConstants.SLOT_DEFINITION);     
  }

  public final LispObject getName()
  {
    return slots[SlotDefinitionClass.SLOT_INDEX_NAME];
  }

  public final void setLocation(int i)
  {
    slots[SlotDefinitionClass.SLOT_INDEX_LOCATION] = Fixnum.makeFixnum(i);
  }

  @Override
  public String writeToString() throws ConditionThrowable
  {
    FastStringBuffer sb =
      new FastStringBuffer(SymbolConstants.SLOT_DEFINITION.writeToString());
    LispObject name = slots[SlotDefinitionClass.SLOT_INDEX_NAME];
    if (name != null && name != NIL)
      {
        sb.append(' ');
        sb.append(name.writeToString());
      }
    return unreadableString(sb.toString());
  }

  // ### make-slot-definition
  private static final Primitive MAKE_SLOT_DEFINITION =
    new Primitive("make-slot-definition", PACKAGE_SYS, true, "")
    {
      @Override
      public LispObject execute() throws ConditionThrowable
      {
        return new SlotDefinition();
      }
    };

  // ### %slot-definition-name
  private static final Primitive _SLOT_DEFINITION_NAME =
    new Primitive(SymbolConstants._SLOT_DEFINITION_NAME, "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_NAME];
      }
    };

  // ### set-slot-definition-name
  private static final Primitive SET_SLOT_DEFINITION_NAME =
    new Primitive("set-slot-definition-name", PACKAGE_SYS, true,
                  "slot-definition name")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_NAME] = second;
          return second;
      }
    };

  // ### %slot-definition-initfunction
  private static final Primitive _SLOT_DEFINITION_INITFUNCTION =
    new Primitive(SymbolConstants._SLOT_DEFINITION_INITFUNCTION, "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_INITFUNCTION];
      }
    };

  // ### set-slot-definition-initfunction
  private static final Primitive SET_SLOT_DEFINITION_INITFUNCTION =
    new Primitive("set-slot-definition-initfunction", PACKAGE_SYS, true,
                  "slot-definition initfunction")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_INITFUNCTION] = second;
          return second;
      }
    };

  // ### %slot-definition-initform
  private static final Primitive _SLOT_DEFINITION_INITFORM =
    new Primitive("%slot-definition-initform", PACKAGE_SYS, true,
                  "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_INITFORM];
      }
    };

  // ### set-slot-definition-initform
  private static final Primitive SET_SLOT_DEFINITION_INITFORM =
    new Primitive("set-slot-definition-initform", PACKAGE_SYS, true,
                  "slot-definition initform")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_INITFORM] = second;
          return second;
      }
    };

  // ### %slot-definition-initargs
  private static final Primitive _SLOT_DEFINITION_INITARGS =
    new Primitive(SymbolConstants._SLOT_DEFINITION_INITARGS, "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_INITARGS];
      }
    };

  // ### set-slot-definition-initargs
  private static final Primitive SET_SLOT_DEFINITION_INITARGS =
    new Primitive("set-slot-definition-initargs", PACKAGE_SYS, true,
                  "slot-definition initargs")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_INITARGS] = second;
          return second;
      }
    };

  // ### %slot-definition-readers
  private static final Primitive _SLOT_DEFINITION_READERS =
    new Primitive("%slot-definition-readers", PACKAGE_SYS, true,
                  "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_READERS];
      }
    };

  // ### set-slot-definition-readers
  private static final Primitive SET_SLOT_DEFINITION_READERS =
    new Primitive("set-slot-definition-readers", PACKAGE_SYS, true,
                  "slot-definition readers")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_READERS] = second;
          return second;
      }
    };

  // ### %slot-definition-writers
  private static final Primitive _SLOT_DEFINITION_WRITERS =
    new Primitive("%slot-definition-writers", PACKAGE_SYS, true,
                  "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_WRITERS];
      }
    };

  // ### set-slot-definition-writers
  private static final Primitive SET_SLOT_DEFINITION_WRITERS =
    new Primitive("set-slot-definition-writers", PACKAGE_SYS, true,
                  "slot-definition writers")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_WRITERS] = second;
          return second;
      }
    };

  // ### %slot-definition-allocation
  private static final Primitive _SLOT_DEFINITION_ALLOCATION =
    new Primitive("%slot-definition-allocation", PACKAGE_SYS, true,
                  "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_ALLOCATION];
      }
    };

  // ### set-slot-definition-allocation
  private static final Primitive SET_SLOT_DEFINITION_ALLOCATION =
    new Primitive("set-slot-definition-allocation", PACKAGE_SYS, true,
                  "slot-definition allocation")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_ALLOCATION] = second;
          return second;
      }
    };

  // ### %slot-definition-allocation-class
  private static final Primitive _SLOT_DEFINITION_ALLOCATION_CLASS =
    new Primitive("%slot-definition-allocation-class", PACKAGE_SYS, true,
                  "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_ALLOCATION_CLASS];
      }
    };

  // ### set-slot-definition-allocation-class
  private static final Primitive SET_SLOT_DEFINITION_ALLOCATION_CLASS =
    new Primitive("set-slot-definition-allocation-class", PACKAGE_SYS, true,
                  "slot-definition allocation-class")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_ALLOCATION_CLASS] = second;
          return second;
      }
    };

  // ### %slot-definition-location
  private static final Primitive _SLOT_DEFINITION_LOCATION =
    new Primitive("%slot-definition-location", PACKAGE_SYS, true, "slot-definition")
    {
      @Override
      public LispObject execute(LispObject arg) throws ConditionThrowable
      {
          return checkSlotDefination(arg).slots[SlotDefinitionClass.SLOT_INDEX_LOCATION];
      }
    };

  // ### set-slot-definition-location
  private static final Primitive SET_SLOT_DEFINITION_LOCATION =
    new Primitive("set-slot-definition-location", PACKAGE_SYS, true, "slot-definition location")
    {
      @Override
      public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
      {
          checkSlotDefination(first).slots[SlotDefinitionClass.SLOT_INDEX_LOCATION] = second;
          return second;
      }
    };
}
