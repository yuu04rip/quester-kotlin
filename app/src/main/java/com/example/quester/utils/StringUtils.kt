package com.example.quester.ui.utils

/**
 * Capitalizza la prima lettera di ogni parola
 * Es: "ciao mondo" → "Ciao Mondo"
 */
fun capitalizeWords(text: String): String {
    if (text.isBlank()) return text
    return text.split(" ")
        .joinToString(" ") { word ->
            if (word.isNotBlank()) {
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            } else {
                word
            }
        }
}

/**
 * Capitalizza solo la prima lettera della frase
 * Es: "ciao mondo" → "Ciao mondo"
 */
fun capitalizeFirstLetter(text: String): String {
    if (text.isBlank()) return text
    return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

/**
 * Capitalizza la prima lettera di ogni parola e mantiene il resto minuscolo
 * Es: "ciaO MOnDo" → "Ciao Mondo"
 */
fun capitalizeWordsProper(text: String): String {
    if (text.isBlank()) return text
    return text.split(" ")
        .joinToString(" ") { word ->
            if (word.isNotBlank()) {
                word.lowercase().replaceFirstChar { it.titlecase() }
            } else {
                word
            }
        }
}

/**
 * Capitalizza la prima lettera e mantiene il resto minuscolo
 * Es: "cIaO" → "Ciao"
 */
fun capitalizeFirstLetterProper(text: String): String {
    if (text.isBlank()) return text
    return text.lowercase().replaceFirstChar { it.titlecase() }
}