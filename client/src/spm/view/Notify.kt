package spm.view

/**
 * User: rnentjes
 * Date: 22-4-17
 * Time: 15:00
 */

object Notify {

    /**
     * type:
     * - success
     * - info
     * - warning
     * - error
     */
    fun show(message: String, type: String = "") {
        js("$.notify(message, type)")
    }

}
