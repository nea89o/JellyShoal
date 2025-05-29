package moe.nea.jellyshoal.util

import java.io.File


fun joinLibraryPath(vararg paths: String?): String {
	var sumPath = ""
	for (path in paths) {
		if (path == null) continue
		path.split(File.pathSeparatorChar)
			.filter { it.isNotEmpty() }
			.forEach {
				if (sumPath.isNotEmpty())
					sumPath += File.pathSeparatorChar
				sumPath += it
			}
	}
	return sumPath
}

