package grails.plugins.springbatch.ui

/*
 * Copyright 2007 Evan A Slatis
 *
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

/**
 * Written under Groovy version 1.5
 *
 * @author hippy
 */
class NaturalSort implements Comparator {
	def compRegEx = /(\d+(?:[.]?\d+)?)|\D+/

	int compare(Object obj1, Object obj2) {
		def m1 = obj1 =~ compRegEx
		def m2 = obj2 =~ compRegEx

		def result = 0
		def limit = Math.min(m1.size(),  m2.size())
		for (int i = 0; !result && i < limit; i++) {
			if (m1[i][1]) {
				result = m2[i][1] ? m1[i][1].toDouble() - m2[i][1].toDouble() : -1
			}
			else {
				result = m2[i][1] ? 1 : m1[i][0].compareToIgnoreCase(m2[i][0])
			}
		}

		return (!result && (m1.size() != m2.size())) ? m1.size() - m2.size() : result
	}
}
