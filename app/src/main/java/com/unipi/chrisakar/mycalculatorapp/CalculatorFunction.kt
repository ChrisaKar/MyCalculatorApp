package com.unipi.chrisakar.mycalculatorapp

class CalculatorFunction {
    var operand1 = "0";
    var operand2 = "";
    var operator = ' '
    var lastIsDecimalPoint = false;
    var lastIsOperator = false
    var totalText = "";

    fun String.fullTrim() = trim().replace("\uFEFF", "")

    fun operatorFromChar(charOperator: Char):(Double, Double)->Double
    {
        return when(charOperator)
        {
            '+'->{a,b->a+b}
            '-'->{a,b->a-b}
            '/'->{a,b->a/b}
            '*'->{a,b->a*b}
            else -> throw Exception("That's not a supported operator")
        }
    }

    fun CheckLastInput(CurrentInput: Char): String {
        when (CurrentInput) {
            '.' -> {
                if (operand2 != "") {
                    if (!operand2.contains('.')) {
                        operand2 += '.'
                        totalText += '.'
                    }
                } else if (operand1 != "" && !lastIsOperator) {
                    if (!operand1.contains('.')) {
                        operand1 += '.'
                        totalText += '.'
                    }
                } else {
                    operand1 += "0."
                    totalText += "0."
                }
                lastIsOperator = false
                lastIsDecimalPoint = true
            }
            '+', '-', '*', '/' -> {

                if (lastIsDecimalPoint) {
                    totalText = totalText.dropLast(1)
                    if (operand2 != "") {
                        operand2 = operand2.dropLast(1)
                    } else if (operand1 != "") {
                        operand1 = operand1.dropLast(1)
                    }
                }

                if (operand1 != "" && operand2 != "") {
                    if (operand2.fullTrim().toDouble() == 0.0 && operator == '/') {
                        // error here
                    } else {
                        val number = operatorFromChar(operator).invoke(operand1.fullTrim().toDouble(), operand2.fullTrim().toDouble())
                        operand1 = number.toString()
                        operand2 = ""
                        operator = CurrentInput
                        if (number == number.toInt().toDouble()) {
                            operand1 = number.toInt().toString()
                        }
                        totalText = operand1 + operator
                    }
                } else if (operand1 != "") {
                    operator = CurrentInput
                    totalText = operand1 + operator
                }
                lastIsOperator = true
                lastIsDecimalPoint = false
            }
            '=' -> {
                if (operand1 != "" && operand2 != "" && operator != ' ') {
                    if (lastIsDecimalPoint) {
                        totalText = totalText.dropLast(1)
                        if (operand2 != "") {
                            operand2 = operand2.dropLast(1)
                        } else if (operand1 != "") {
                            operand1 = operand1.dropLast(1)
                        }
                    }

                    if (operand2.fullTrim().toDouble() == 0.0 && operator == '/') {
                        // error here
                    } else {

                        val number = operatorFromChar(operator).invoke(operand1.fullTrim().toDouble(), operand2.fullTrim().toDouble())

                        operand1 = number.toString()
                        operand2 = ""
                        operator = ' '
                        if (number == number.toInt().toDouble()) {
                            operand1 = number.toInt().toString()
                        }
                        totalText = operand1
                    }
                }

            }
            '~' -> {
                totalText = totalText.dropLast(1);
                if (operand2 != "") {
                    operand2 = operand2.dropLast(1)
                } else if (lastIsOperator) {
                    lastIsOperator = false
                    operator = ' '
                } else if (lastIsDecimalPoint) {
                    lastIsDecimalPoint = false
                } else if (operand1 != "") {
                    operand1 = operand1.dropLast(1)
                }
                if (!totalText.equals("")){
               when (totalText.last()) {
                   '+',  '-', '*', '/' -> {
                       lastIsOperator = true
                       operator = totalText.last()
               } '.' -> {
                   lastIsDecimalPoint = true
               }
               } }

            }
            else -> {

                totalText += CurrentInput

                if (operand2 != "" || (lastIsOperator && operand1 != "")) {
                    operand2 += CurrentInput
                } else {
                    operand1 += CurrentInput
                }
                lastIsDecimalPoint = false
                lastIsOperator = false;
            }

        }
        return totalText
    }
}



