/*
 * This file is part of LibEuFin.
 * Copyright (C) 2021 Taler Systems S.A.

 * LibEuFin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3, or
 * (at your option) any later version.

 * LibEuFin is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General
 * Public License for more details.

 * You should have received a copy of the GNU Affero General Public
 * License along with LibEuFin; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>
 */

package tech.libeufin.nexus

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JsonObjectMaker(val obj: ObjectNode) {
    fun prop(key: String, value: String?) {
        obj.put(key, value)
    }
    fun prop(key: String, value: Long?) {
        obj.put(key, value)
    }
    fun prop(key: String, value: Int?) {
        obj.put(key, value)
    }
}

fun makeJsonObject(f: JsonObjectMaker.() -> Unit): ObjectNode {
    val mapper = jacksonObjectMapper()
    val obj = mapper.createObjectNode()
    f(JsonObjectMaker(obj))
    return obj
}