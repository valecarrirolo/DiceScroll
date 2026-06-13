package com.github.valecarrirolo.dicescroll.data.model

enum class DiceType(val maxVal: Int, val displayName: String, val colorHex: String) {
    D4(4, "D4", "#FF4757"),     // Coral Red
    D6(6, "D6", "#FFA502"),     // Coral Orange
    D8(8, "D8", "#ECCC68"),     // Golden Yellow
    D10(10, "D10", "#2ED573"),   // Bright Green
    D12(12, "D12", "#1E90FF"),   // Clear Blue
    D20(20, "D20", "#9B5DE5"),   // Vibrant Purple
    D100(100, "D100", "#F15BB5")  // Neon Pink
}
