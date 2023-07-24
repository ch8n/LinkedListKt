package com.github.ch8n.matrix.core

import com.github.ch8n.matrix.MatrixIterator


/***
 * Fixed array doesn't have ability to add or remove elements
 * it can only set and get data from array
 */
interface MatrixOperations<T> : Iterable<T> {
    val rowsCount: Int
    val columnsCount: Int
    fun rows(row: Int): Array<T>
    fun columns(columns: Int): Array<T>
    fun get(row: Int, col: Int): T
    fun set(row: Int, col: Int, value: T)
}

interface MatrixOpsAdvance<T> : MatrixOperations<T> {
    // advance
    fun plus(matrix: Matrix<T>)
    fun minus(matrix: Matrix<T>)
    fun cross(matrix: Matrix<T>)
    fun dot(matrix: Matrix<T>)
    fun transpose(matrix: Matrix<T>)
    fun inverse(matrix: Matrix<T>)
}

val <T> MatrixOperations<T>.size get() = Pair(rowsCount, columnsCount)

class Matrix<T> private constructor(
    override val rowsCount: Int,
    override val columnsCount: Int,
    initializer: (row: Int, col: Int) -> T
) : MatrixOperations<T> {

    private val matrix = Array(rowsCount) { rowIndex ->
        Array(columnsCount) { columnIndex ->
            initializer.invoke(rowIndex, columnIndex) as Any
        }
    }

    private fun checkRange(row: Int, col: Int) {
        val inValidRange = row in (0 until rowsCount) && col in (0 until columnsCount)
        if (!inValidRange) {
            throw IndexOutOfBoundsException("$row out of $rowsCount or $col out of $columnsCount")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun rows(row: Int): Array<T> {
        checkRange(row, 0)
        return matrix.get(row) as Array<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun columns(columns: Int): Array<T> {
        checkRange(0, columns)
        return (0 until rowsCount)
            .map { it to columns }
            .map { matrix.get(it.first).get(it.second) }
            .toTypedArray() as Array<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(row: Int, col: Int): T {
        checkRange(row, col)
        return matrix.get(row).get(col) as T
    }

    override fun iterator(): Iterator<T> {
        return MatrixIterator(
            rows = rowsCount,
            columns = columnsCount,
            getItemAt = ::get
        )
    }

    inline fun onEach(action: (rowIndex: Int, columnIndex: Int, value: T) -> Unit) {
        (0 until rowsCount).forEach { rowIndex ->
            (0 until columnsCount).forEach { columnIndex ->
                action.invoke(rowIndex, columnIndex, get(rowIndex, columnIndex))
            }
        }
    }

    override fun set(row: Int, col: Int, value: T) {
        checkRange(row, col)
        matrix.get(row).set(col, value as Any)
    }

    override fun toString(): String {
        return buildString {
            matrix.forEach { column ->
                column.forEach { element ->
                    append(" ~ $element ~")
                }
                append("\n")
            }
        }
    }

    companion object {
        fun <T> of(rows: Int, columns: Int, initializer: (row: Int, col: Int) -> T): Matrix<T> =
            Matrix(rows, columns, initializer)
    }
}

fun <T> matrixOf(rows: Int, columns: Int, initializer: (row: Int, col: Int) -> T): Matrix<T> =
    Matrix.of(rows, columns, initializer)