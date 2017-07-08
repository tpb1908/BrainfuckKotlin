package com.tpb.brainfuck.runner

/**
 * Created by theo on 07/07/17.
 */
class Formatter {

    companion object {

        fun format(source: String, indent: Int = 2): String {
            val formatted = StringBuilder()
            var indentLevel = 0
            var prevChar: Char = 0.toChar()
            source.forEachIndexed { i, c ->
                if (c == '[') {
                    if (prevChar != c) { //We don't want empty lines between sequential [
                        formatted.append('\n')
                        repeat(indentLevel * indent, { formatted.append(' ')})
                    }

                    formatted.append("[\n")
                    indentLevel++
                    repeat(indentLevel * indent, { formatted.append(' ')})
                } else if (c == ']') {
                    indentLevel--
                    if (prevChar != c && source[minOf(i + 1, source.length - 1)] != '\n') { //We don't want empty lines between sequential ]
                        formatted.append('\n')
                        repeat((indentLevel) * indent, { formatted.append(' ')})
                    }
                    formatted.append("]\n")
                    repeat((indentLevel-1) * indent, { formatted.append(' ')})
                }  else {
                    formatted.append(c)
                    if (c == '\n' ) { repeat(indentLevel * indent, { formatted.append(' ')}) }
                }
                prevChar = c
            }
            return formatted.toString()
        }

    }



}