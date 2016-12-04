package spm.view.password

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import spm.view.add
import spm.view.elem
import spm.view.form.*
import spm.view.modal.Notify

/**
 * User: rnentjes
 * Date: 4-12-16
 * Time: 13:29
 */

object GeneratePasswordView {

    fun create(password: Password, settings: PasswordSettings): Element {
        return Form.create(FormType.HORIZONTAL)
          .add {
              Input.create(
                "password_length",
                type = "number",
                label = "Pasword length",
                labelWidth = 4,
                value = "${settings.length}",
                blur = { it ->
                    val target = it.target
                    if (target is HTMLInputElement) {
                        try {
                            var length = parseInt(target.value)
                            if (length < 1) {
                                length = 1
                            }

                            settings.length = length

                            if (length < 12) {
                                Notify.show("Password length of $length seems a but short...", "warning")
                            }
                        } catch(e: Exception) {
                            Notify.show("Length must be a number!", "error")
                        }
                    }
                },
                change = { it ->
                    val target = it.target
                    if (target is HTMLInputElement) {
                        try {
                            var length = parseInt(target.value)
                            if (length < 1) {
                                length = 1
                            }

                            settings.length = length
                        } catch(e: Exception) {
                            Notify.show("Length must be a number!", "error")
                        }
                    }
                }
              )
          }
          .add {
            Checkbox.create(
              "generate_numbers",
              "Numbers 0..9",
              labelWidth = 4,
              inputWidth = 8,
              checked = settings.numbers,
              click = { it ->
                  settings.numbers = !settings.numbers
              }
            )
        }.add {
            Checkbox.create(
              "generate_special",
              "Special !@#$'<`~ etc.",
              labelWidth = 4,
              inputWidth = 8,
              checked = settings.special,
              click = { it ->
                  settings.special = !settings.special
              }
            )
        }.add {
            Input.create(
              "generate_password_id",
              label = "Generated password:",
              labelWidth = 4,
              inputWidth = 7,
              value = password.password1,
              change = { it ->
                  val target = it.target
                  if (target is HTMLInputElement) {
                      password.password1 = target.value
                      password.password2 = target.value
                  }
              }).add {
                IconButton.create("refresh") { e ->
                    val generatedPassword = generatePassword(settings)

                    val input = elem("generate_password_id") as HTMLInputElement
                    input.value = generatedPassword
                    password.password1 = generatedPassword
                    password.password2 = generatedPassword
                }
            }
        }
    }

    val basic = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val special = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%*()_+-={}[]:\"|;'\\<>?,./"
    val specialNumbers = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%*()_+-={}[]:\"|;'\\<>?,./0123456789"

    private fun generatePassword(settings: PasswordSettings): String {
        val random = Math.random()
        val builder = StringBuilder()
        val source: String
        var select = 0

        if (settings.numbers) { select += 1 }
        if (settings.special) { select += 2 }

        when(select) {
            0 -> { source = basic }
            1 -> { source = numbers }
            2 -> { source = special }
            3 -> { source = specialNumbers }
            else -> {
                source = specialNumbers
            }
        }

        for (index in 0..settings.length-1) {
            builder.append(source[(source.length * Math.random()).toInt()])
        }

        return builder.toString()
    }

}