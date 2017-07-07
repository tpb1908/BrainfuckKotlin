package com.tpb.brainfuck.runner

/**
 * Created by theo on 07/07/17.
 */
class Formatter {

    companion object {

        fun format(source: String, indent: Int = 2): String {
            val formatted = StringBuilder()
            var indentLevel = 0
            var previousChar: Char = 0.toChar()
            source.forEach {
                if (it == '[') {
                    if (previousChar != it) { //We don't want empty lines between sequential [
                        formatted.append('\n')
                        repeat(indentLevel * indent, { formatted.append(' ')})
                    }

                    formatted.append("[\n")
                    indentLevel++
                    repeat(indentLevel * indent, { formatted.append(' ')})
                } else if (it == ']') {
                    indentLevel--
                    if (previousChar != it) { //We don't want empty lines between sequential ]
                        formatted.append('\n')
                        repeat((indentLevel) * indent, { formatted.append(' ')})
                    }
                    formatted.append("]\n")
                    repeat((indentLevel-1) * indent, { formatted.append(' ')})
                }  else {
                    formatted.append(it)
                    if (it == '\n' ) { repeat(indentLevel * indent, { formatted.append(' ')}) }
                }
                previousChar = it

            }
            return formatted.toString()
        }

    }



}