/*
 * zip.java
 *
 * Copyright (C) 2005 Peter Graves
 * $Id: zip.java 11488 2008-12-27 10:50:33Z ehuelsmann $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// ### zip pathname pathnames
public final class zip extends Primitive
{
    private zip()
    {
        super("zip", PACKAGE_SYS, true, "pathname pathnames");
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)
        throws ConditionThrowable
    {
        Pathname zipfilePathname = coerceToPathname(first);
        byte[] buffer = new byte[4096];
        try {
            String zipfileNamestring = zipfilePathname.getNamestring();
            if (zipfileNamestring == null)
                return error(new SimpleError("Pathname has no namestring: " +
                                              zipfilePathname.writeToString()));
            ZipOutputStream out =
                new ZipOutputStream(new FileOutputStream(zipfileNamestring));
            LispObject list = second;
            while (list != NIL) {
                Pathname pathname = coerceToPathname(list.CAR());
                String namestring = pathname.getNamestring();
                if (namestring == null) {
                    // Clean up before signalling error.
                    out.close();
                    File zipfile = new File(zipfileNamestring);
                    zipfile.delete();
                    return error(new SimpleError("Pathname has no namestring: " +
                                                  pathname.writeToString()));
                }
                File file = new File(namestring);
                FileInputStream in = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int n;
                while ((n = in.read(buffer)) > 0)
                    out.write(buffer, 0, n);
                out.closeEntry();
                in.close();
                list = list.CDR();
            }
            out.close();
        }
        catch (IOException e) {
            return error(new LispError(e.getMessage()));
        }
        return zipfilePathname;
    }

    private static final Primitive zip = new zip();
}
