/*
* Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
*  
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*  
*  * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*  * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer
*       in the documentation and/or other materials provided with the
*       distribution.
*  * Neither the names of the Naval Postgraduate School (NPS)
*       Modeling Virtual Environments and Simulation (MOVES) Institute
*       (http://www.nps.edu and http://www.MovesInstitute.org)
*       nor the names of its contributors may be used to endorse or
*       promote products derived from this software without specific
*       prior written permission.
*  
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
* FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
* COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
* LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
* ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package edu.nps.moves.mmowgli.utility;

/**
 * SysOut.java
 * Created on Feb 23, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 * 
 * A synchronizer for System.out.println message;
 * (is this really necessary?  seemed like a good idea)
 */
public class SysOut
{
  private static Object syncher = new Object();
  //@formatter:off  
  public static void print(boolean b)   {synchronized(syncher) {System.out.print(b);}}
  public static void print(char c)      {synchronized(syncher) {System.out.print(c);}}
  public static void print(char[] s)    {synchronized(syncher) {System.out.print(s);}}
  public static void print(double d)    {synchronized(syncher) {System.out.print(d);}}
  public static void print(float f)     {synchronized(syncher) {System.out.print(f);}}
  public static void print(int i)       {synchronized(syncher) {System.out.print(i);}}
  public static void print(long l)      {synchronized(syncher) {System.out.print(l);}}
  public static void print(Object obj)  {synchronized(syncher) {System.out.print(obj);}}
  public static void print(String s)    {synchronized(syncher) {System.out.print(s);}}
  public static void println()          {synchronized(syncher) {System.out.println();}}
  public static void println(boolean b) {synchronized(syncher) {System.out.println(b);}}
  public static void println(char c)    {synchronized(syncher) {System.out.println(c);}}
  public static void println(char[] ca) {synchronized(syncher) {System.out.println(ca);}}
  public static void println(double d)  {synchronized(syncher) {System.out.println(d);}}
  public static void println(float f)   {synchronized(syncher) {System.out.println(f);}}
  public static void println(int i)     {synchronized(syncher) {System.out.println(i);}}
  public static void println(long l)    {synchronized(syncher) {System.out.println(l);}}
  public static void println(Object obj){synchronized(syncher) {System.out.println(obj);}}
  public static void println(String s)  {synchronized(syncher) {System.out.println(s);}}
  //@formatter:on
}
