package com.tms.csp.parse

import com.tms.csp.ast.PLConstants
import com.tms.csp.ast.PLConstants.*

class Head : PLConstants {
    companion object {


        fun isNegated(lit: Int): Boolean {
            return lit < 1
        }

        fun getVarId(lit: Int): Int {
            return Math.abs(lit)
        }

        fun getSign(lit: Int): Boolean {
            return lit >= 0
        }

        fun getSign(head: String): Boolean {
            return !isNegated(head)
        }

        fun getCode(head: Int): String {
            return Math.abs(head).toString() + ""
        }

        fun isNegated(head: String): Boolean {
            return head[0] == BANG
        }

        fun getVarCode(head: String): String {
            return if (isNegated(head)) {
                head.substring(1)
            } else {
                head
            }
        }

        fun isConstantTrue(head: String): Boolean {
            return head == TRUE_TOKEN
        }

        fun isConstantFalse(head: String): Boolean {
            return head == FALSE_TOKEN
        }
    }


}
