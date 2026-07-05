package com.example

import org.junit.Test
import java.io.File
import javax.imageio.ImageIO

class ImageHeaderInspectTest {
    @Test
    fun inspectAllHeaders() {
        println("=== INSPECTING GENERATED PORTRAIT TABLET SCREENSHOTS ===")
        val dir = File("src/test/screenshots").absoluteFile
        if (!dir.exists()) {
            println("Dir not found: ${dir.path}")
            return
        }
        val files = dir.listFiles() ?: emptyArray()
        files.sortBy { it.name }
        for (f in files) {
            if (f.name.startsWith("tablet_10_") && f.name.endsWith(".png")) {
                try {
                    val img = ImageIO.read(f)
                    if (img != null) {
                        val w = img.width
                        val h = img.height
                        val ratio = w.toDouble() / h.toDouble()
                        println("  ${f.name} | Size: ${f.length()} bytes | Resolution: ${w}x${h} | Aspect Ratio: %.4f".format(ratio))
                    } else {
                        println("  ${f.name} | Failed to read as image")
                    }
                } catch (e: Exception) {
                    println("  ${f.name} | Error: ${e.message}")
                }
            }
        }
    }
}
