package nau.android.taskify.ui.eisenhowerMatrix

import nau.android.taskify.ui.model.EisenhowerMatrixModel

sealed class MatrixState {

    class Success(val matrixModel: EisenhowerMatrixModel) : MatrixState()

    class Error(val throwable: Throwable) : MatrixState()

    object Loading : MatrixState()

}