package com.tms.csp;

import com.google.common.base.Splitter
import com.tms.csp.parse.ParseUtil
import java.util.*

private val SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings()

object Vars {

    const val HEAD_VARS_LINE = "vars("
    const val HEAD_INV_VARS_LINE = "invVars("
    const val HEAD_DONTCARES_LINE = "dontCares("
    const val FOOT = ")"

    @JvmStatic
    fun isVarsLine(line: String?): Boolean = if (line == null)
        false
    else
        line.startsWith(HEAD_VARS_LINE) && line.endsWith(FOOT)


    @JvmStatic
    fun isDontCaresLine(line: String): Boolean = line.startsWith(HEAD_DONTCARES_LINE) && line.endsWith(FOOT)

    @JvmStatic
    fun isInvVarsLine(line: String): Boolean = line.startsWith(HEAD_INV_VARS_LINE) && line.endsWith(FOOT)

    @JvmStatic
    fun parseVarsLine(varsLine: String?): Iterable<String> {
        return if (varsLine == null)
            emptyList<String>()
        else
            parseVarsLineInternal(varsLine, HEAD_VARS_LINE)
    }

    @JvmStatic
    fun parseDontCaresLine(varsLine: String): Set<String> {
        return parseVarsLineInternal(varsLine, HEAD_DONTCARES_LINE).toSet()
    }


    @JvmStatic
    fun isVarsOrInvVarsLine(line: String): Boolean = isVarsLine(line) || isInvVarsLine(line)


//
//    @JvmStatic
//    fun parseVarsLineOrNull(lines: Sequence<String>): Set<String>? {
//        return when {
//            lines.isEmpty() -> null
//            isVarsLine(lines[0]) -> parseVarsLine(lines[0])
//            else -> null
//        }
//    }
//
//    @JvmStatic
//    fun parseInvVarsLineOrNull(lines: Sequence<String>): Set<Int>? {
//        return when {
//            lines.sisize < 2 -> null
//            isInvVarsLine(lines[1]) -> parseInvVarsLine(lines[1])
//            else -> null
//        }
//    }


    @JvmStatic
    fun parseInvVarsLine(line: String): SortedSet<Int> {
        return parseVarsLineInternal(line, HEAD_INV_VARS_LINE).map { it.toInt() }.toSortedSet()
    }


    private fun fixVarsLine(varsLine: String): String {
        return varsLine
                .replace(", ".toRegex(), " ")
                .replace(",".toRegex(), " ")
                .replace(" {2}".toRegex(), " ").trim()
    }

    fun parseVarsLineInternal(varsLine: String, head: String): Iterable<String> {
        val varsLineFixed = fixVarsLine(varsLine)
        val argList = ParseUtil.stripHeadFootFromLine(varsLineFixed, head, FOOT)
        return parseVarList(argList)
    }

    @JvmStatic
    fun parseVarList(argList: String): Iterable<String> {
        return SPLITTER.split(argList)
    }

}

