package com.example.quester.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle

class FantasyBoardButtonStyle : ButtonStyle {

    // Implementazione principale per il bottone standard riempito (filled)
    @Composable
    override fun getButtonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = FantasyBoardWood,             // Colore di sfondo del bottone attivo (Legno medio)
            contentColor = FantasyBoardParchment,          // Colore del testo e delle icone sul bottone attivo (Pergamena)
            disabledContainerColor = FantasyBoardWoodDark, // Colore di sfondo attenuato quando il bottone non è cliccabile
            disabledContentColor = FantasyBoardParchmentDark // Colore del testo disabilitato, per un contrasto ridotto
        )
    }

    // Implementazione strutturata per il bottone delineato (con perimetro in evidenza e sfondo trasparente)
    @Composable
    override fun getOutlinedButtonColors(): ButtonColors {
        return ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,            // Sfondo strettamente trasparente tipico dei bottoni outlined
            contentColor = FantasyBoardGoldLeaf,           // Testo dorato brillante per risaltare sul background scuro dell'app
            disabledContainerColor = Color.Transparent,
            disabledContentColor = FantasyBoardParchmentDark
        )
    }

    // Implementazione essenziale per i bottoni testuali (privi di bordi e di sfondi volumetrici)
    @Composable
    override fun getTextButtonColors(): ButtonColors {
        return ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = FantasyBoardParchment,          // Testo color pergamena standard ad alta leggibilità
            disabledContainerColor = Color.Transparent,
            disabledContentColor = FantasyBoardParchmentDark
        )
    }

    // Definizione morfologica della forma geometrica del componente interattivo
    @Composable
    override fun getButtonShape(): Shape {
        // Eredita la forma arrotondata standard definita centralmente nel MaterialTheme globale,
        // garantendo una coerenza strutturale con il resto dell'applicazione.
        return MaterialTheme.shapes.medium
    }

    // Definizione gerarchica dello stile tipografico applicato ai testi dei bottoni
    @Composable
    override fun getTextStyle(): TextStyle {
        // Applica lo stile tipografico ottimizzato per etichette (labelLarge),
        // che solitamente include il grassetto e una spaziatura adatta all'interazione.
        return MaterialTheme.typography.labelLarge
    }

    // Gestione del modificatore dinamico per applicare animazioni o effetti grafici vincolati agli eventi di pressione
    override fun getButtonModifier(pressed: Boolean, enabled: Boolean): Modifier {
        // In questa implementazione di base restituiamo un Modifier vuoto. La funzione esiste per
        // l'eventuale integrazione futura di effetti tattili complessi, variazioni di scala o ombre dinamiche (elevation).
        return Modifier
    }

    // Restituzione della codifica cromatica associata ad avvisi critici o errori semantici
    @Composable
    override fun getErrorTextColor(): Color {
        return FantasyBoardRedWax // Utilizza la tonalità profonda della cera lacca rossa definita per il tema
    }

    // Restituzione della codifica cromatica associata ad azioni positive o completamenti con successo
    @Composable
    override fun getSuccessTextColor(): Color {
        return FantasyBoardGoldLeaf // Impiega l'oro per simboleggiare chiaramente la risoluzione positiva o la ricompensa
    }
}