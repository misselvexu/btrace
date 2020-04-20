/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.btrace.instr;

import org.openjdk.btrace.core.BTraceRuntime;
import org.openjdk.btrace.instr.random.SharedRandomIntProvider;
import org.openjdk.btrace.instr.random.ThreadLocalRandomIntProvider;

/** @author Jaroslav Bachorik */
@SuppressWarnings("LiteralClassName")
public abstract class RandomIntProvider {
  private static final RandomIntProvider rndIntProvider;
  // for the testability purposes; BTraceRuntime initializes Unsafe instance
  // and fails under JUnit
  private static volatile boolean useBtraceEnter = true;

  static {
    boolean entered = false;
    try {
      if (useBtraceEnter) {
        entered = BTraceRuntime.enter();
      }
      Class clz = null;
      try {
        clz = Class.forName("java.util.concurrent.ThreadLocalRandom");
      } catch (Throwable e) {
        // ThreadLocalRandom not accessible -> pre JDK8
      }
      if (clz != null) {
        rndIntProvider = new ThreadLocalRandomIntProvider();
      } else {
        rndIntProvider = new SharedRandomIntProvider();
      }
    } finally {
      if (entered) BTraceRuntime.leave();
    }
  }

  protected RandomIntProvider() {}

  public static RandomIntProvider getInstance() {
    return rndIntProvider;
  }

  public abstract int nextInt(int bound);
}
