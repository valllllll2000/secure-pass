package com.vaxapp.passgen.domain.usecases

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

internal class CreatePasswordUseCase @Inject constructor() {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val upperCaseChars: List<Char> = 'A'.rangeTo('Z').toList()
    private val lowerCaseChars: List<Char> = 'a'.rangeTo('z').toList()
    private val numbers: List<Char> = '0'.rangeTo('9').toList()
    private val specialChars: List<Char> = "~!@#$%^&*()_-+={[}]|:;<,>.?/".toList()

    internal suspend operator fun invoke(passwordLength: Int): String {
        //min length passwordLength
        //min 1 uppercase
        //min 1 lowercase
        //min 1 special char
        //min one number
        return withContext(dispatcher) {
            val tempPass: StringBuilder = StringBuilder()
            for (i in 0..<passwordLength) {
                when (i) {
                    0 -> tempPass.append(upperCaseChars.random())
                    1 -> tempPass.append(numbers.random())
                    2 -> tempPass.append(specialChars.random())
                    3 -> tempPass.append(lowerCaseChars.random())
                    else -> {
                        when (Random.nextInt(0, 4)) {
                            0 -> tempPass.append(upperCaseChars.random())
                            1 -> tempPass.append(numbers.random())
                            2 -> tempPass.append(specialChars.random())
                            3 -> tempPass.append(lowerCaseChars.random())
                        }
                    }
                }
            }
            Log.d("Password", "original string: $tempPass")
            val toList = tempPass.toList()
            Log.d("Password", "after toList: $toList")
            val shuffled = toList.shuffled()
            Log.d("Password", "after shuffled: $shuffled")
            val toString = shuffled.joinToString("")
            Log.d("Password", "after toString: $toString")
            toString
        }
    }
}
