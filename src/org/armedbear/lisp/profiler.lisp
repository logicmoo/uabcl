;;; profiler.lisp
;;;
;;; Copyright (C) 2003-2005 Peter Graves
;;; $Id: profiler.lisp 11391 2008-11-15 22:38:34Z vvoutilainen $
;;;
;;; This program is free software; you can redistribute it and/or
;;; modify it under the terms of the GNU General Public License
;;; as published by the Free Software Foundation; either version 2
;;; of the License, or (at your option) any later version.
;;;
;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU General Public License for more details.
;;;
;;; You should have received a copy of the GNU General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
;;;
;;; As a special exception, the copyright holders of this library give you
;;; permission to link this library with independent modules to produce an
;;; executable, regardless of the license terms of these independent
;;; modules, and to copy and distribute the resulting executable under
;;; terms of your choice, provided that you also meet, for each linked
;;; independent module, the terms and conditions of the license of that
;;; module.  An independent module is a module which is not derived from
;;; or based on this library.  If you modify this library, you may extend
;;; this exception to your version of the library, but you are not
;;; obligated to do so.  If you do not wish to do so, delete this
;;; exception statement from your version.

(in-package #:profiler)

(export '(*hidden-functions*))

(require '#:clos)
(require '#:format)

(defvar *type* nil)

(defvar *granularity* 1 "Sampling interval (in milliseconds).")

(defvar *hidden-functions*
  '(funcall apply eval
    sys::%eval sys::interactive-eval
    tpl::repl tpl::top-level-loop))

(defstruct (profile-info
            (:constructor make-profile-info (object count)))
  object
  count)

;; Returns list of all symbols with non-zero call counts.
(defun list-called-objects ()
  (let ((result '()))
    (dolist (pkg (list-all-packages))
      (dolist (sym (sys:package-symbols pkg))
        (unless (memq sym *hidden-functions*)
          (when (fboundp sym)
            (let* ((definition (fdefinition sym))
                   (count (sys:call-count definition)))
              (unless (zerop count)
                (cond ((typep definition 'generic-function)
                       (push (make-profile-info definition count) result)
                       (dolist (method (mop::generic-function-methods definition))
                         (setf count (sys:call-count (sys:%method-function method)))
                         (unless (zerop count)
                           (push (make-profile-info method count) result))))
                      (t
                       (push (make-profile-info sym count) result)))))))))
    (remove-duplicates result :key 'profile-info-object :test 'eq)))

(defun object-name (object)
  (cond ((symbolp object)
         object)
        ((typep object 'generic-function)
         (sys:%generic-function-name object))
        ((typep object 'method)
         (list 'METHOD
               (sys:%generic-function-name (sys:%method-generic-function object))
               (sys:%method-specializers object)))))

(defun object-compiled-function-p (object)
  (cond ((symbolp object)
         (compiled-function-p (fdefinition object)))
        ((typep object 'method)
         (compiled-function-p (sys:%method-function object)))
        (t
         (compiled-function-p object))))

(defun show-call-count (info max-count)
  (let* ((object (profile-info-object info))
         (count (profile-info-count info)))
    (if max-count
        (format t "~5,1F ~8D ~S~A~%"
                (/ (* count 100.0) max-count)
                count
                (object-name object)
                (if (object-compiled-function-p object)
                    ""
                    " [interpreted function]"))
        (format t "~8D ~S~A~%"
                count
                (object-name object)
                (if (object-compiled-function-p object)
                    ""
                    " [interpreted function]")))))

(defun show-call-counts ()
  (let ((list (list-called-objects)))
    (setf list (sort list #'< :key 'profile-info-count))
    (let ((max-count nil))
      (when (eq *type* :time)
        (let ((last-info (car (last list))))
          (setf max-count (if last-info
                              (profile-info-count last-info)
                              nil))
          (when (eql max-count 0)
            (setf max-count nil))))
      (dolist (info list)
        (show-call-count info max-count))))
  (values))

(defun start-profiler (&key type)
  "Starts the profiler.
  :TYPE may be either :TIME (statistical sampling) or :COUNT-ONLY (exact call
  counts)."
  (unless type
    (setf type :time))
  (unless (memq type '(:time :count-only))
    (error ":TYPE must be :TIME or :COUNT-ONLY"))
  (setf *type* type)
  (%start-profiler type *granularity*))

(defmacro with-profiling ((&key type) &body body)
  `(unwind-protect (progn (start-profiler :type ,type) ,@body)
                   (stop-profiler)))
