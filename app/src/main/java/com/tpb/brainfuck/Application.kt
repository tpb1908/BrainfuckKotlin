package com.tpb.brainfuck

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.support.annotation.StyleRes
import com.tpb.brainfuck.db.Database
import com.tpb.brainfuck.db.Program
import com.tpb.brainfuck.db.ProgramMigrations
import kotlin.concurrent.thread

/**
 * Created by theo on 01/07/17.
 */
class Application : Application() {

    companion object {
        lateinit var db: Database

        var themeId: Int = R.style.AppTheme
            private set
            @StyleRes get

        fun toggleTheme(context: Context) {
            if (themeId == R.style.AppTheme) {
                enableDarkTheme(context)
            } else {
                enableLightTheme(context)
            }
        }

        fun enableDarkTheme(context: Context) {
            themeId = R.style.AppTheme_Dark
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("darkTheme", true)
                    .apply()
        }

        fun enableLightTheme(context: Context) {
            themeId = R.style.AppTheme
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("darkTheme", false)
                    .apply()

        }

        private fun initDefaultPrograms() {
            thread {
                db.programDao().insertAll(defaults)
            }
        }

        fun restoreDefaultValues() {
            thread {
                val dao = db.programDao()
                defaults.reversed().forEach {
                    if (dao.getProgramIfExists(it.name, it.source) == null) {
                        dao.insert(it)
                    }
                }

            }
        }

        val defaults = listOf(Program(
                name = "QWERTY to DVORAK",
                description = "Converts QWERTY keycodes to DVORAK",
                source = "+>>>>>>>++[+<[>]>[<++>-]<]<[[>+>+<<-]>>-]>+++++[>+++++++<-]>[[<<+>>-]<<-]\n" +
                        "++++++[>++++++++++<-]>+<<<<<<<<<<++++++<<<<<<<+++++[<<+++>+>-]<++\n" +
                        "<[<<<<<<<+++++>>+++++>+>+++>>+++++>>+++++<-]<<<+<<--->--[[<<+>>-]<<-]>---<<<<-\n" +
                        "<++++[<<<<++>->+++++++>+>-]<<[<<+>+>>+>>+>>++>>+<<<<<<<-]<[>+<-]<<-\n" +
                        ">[[<<+>>-]<<-]<<<<++++++++++++[<<+>---->-]<<[[<<+>>-]<<-]+++[>---------<-]\n" +
                        "<<<<<<<<<<<<<<<<<+<++++[<<++++>>-]<<[<<<--->>>>->>-->>>>>>---<<<<<<<<<-]<<<--<<[\n" +
                        "    >>+>+++++++++++[<--->>>>---->>--->>--<<<<<<<-]>>>>>+>>+++\n" +
                        "    >>>+++++++[<->>---->>->>--<<<<<-]>+>>---->>>>+++++>>---->>-->\n" +
                        "    ++++++[>--------<-]>+>>---->>+++>>------------>>>>++>>+++++++++>>-->>------\n" +
                        "    >>---->>++>>+>+++++++[<++>>-<-]>>>+>>>+++++++[<+>>+++>>>>>>++++<<<<<<<-]>+\n" +
                        "    >>>>>>>>\n" +
                        "]>[<+>-]>[>>]<,[[[<<+>>-]<<-]>.[>>]<,]",
                memoryCapacity = 520
                ),
                Program(
                        name = "Factorial",
                        description = "Outputs arbitrarily many factorials",
                        source = ">++++++++++>>>+>+[>>>+[-[<<<<<[+<<<<<]>>[[-]>[<<+>+>-]<[>+<-]<[>+<-[>+<-[>\n" +
                                "+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>[-]>>>>+>+<<<<<<-[>+<-]]]]]]]]]]]>[<+>-\n" +
                                "]+>>>>>]<<<<<[<<<<<]>>>>>>>[>>>>>]++[-<<<<<]>>>>>>-]+>>>>>]<[>++<-]<<<<[<[\n" +
                                ">+<-]<<<<]>>[->[-]++++++[<++++++++>-]>>>>]<<<<<[<[>+>+<<-]>.<<<<<]>.>>>>]"
                ),
                Program(
                        name = "Squares",
                        description = "Outputs the squares of 0 to 100",
                        source = "++++[>+++++<-]>[<+++++>-]+<+[\n" +
                                "    >[>+>+<<-]++>>[<<+>>-]>>>[-]++>[-]+\n" +
                                "    >>>+[[-]++++++>>>]<<<[[<++++++++<++>>-]+<.<[>----<-]<]\n" +
                                "    <<[>>>>>[>>>[-]+++++++++<[>-<-]+++++++++>[-[<->-]+[<<<]]<[>+<-]>]<<-]<<-\n" +
                                "]",
                        memoryCapacity = 32
                ),
                Program(
                        name = "Fibonacci",
                        description = "Outputs the fibonacci sequence to 100",
                        source = "+++++++++++\n" +
                                ">+>>>>++++++++++++++++++++++++++++++++++++++++++++\n" +
                                ">++++++++++++++++++++++++++++++++<<<<<<[>[>>>>>>+>\n" +
                                "+<<<<<<<-]>>>>>>>[<<<<<<<+>>>>>>>-]<[>++++++++++[-\n" +
                                "<-[>>+>+<<<-]>>>[<<<+>>>-]+<[>[-]<[-]]>[<<[>>>+<<<\n" +
                                "-]>>[-]]<<]>>>[>>+>+<<<-]>>>[<<<+>>>-]+<[>[-]<[-]]\n" +
                                ">[<<+>>[-]]<<<<<<<]>>>>>[+++++++++++++++++++++++++\n" +
                                "+++++++++++++++++++++++.[-]]++++++++++<[->-<]>++++\n" +
                                "++++++++++++++++++++++++++++++++++++++++++++.[-]<<\n" +
                                "<<<<<<<<<<[>>>+>+<<<<-]>>>>[<<<<+>>>>-]<-[>>.>.<<<\n" +
                                "[-]]<<[>>+>+<<<-]>>>[<<<+>>>-]<<[<+>-]>[<+>-]<<<-]"
                ),
                Program(
                        name = "ROT 13",
                        description = "Shift characters by 13 places",
                        source = "-,+[                         \n" +
                                "    -[                       \n" +
                                "        >>++++[>++++++++<-]  \n" +
                                "                             \n" +
                                "        <+<-[                \n" +
                                "            >+>+>-[>>>]      \n" +
                                "            <[[>+<-]>>+>]    \n" +
                                "            <<<<<-           \n" +
                                "        ]                    \n" +
                                "    ]>>>[-]+                 \n" +
                                "    >--[-[<->[-]]]<[         \n" +
                                "        ++++++++++++<[       \n" +
                                "                             \n" +
                                "            >-[>+>>]         \n" +
                                "            >[+[<+>-]>+>>]   \n" +
                                "            <<<<<-           \n" +
                                "        ]                    \n" +
                                "        >>[<+>-]             \n" +
                                "        >[                   \n" +
                                "            -[               \n" +
                                "                -<<[-]>>     \n" +
                                "            ]<<[<<->>-]>>    \n" +
                                "        ]<<[<<+>>-]          \n" +
                                "    ]                        \n" +
                                "    <[-]                     \n" +
                                "    <.[-]                    \n" +
                                "    <-,+                     \n" +
                                "]",
                        minVal = -1E6.toInt(),
                        outSuffix = "\n"
                ),
                Program(
                        name = "Hello world",
                        description = "Prints \"Hello world\"",
                        source = "++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.>++.",
                        memoryCapacity = 8
                ))
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, Database::class.java, "bfdb")
                .addMigrations(ProgramMigrations.Migration_1_2, ProgramMigrations.Migration_2_3, ProgramMigrations.Migration_3_4)
                .build()

        val sp = getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        if (sp.getBoolean("firstRun", true)) {
            sp.edit().putBoolean("firstRun", false).apply()
            initDefaultPrograms()
        }
        if (sp.getBoolean("darkTheme", false)) {
            themeId = R.style.AppTheme_Dark
        }

    }

}