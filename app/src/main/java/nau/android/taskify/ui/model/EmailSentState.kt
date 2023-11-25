package nau.android.taskify.ui.model

sealed class EmailSentState {

    object EmailSent : EmailSentState()

    class Error(val message: String) : EmailSentState()
}