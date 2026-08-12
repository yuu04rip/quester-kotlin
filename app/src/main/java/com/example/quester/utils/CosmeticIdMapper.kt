package com.example.quester.utils

import com.example.quester.ui.components.FrameType
import com.example.quester.ui.components.HatType
import com.example.quester.ui.components.WeaponType

/**
 * Mappa gli ID dei cosmetici tra Shop e Avatar.
 */
object CosmeticIdMapper {

    // ===== HAT / COPRICAPO =====
    fun hatToShopId(hat: HatType): String? {
        return when (hat) {
            HatType.NONE -> null
            HatType.MAGO -> "hat_mago"
            HatType.ELMO_CAVALIERE -> "elmo_cavaliere"
            HatType.VISOR_FUTURISTICO -> "visor_futuristico"
        }
    }

    fun shopIdToHat(shopId: String?): HatType? {
        if (shopId.isNullOrBlank()) return HatType.NONE
        return when (shopId) {
            "hat_mago" -> HatType.MAGO
            "elmo_cavaliere" -> HatType.ELMO_CAVALIERE
            "visor_futuristico" -> HatType.VISOR_FUTURISTICO
            else -> null
        }
    }

    fun parseHatType(value: String?): HatType {
        if (value.isNullOrBlank() || value.equals("NONE", ignoreCase = true)) return HatType.NONE

        // 1. Prova da ID dello Shop
        shopIdToHat(value)?.let { if (it != HatType.NONE) return it }

        // 2. Prova da nome Enum
        return HatType.entries.find {
            it.name.equals(value, ignoreCase = true)
        } ?: HatType.NONE
    }

    // ===== WEAPON / ARMA =====
    fun weaponToShopId(weapon: WeaponType): String? {
        return when (weapon) {
            WeaponType.NONE -> null
            WeaponType.STAFF -> "staff_mago"
            WeaponType.SWORD -> "sword_cavaliere"
            WeaponType.GUN -> "gun_spaziale"
        }
    }

    fun shopIdToWeapon(shopId: String?): WeaponType? {
        if (shopId.isNullOrBlank()) return WeaponType.NONE
        return when (shopId) {
            "staff_mago" -> WeaponType.STAFF
            "sword_cavaliere" -> WeaponType.SWORD
            "gun_spaziale" -> WeaponType.GUN
            else -> null
        }
    }

    fun parseWeaponType(value: String?): WeaponType {
        if (value.isNullOrBlank() || value.equals("NONE", ignoreCase = true)) return WeaponType.NONE

        // 1. Prova da ID dello Shop
        shopIdToWeapon(value)?.let { if (it != WeaponType.NONE) return it }

        // 2. Prova da nome Enum
        return WeaponType.entries.find {
            it.name.equals(value, ignoreCase = true)
        } ?: WeaponType.NONE
    }

    // ===== FRAME / CORNICE =====
    fun frameToShopId(frame: FrameType): String? {
        return when (frame) {
            FrameType.NONE -> null
            FrameType.MAGO -> "frame_mago"
            FrameType.CAVALIERE -> "frame_cavaliere"
            FrameType.SCI_FI -> "frame_scifi"
        }
    }

    fun shopIdToFrame(shopId: String?): FrameType? {
        if (shopId.isNullOrBlank()) return FrameType.NONE
        return when (shopId) {
            "frame_mago" -> FrameType.MAGO
            "frame_cavaliere" -> FrameType.CAVALIERE
            "frame_scifi" -> FrameType.SCI_FI
            else -> null
        }
    }

    fun parseFrameType(value: String?): FrameType {
        if (value.isNullOrBlank() || value.equals("NONE", ignoreCase = true)) return FrameType.NONE

        // 1. Prova da ID dello Shop
        shopIdToFrame(value)?.let { if (it != FrameType.NONE) return it }

        // 2. Prova da nome Enum
        return FrameType.entries.find {
            it.name.equals(value, ignoreCase = true)
        } ?: FrameType.NONE
    }
}
